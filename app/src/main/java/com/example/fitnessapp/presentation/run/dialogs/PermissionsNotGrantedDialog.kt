package com.example.fitnessapp.presentation.run.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.fitnessapp.R
import com.example.fitnessapp.presentation.run.RunActivity
import com.example.fitnessapp.presentation.run.RunActivityCallback

class PermissionsNotGrantedDialog : DialogFragment(), DialogInterface.OnClickListener {

    companion object {
        const val TAG = "PERMISSION_NOT_GRANTED_DIALOG"
    }

    private var runActivityCallback: RunActivityCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            runActivityCallback = context as RunActivity
        } catch (e: ClassCastException) {
            throw java.lang.ClassCastException(getString(R.string.no_callback_implementation_error))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(context)
            .setTitle(getString(R.string.no_location_permissions))
            .setMessage(getString(R.string.no_location_permissions_message))
            .setPositiveButton(getString(R.string.btn_apply_text), this)
            .setNegativeButton(getString(R.string.cancel_btn_text), this)
        return dialogBuilder.create()
    }

    override fun onClick(dialogInterface: DialogInterface?, button: Int) {
        when (button) {
            DialogInterface.BUTTON_POSITIVE -> {
                runActivityCallback?.requestNeededPermissions()
                dismiss()
            }
            DialogInterface.BUTTON_NEGATIVE -> {
                runActivityCallback?.onBackToMain()
            }
        }
    }

    override fun onDetach() {
        runActivityCallback = null
        super.onDetach()
    }
}