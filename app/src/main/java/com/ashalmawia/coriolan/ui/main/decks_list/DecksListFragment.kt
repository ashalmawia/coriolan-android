package com.ashalmawia.coriolan.ui.main.decks_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.databinding.LearningBinding
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.ui.BaseFragment
import com.ashalmawia.coriolan.ui.main.DomainActivity
import com.ashalmawia.coriolan.ui.util.activityViewModelBuilder
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject

private const val ARGUMENT_DOMAIN_ID = "domain_id"

class DecksListFragment : BaseFragment() {

    companion object {
        fun create(domain: Domain): DecksListFragment {
            val arguments = Bundle().also {
                it.putLong(ARGUMENT_DOMAIN_ID, domain.id)
            }
            return DecksListFragment().also { it.arguments = arguments }
        }
    }

    private lateinit var decksListView: DecksListView

    private val repository: Repository by inject()

    private val viewModel: DecksListViewModel by activityViewModelBuilder {
        DecksListViewModel(requireArguments().getLong(ARGUMENT_DOMAIN_ID), repository, get(), get())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = LearningBinding.inflate(inflater, container, false)
        decksListView = DecksListViewImpl(binding, this, viewModel)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        viewModel.bind(this.decksListView)

        reportFragmentInflated(view)
    }

    private fun reportFragmentInflated(view: View) {
        val globalLayoutListener = object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val firstDeckView = decksListView.firstDeckView() ?: return
                (requireActivity() as DomainActivity).onDecksListFragmentInflated(firstDeckView)
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onStop() {
        super.onStop()
        viewModel.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.unbind()
    }
}
