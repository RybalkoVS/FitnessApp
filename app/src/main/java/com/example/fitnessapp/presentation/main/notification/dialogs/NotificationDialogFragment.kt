package com.example.fitnessapp.presentation.main.notification.dialogs

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.example.fitnessapp.FitnessApp
import com.example.fitnessapp.R
import com.example.fitnessapp.getValue
import com.example.fitnessapp.presentation.main.notification.NotificationListFragment
import com.example.fitnessapp.presentation.main.notification.NotificationsFragmentCallback
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class NotificationDialogFragment : DialogFragment(), DialogInterface.OnClickListener,
    DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    companion object {
        const val TAG = "NOTIFICATION_DIALOG_FRAGMENT"
        const val DIALOG_TYPE = "DIALOG_TYPE"

        fun newInstance(args: Bundle?) = NotificationDialogFragment().apply {
            arguments = args
        }
    }

    private var dialogType: String? = null
    private var notificationsFragmentCallback: NotificationsFragmentCallback? = null
    private val toastProvider = FitnessApp.INSTANCE.toastProvider
    private var calendar = Calendar.getInstance()
    private lateinit var dateEditText: EditText
    private lateinit var timeEditText: EditText
    private var readyToDismiss = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            notificationsFragmentCallback = parentFragment as NotificationsFragmentCallback
        } catch (e: ClassCastException) {
            throw java.lang.ClassCastException(getString(R.string.no_callback_implementation_error))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        arguments?.let {
            dialogType = it.getString(DIALOG_TYPE)
        }
        val dialogBuilder = configureDialog(dialogType)
        return dialogBuilder.create()
    }

    private fun configureDialog(type: String?): AlertDialog.Builder {
        var dialog = AlertDialog.Builder(context)
        when (type) {
            NotificationListFragment.ADD -> {
                dialog = createAddNotificationDialog()
            }
            NotificationListFragment.EDIT -> {
                dialog = createEditNotificationDialog()
            }
        }
        return dialog
    }

    private fun createAddNotificationDialog(): AlertDialog.Builder {
        val customView = View.inflate(context, R.layout.fragment_notification_dialog, null)
        initDialogViews(customView)
        return AlertDialog.Builder(context)
            .setView(customView)
            .setTitle(getString(R.string.add_notification))
            .setPositiveButton(getString(R.string.add_btn_text), this)
            .setNegativeButton(R.string.cancel_btn_text, this)
    }

    private fun createEditNotificationDialog(): AlertDialog.Builder {
        val customView = View.inflate(context, R.layout.fragment_notification_dialog, null)
        initDialogViews(customView)
        return AlertDialog.Builder(context)
            .setView(R.layout.fragment_notification_dialog)
            .setTitle(getString(R.string.edit_notification))
            .setPositiveButton(getString(R.string.save_btn_text), this)
            .setNegativeButton(R.string.cancel_btn_text, this)
    }

    private fun initDialogViews(view: View) {
        dateEditText = view.findViewById(R.id.edit_text_notification_date)
        timeEditText = view.findViewById(R.id.edit_text_notification_time)
        dateEditText.setOnClickListener {
            onEditDateClick()
        }
        timeEditText.setOnClickListener {
            onEditTimeClick()
        }
    }

    private fun onEditDateClick() {
        val datePicker = DatePickerDialogFragment()
        datePicker.show(childFragmentManager, DatePickerDialogFragment.TAG)
    }

    private fun onEditTimeClick() {
        val timePicker = TimePickerDialogFragment()
        timePicker.show(childFragmentManager, TimePickerDialogFragment.TAG)
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                onPositiveButtonClick()
            }
            else -> {
                dismiss()
            }
        }
    }

    private fun onPositiveButtonClick() {
        if (isInputEmpty()) {
            toastProvider.showMessage(message = getString(R.string.empty_fields_toast))
        } else if (!isTimeCorrect()) {
            toastProvider.showMessage(message = getString(R.string.incorrect_time_error))
        } else {
            readyToDismiss = true
            handlePositiveButtonClick()
        }
    }

    private fun isInputEmpty(): Boolean {
        return dateEditText.getValue().isEmpty() || timeEditText.getValue().isEmpty()
    }

    private fun isTimeCorrect(): Boolean {
        return calendar.after(Calendar.getInstance())
    }

    private fun handlePositiveButtonClick() {
        when (dialogType) {
            NotificationListFragment.ADD -> {
                notificationsFragmentCallback?.addNotification(calendar)
            }
            NotificationListFragment.EDIT -> {
                notificationsFragmentCallback?.editNotification(calendar)
            }
        }
    }

    override fun onDateSet(datePicker: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }
        val date = DateFormat.getDateInstance().format(calendar.time)
        dateEditText.setText(date)
    }

    override fun onTimeSet(timePicker: TimePicker?, hourOfDay: Int, minute: Int) {
        calendar.apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
        }
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val time = sdf.format(calendar.time)
        timeEditText.setText(time)
    }

    override fun onDetach() {
        notificationsFragmentCallback = null
        super.onDetach()
    }

    override fun onDismiss(dialog: DialogInterface) {
        if (readyToDismiss) {
            super.onDismiss(dialog)
        }
    }
}