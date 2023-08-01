package com.ashalmawia.coriolan.ui.main.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashalmawia.coriolan.data.logbook.Logbook
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.learning.exercise.CardAction
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.util.midnight
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTime

class StatisticsViewModel(
        private val domainId: Long,
        private val repository: Repository,
        private val logbook: Logbook
) : ViewModel() {
    private val today = DateTime.now().midnight()

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
            StatisticsDateRange.LastWeek -> DateRange(today.minusWeeks(1), today)
            StatisticsDateRange.LastMonth -> DateRange(today.minusMonths(1), today)
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
        val domain = domain()
        val allDecks = allDecks(domain)

        val (from, to) = buildDateRange(range)
        val lastData = lastData

        val state = if (lastData != null) {
            lastData.copy(
                    rangeFrom = from,
                    rangeTo = to,
                    rangeValue = range,
                    cardsLearntByDayData = extractCardsLearntByDayData(from, to, allDecks)
            )
        } else {
            val allCards = allCards(domain)
            StatisticsViewState.Data(
                    rangeFrom = from,
                    rangeTo = to,
                    rangeValue = range,
                    learningProgressData = extractCardByLearningProgressData(allCards),
                    cardsLearntByDayData = extractCardsLearntByDayData(from, to, allDecks),
                    cardsAddedByDayData = extractCardsAddedByDayData(allCards)
            )
        }
        this.lastData = state
        _currentState.postValue(state)
    }

    private fun domain(): Domain = repository.domainById(domainId)!!
    private fun allDecks(domain: Domain): List<Deck> = repository.allDecks(domain)
    private fun allCards(domain: Domain): List<Card> = repository.allCards(domain)

    private fun extractCardsLearntByDayData(from: DateTime, to: DateTime, allDecks: List<Deck>): Map<DateTime, Int> {
        val data = logbook.cardsStudiedOnDateRange(from, to, allDecks)
        return data.mapKeys { it.key.midnight() }.mapValues { pair ->
            pair.value.filter {
                it.key == CardAction.NEW_CARD_FIRST_SEEN || it.key == CardAction.CARD_REVIEWED
            }.values.sum()
        }
    }

    private fun extractCardsAddedByDayData(allCards: List<Card>): Map<DateTime, Int> {
        return allCards.groupBy { it.dateAdded.midnight() }.mapValues { it.value.size }
    }

    private fun extractCardByLearningProgressData(allCards: List<Card>): Map<Status, Int> {
        val learningProgress = repository.getProgressForCardsWithOriginals(allCards.map { it.original.id })
        return learningProgress.values.groupBy { it.status }.mapValues { it.value.count() }.toSortedMap()
    }
}

private data class DateRange(val from: DateTime, val to: DateTime)