package com.ashalmawia.coriolan.ui.domain_add_edit

import androidx.appcompat.app.AlertDialog
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.ui.BaseActivity
import com.ashalmawia.coriolan.ui.util.positiveButton

object DeleteDomainDialog {

    fun BaseActivity.showConfirmDeleteDomainDialog(domain: Domain, onDelete: (Domain) -> Unit) {
        AlertDialog.Builder(this)
                .setTitle(R.string.delete_domain__title)
                .setMessage(getString(R.string.delete_domain__message, domain.name))
                .positiveButton(R.string.delete_domain__ok) { onDelete(domain) }
                .setNegativeButton(R.string.button_cancel, null)
                .create()
                .show()
    }
}