package com.ashalmawia.coriolan.ui.main.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ashalmawia.coriolan.databinding.EditBinding
import com.ashalmawia.coriolan.ui.BaseActivity
import com.ashalmawia.coriolan.ui.BaseFragment
import com.ashalmawia.coriolan.ui.util.activityViewModelBuilder
import org.koin.android.ext.android.get

private const val ARGUMENT_DOMAIN_ID = "domain_id"

class EditFragment : BaseFragment() {

    companion object {
        fun create(domainId: Long): EditFragment {
            val arguments = Bundle().also {
                it.putLong(ARGUMENT_DOMAIN_ID, domainId)
            }
            return EditFragment().also { it.arguments = arguments }
        }
    }

    private lateinit var view: EditView
    private val viewModel by activityViewModelBuilder {
        val domainId = requireArguments().getLong(ARGUMENT_DOMAIN_ID)
        EditViewModel(domainId, get())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val views = EditBinding.inflate(inflater, container, false)
        view = EditViewImpl(views, requireActivity() as BaseActivity, viewModel)
        return views.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.start(this.view)
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.finish()
    }
}