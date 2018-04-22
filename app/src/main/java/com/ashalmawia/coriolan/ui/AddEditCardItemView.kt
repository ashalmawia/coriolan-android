package com.ashalmawia.coriolan.ui

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.ui.view.showKeyboard
import kotlinx.android.synthetic.main.add_edit_card_item.view.*

private val killDoubleWhitespacesRegex = "\\s+".toRegex()

class AddEditCardItemView : LinearLayout {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attr: AttributeSet?) : this(context, attr, 0)
    constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int) : super(context, attr, defStyleAttr) {
        initialize(context)
    }

    private fun initialize(context: Context) {
        orientation = HORIZONTAL
        LayoutInflater.from(context).inflate(R.layout.add_edit_card_item, this, true)
        minimumHeight = context.resources.getDimensionPixelSize(R.dimen.minimum_tappable_area)
        gravity = Gravity.CENTER

        removeButton.setOnClickListener { removeListener?.invoke(this) }
    }

    fun showKeyboard() {
        inputField.requestFocus()
        inputField.showKeyboard()
    }

    var input
        get() = inputField.text.toString().trim().replace(killDoubleWhitespacesRegex, " ")
        set(value) { inputField.setText(value) }

    var removeListener: ((AddEditCardItemView) -> Unit)? = null

    var canBeDeleted: Boolean = true
        set(value) { removeButton.visibility = if (value) View.VISIBLE else View.INVISIBLE }

    var ordinal: Int = -1
        set(value) {
            ordinalLabel.text = value.toString()
            ordinalLabel.visibility = View.VISIBLE
        }
}