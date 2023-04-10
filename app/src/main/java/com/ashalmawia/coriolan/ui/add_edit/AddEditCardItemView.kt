package com.ashalmawia.coriolan.ui.add_edit

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.databinding.AddEditCardItemBinding
import com.ashalmawia.coriolan.ui.view.layoutInflator
import com.ashalmawia.coriolan.ui.view.showKeyboard

private val killDoubleWhitespacesRegex = "\\s+".toRegex()

class AddEditCardItemView : LinearLayout {
    
    private val views: AddEditCardItemBinding

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attr: AttributeSet?) : this(context, attr, 0)
    constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int) : super(context, attr, defStyleAttr)

    init {
        orientation = HORIZONTAL
        views = AddEditCardItemBinding.inflate(layoutInflator, this)
        minimumHeight = context.resources.getDimensionPixelSize(R.dimen.minimum_tappable_area)
        gravity = Gravity.CENTER

        views.removeButton.setOnClickListener { removeListener?.invoke(this) }
    }

    fun showKeyboard() {
        views.inputField.requestFocus()
        views.inputField.showKeyboard()
    }

    var input
        get() = views.inputField.text.toString().trim().replace(killDoubleWhitespacesRegex, " ")
        set(value) { views.inputField.setText(value) }

    var removeListener: ((AddEditCardItemView) -> Unit)? = null

    var canBeDeleted: Boolean = true
        set(value) { views.removeButton.visibility = if (value) View.VISIBLE else View.INVISIBLE }

    var ordinal: Int = -1
        set(value) {
            views.ordinalLabel.text = value.toString()
            views.ordinalLabel.visibility = View.VISIBLE
        }
}