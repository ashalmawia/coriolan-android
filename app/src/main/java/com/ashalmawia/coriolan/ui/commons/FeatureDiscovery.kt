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

fun Activity.tapTargetForView(view: View, @StringRes titleRes: Int, @StringRes descriptionRes: Int): TapTarget {
    return TapTarget.forView(
            view,
            getString(titleRes),
            getString(descriptionRes)
    ).descriptionTextAlpha(0.7f)
}

fun Activity.tapTargetForToolbarOverflow(toolbar: Toolbar, @StringRes titleRes: Int, @StringRes descriptionRes: Int): TapTarget {
    return TapTarget.forToolbarOverflow(
            toolbar,
            getString(titleRes),
            getString(descriptionRes)
    ).descriptionTextAlpha(0.7f)
}

fun Activity.tapTargetForNavigationIcon(toolbar: Toolbar, @StringRes titleRes: Int, @StringRes descriptionRes: Int): TapTarget {
    return TapTarget.forToolbarNavigationIcon(
            toolbar,
            getString(titleRes),
            getString(descriptionRes)
    ).descriptionTextAlpha(0.7f)
}