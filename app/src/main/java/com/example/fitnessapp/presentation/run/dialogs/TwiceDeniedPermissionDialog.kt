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

class TwiceDeniedPermissionDialog : DialogFragment(), DialogInterface.OnClickListener {

    companion object {
        const val TAG = "TWICE_DENIED_PERMISSION_DIALOG"
    }

    private var runActivityCallback: RunActivityCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            runActivityCallback = context as RunActivity
        } catch (e: ClassCastException) {
            throw ClassCastException(getString(R.string.no_callback_implementation_error))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(context)
            .setTitle(getString(R.string.twice_denied_permission_title))
            .setMessage(getString(R.string.twice_denied_permission_message))
            .setPositiveButton(getString(R.string.btn_ok_text), this)
        return dialogBuilder.create()
    }

    override fun onClick(dialog: DialogInterface?, button: Int) {
        when (button) {
            DialogInterface.BUTTON_POSITIVE -> {
                runActivityCallback?.onBackToMain()
            }
            else -> {
                dismiss()
            }
        }
    }

    override fun onDetach() {
        runActivityCallback = null
        super.onDetach()
    }

}