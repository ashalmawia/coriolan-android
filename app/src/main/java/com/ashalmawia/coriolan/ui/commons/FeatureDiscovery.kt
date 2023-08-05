package com.ashalmawia.coriolan.ui.commons

import android.app.Activity
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence

fun Activity.showFeatureDiscoverySequence(targets: List<TapTarget>, listener: () -> Unit) {
    TapTargetSequence(this).targets(targets).listener(object : TapTargetSequence.Listener {
        override fun onSequenceFinish() {
            listener()
        }

        override fun onSequenceStep(lastTarget: TapTarget?, targetClicked: Boolean) {
        }

        override fun onSequenceCanceled(lastTarget: TapTarget?) {
        }
    }).continueOnCancel(true).start()
}

fun tapTargetForView(view: View, @StringRes titleRes: Int, @StringRes descriptionRes: Int): TapTarget {
    val context = view.context
    return TapTarget.forView(
            view,
            context.getString(titleRes),
            context.getString(descriptionRes)
    ).descriptionTextAlpha(0.7f)
}

fun tapTargetForToolbarOverflow(toolbar: Toolbar, @StringRes titleRes: Int, @StringRes descriptionRes: Int): TapTarget {
    val context = toolbar.context
    return TapTarget.forToolbarOverflow(
            toolbar,
            context.getString(titleRes),
            context.getString(descriptionRes)
    ).descriptionTextAlpha(0.7f)
}

fun tapTargetForNavigationIcon(toolbar: Toolbar, @StringRes titleRes: Int, @StringRes descriptionRes: Int): TapTarget {
    val context = toolbar.context
    return TapTarget.forToolbarNavigationIcon(
            toolbar,
            context.getString(titleRes),
            context.getString(descriptionRes)
    ).descriptionTextAlpha(0.7f)
}