package com.ashalmawia.coriolan.data.importer.file

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.importer.DataImportFlow
import kotlinx.android.synthetic.main.app_toolbar.*
import kotlinx.android.synthetic.main.enter_file_path.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions
import java.io.File

const val EXTRA_TEXT = "extra_text"

@RuntimePermissions
class EnterFilePathActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.enter_file_path)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle(R.string.import_from_file)

        restore(savedInstanceState)

        buttonCancel.setOnClickListener { cancel() }
        buttonSubmit.setOnClickListener { validateAndSubmit(editText.text.toString()) }
    }

    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun cancel() {
        finish()
    }

    fun validateAndSubmit(path: String) {
        if (path.isBlank()) {
            showMessage(getString(R.string.import_file_empty_path))
            return
        }

        submitWithPermissionCheck(path)
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun submit(path: String) {
        val file = File(path)
        if (!file.exists() || file.isDirectory) {
            val message = getString(R.string.import_file_does_not_exist, path)
            showMessage(message)
            return
        }

        (DataImportFlow.ongoing!!.importer as ImporterFromFile).onFile(this, path)
        finish()
    }

    private fun showMessage(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun restore(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            val text = savedInstanceState.getString(EXTRA_TEXT)
            if (text != null) {
                editText.setText(text)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putString(EXTRA_TEXT, editText.text.toString())
    }

    companion object {
        fun intent(context: Context): Intent {
            return Intent(context, EnterFilePathActivity::class.java)
        }
    }
}