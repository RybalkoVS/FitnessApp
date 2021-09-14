package com.example.fitnessapp.presentation.main.notification.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.fitnessapp.R

class AddNotificationDialogFragment : DialogFragment() {

    companion object {
        const val TAG = "ADD_NOTIFICATION_DIALOG"

        fun newInstance(args: Bundle?) = AddNotificationDialogFragment().apply {
            arguments = args
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_notification_dialog, container, false)
    }

}