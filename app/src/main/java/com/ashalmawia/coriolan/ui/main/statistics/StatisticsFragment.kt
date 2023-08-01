package com.ashalmawia.coriolan.ui.main.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ashalmawia.coriolan.databinding.StatisticsBinding
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.ui.BaseFragment
import com.ashalmawia.coriolan.ui.util.activityViewModelBuilder
import org.koin.android.ext.android.get

private const val ARGUMENT_DOMAIN_ID = "domain_id"

class StatisticsFragment : BaseFragment() {

    companion object {
        fun create(domain: Domain): StatisticsFragment {
            val arguments = Bundle().also {
                it.putLong(ARGUMENT_DOMAIN_ID, domain.id)
            }
            return StatisticsFragment().also { it.arguments = arguments }
        }
    }

    private val viewModel: StatisticsViewModel by activityViewModelBuilder {
        val domainId = requireArguments().getLong(ARGUMENT_DOMAIN_ID)
        StatisticsViewModel(domainId, get(), get())
    }

    private lateinit var statisticsView: StatisticsView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = StatisticsBinding.inflate(layoutInflater, container, false)
        statisticsView = StatisticsViewImpl(binding, this, viewModel)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.initialize()
    }

    override fun onStop() {
        super.onStop()
        statisticsView.hideLoading()
    }
}