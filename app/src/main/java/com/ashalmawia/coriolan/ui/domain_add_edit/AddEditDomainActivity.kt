package com.ashalmawia.coriolan.ui.domain_add_edit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.databinding.AddEditDomainBinding
import com.ashalmawia.coriolan.ui.BaseActivity
import com.ashalmawia.coriolan.ui.backup.RestoreFromBackupActivity
import com.ashalmawia.coriolan.ui.util.viewModelBuilder
import org.koin.android.ext.android.get

private const val EXTRA_FIRST_START = "cancellable"

class AddEditDomainActivity : BaseActivity() {

    private val firstStart by lazy { intent.getBooleanExtra(EXTRA_FIRST_START, false) }
    private val viewModel by viewModelBuilder { AddEditDomainViewModel(get(), get(), get(), firstStart) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val views = AddEditDomainBinding.inflate(layoutInflater)
        setContentView(views.root)

        val view: AddEditDomainView = AddEditDomainViewImpl(views, this, viewModel)
        viewModel.start(view)
    }

    override fun onDestroy() {
        viewModel.stop()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (firstStart) {
            menuInflater.inflate(R.menu.create_domain, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.create_domain_menu__restore_from_backup -> {
                restoreFromBackup()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun restoreFromBackup() {
        val intent = RestoreFromBackupActivity.intent(this)
        startActivity(intent)
    }

    companion object {

        fun intent(context: Context, firstStart: Boolean): Intent {
            val intent = Intent(context, AddEditDomainActivity::class.java)
            intent.putExtra(EXTRA_FIRST_START, firstStart)
            return intent
        }
    }
}