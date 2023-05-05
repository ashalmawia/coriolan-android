package com.ashalmawia.coriolan.ui.commons

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.ashalmawia.coriolan.R

object DeletingCard {

    fun confirmDeleteCurrentCard(context: Context, deleteCard: () -> Unit) {
        val dialog = AlertDialog.Builder(context)
                .setTitle(R.string.learning_menu_delete_card)
                .setMessage(R.string.deleting_card__are_you_sure)
                .setNegativeButton(R.string.button_cancel, null)
                .setPositiveButton(R.string.button_delete) { _, _ -> deleteCard() }
                .create()
        dialog.show()
    }
}