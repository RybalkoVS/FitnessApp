package com.example.fitnessapp.presentation.main.notification.dialogs

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*

class TimePickerDialogFragment : DialogFragment() {

    companion object {
        const val TAG = "TIME PICKER"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c: Calendar = Calendar.getInstance()
        val hour: Int = c.get(Calendar.HOUR_OF_DAY)
        val minute: Int = c.get(Calendar.MINUTE)
        return TimePickerDialog(
            requireContext(),
            parentFragment as TimePickerDialog.OnTimeSetListener,
            hour,
            minute,
            true
        )
    }
}