package com.ashalmawia.coriolan.ui.main.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashalmawia.coriolan.data.logbook.Logbook
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.learning.TodayManager
import com.ashalmawia.coriolan.learning.exercise.CardAction
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.Domain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTime

class StatisticsViewModel(domainId: Long, private val repository: Repository, private val logbook: Logbook) : ViewModel() {
    private val today = TodayManager.today()

    private val domain: Domain by lazy {
        repository.domainById(domainId)!!
    }

    private val _currentState = MutableLiveData<StatisticsViewState>()
    val currentState: LiveData<StatisticsViewState> = _currentState

    private var lastData: StatisticsViewState.Data? = null

    fun initialize() {
        fetchData(StatisticsDateRange.LastWeek)
    }

    fun onDateRangeChanged(range: StatisticsDateRange) {
        fetchData(range)
    }

    private fun buildDateRange(range: StatisticsDateRange): DateRange {
        return when (range) {
            StatisticsDateRange.LastMonth -> DateRange(today.minusWeeks(1), today)
            StatisticsDateRange.LastWeek -> DateRange(today.minusMonths(1), today)
            StatisticsDateRange.LastYear -> DateRange(today.minusYears(1), today)
        }
    }

    private fun fetchData(range: StatisticsDateRange) {
        _currentState.postValue(StatisticsViewState.Loading)
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                fetchDataSync(range)
            }
        }
    }

    private fun fetchDataSync(range: StatisticsDateRange) {
        val (from, to) = buildDateRange(range)
        val state = lastData?.copy(
                rangeFrom = from,
                rangeTo = to,
                rangeValue = range,
                cardsLearntByDayData = extractCardsLearntByDayData(from, to)
        ) ?: StatisticsViewState.Data(
                rangeFrom = from,
                rangeTo = to,
                rangeValue = range,
                learningProgressData = extractCardByLearningProgressData(),
                cardsLearntByDayData = extractCardsLearntByDayData(from, to),
                cardsAddedByDayData = extractCardsAddedByDayData()
        )
        lastData = state
        _currentState.postValue(state)
    }

    private val allDecks: List<Deck> by lazy {
        repository.allDecks(domain)
    }
    private val allCards: List<Card> by lazy {
        repository.allCards(domain)
    }

    private fun extractCardsLearntByDayData(from: DateTime, to: DateTime): Map<DateTime, Int> {
        val data = logbook.cardsStudiedOnDateRange(from, to, allDecks)
        return data.mapValues { pair ->
            pair.value.filter {
                it.key == CardAction.NEW_CARD_FIRST_SEEN || it.key == CardAction.CARD_REVIEWED
            }.values.sum()
        }
    }

    private fun extractCardsAddedByDayData(): Map<DateTime, Int> {
        return allCards.groupBy { it.dateAdded }.mapValues { it.value.size }
    }

    private fun extractCardByLearningProgressData(): Map<Status, Int> {
        val learningProgress = repository.getProgressForCardsWithOriginals(allCards.map { it.id })
        return learningProgress.values.groupBy { it.status }.mapValues { it.value.count() }.toSortedMap()
    }
}

private data class DateRange(val from: DateTime, val to: DateTime)
