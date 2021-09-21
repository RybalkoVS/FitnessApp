package com.example.fitnessapp.presentation.main

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.fitnessapp.R

class AuthorizationTokenExpiredDialog : DialogFragment(), DialogInterface.OnClickListener {

    companion object {
        const val TAG = "AUTHORIZATION_TOKEN_EXPIRED_DIALOG"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(context)
            .setTitle(getString(R.string.authorization_token_invalid))
            .setMessage(getString(R.string.authorization_token_expired))
            .setPositiveButton(getString(R.string.btn_ok_text), this)
        return dialogBuilder.create()
    }

    override fun onClick(dialogInterface: DialogInterface?, button: Int) {
        dismiss()
    }
}