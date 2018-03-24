package com.ashalmawia.coriolan.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.DecksRegistry
import com.ashalmawia.coriolan.data.storage.DataProcessingException
import com.ashalmawia.errors.Errors
import kotlinx.android.synthetic.main.create_deck.*

private const val TAG = "CreateDeckActivity"

class CreateDeckActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_deck)
        initialize()
    }

    private fun initialize() {
        buttonCancel.setOnClickListener { finish() }
        buttonCreate.setOnClickListener { createWithValidation() }
    }

    private fun createWithValidation() {
        val name = nameField.text.toString()
        if (!validate(name)) {
            return
        }

        createDeckAndFinish(name)
    }

    private fun createDeckAndFinish(name: String) {
        try {
            DecksRegistry.get().addDeck(name)
            finishOk()
        } catch (e: DataProcessingException) {
            showError(getString(R.string.add_deck__failed_already_exists, name))
        } catch (e: Exception) {
            Errors.error(TAG, e)
            showError(getString(R.string.add_deck__failed_to_create, name))
        }
    }

    private fun validate(name: String): Boolean {
        if (TextUtils.isEmpty(name)) {
            showError(getString(R.string.add_deck__empty_name))
            return false
        }

        return true
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun intent(context: Context): Intent {
            return Intent(context, CreateDeckActivity::class.java)
        }
    }
}