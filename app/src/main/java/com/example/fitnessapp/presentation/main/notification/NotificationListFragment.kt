package com.example.fitnessapp.presentation.main.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bolts.Task
import com.example.fitnessapp.DependencyProvider
import com.example.fitnessapp.R
import com.example.fitnessapp.data.model.notification.Notification
import com.example.fitnessapp.presentation.FragmentContainerActivityCallback
import com.example.fitnessapp.presentation.main.MainActivity
import com.example.fitnessapp.presentation.main.notification.dialogs.NotificationDialogFragment
import com.example.fitnessapp.setInvisible
import com.example.fitnessapp.setVisible
import com.example.fitnessapp.showMessage
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class NotificationListFragment : Fragment(R.layout.fragment_notifications),
    NotificationsFragmentCallback, NotificationListAdapter.OnClickListener {

    companion object {
        const val TAG = "NOTIFICATIONS_FRAGMENT"
        const val EDIT = "EDIT"
        const val ADD = "ADD"

        const val NOTIFICATION_EXTRA = "NOTIFICATION_EXTRA"
        private const val ADAPTER_START_POSITION = 0
        private const val SCROLL_POSITION = "SCROLL_POSITION"
        private const val SELECTED_NOTIFICATION_ID = "SELECTED_NOTIFICATION_ID"
        private const val SAVED_STATE = "SAVED_STATE"
        private const val DEFAULT_SELECTED_ITEM_ID = 0

        fun newInstance() = NotificationListFragment().apply {
            arguments = Bundle()
        }
    }

    private var fragmentContainerActivityCallback: FragmentContainerActivityCallback? = null
    private lateinit var recyclerViewNotifications: RecyclerView
    private lateinit var notificationListAdapter: NotificationListAdapter
    private lateinit var fabAddNotification: FloatingActionButton
    private lateinit var noNotificationsTextView: TextView
    private var notifications = mutableListOf<Notification>()
    private var alarmManager: AlarmManager? = null
    private val localRepository = DependencyProvider.localRepository
    private var scrollPosition = ADAPTER_START_POSITION
    private var selectedNotificationId: Int = DEFAULT_SELECTED_ITEM_ID

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            fragmentContainerActivityCallback = context as MainActivity
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + getString(R.string.no_callback_implementation_error))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)

        alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (savedInstanceState != null) {
            arguments = savedInstanceState.getBundle(SAVED_STATE)
            restoreState(arguments)
        }

        getNotifications()

        fabAddNotification.setOnClickListener {
            onAddNotification()
        }
    }

    private fun initViews(v: View) {
        recyclerViewNotifications = v.findViewById(R.id.recycler_view_notification_list)
        recyclerViewNotifications.layoutManager = LinearLayoutManager(context)
        notificationListAdapter = NotificationListAdapter(notifications, this)
        recyclerViewNotifications.adapter = notificationListAdapter
        fabAddNotification = v.findViewById(R.id.fab_add_notification)
        noNotificationsTextView = v.findViewById(R.id.text_no_notifications_found)
    }

    private fun restoreState(state: Bundle?) {
        state?.let {
            scrollPosition = it.getInt(SCROLL_POSITION)
            selectedNotificationId = it.getInt(SELECTED_NOTIFICATION_ID)
        }
    }

    private fun getNotifications() {
        localRepository.getNotifications().continueWith({ task ->
            if (task.error != null) {
                requireContext().showMessage(message = task.error.message.toString())
            } else {
                handleNotificationsResult(task.result)
            }
        }, Task.UI_THREAD_EXECUTOR)
    }

    private fun handleNotificationsResult(notificationsList: List<Notification>) {
        if (notificationsList.isNotEmpty()) {
            notifications.addAll(notificationsList)
            notificationListAdapter.notifyItemRangeInserted(
                ADAPTER_START_POSITION,
                notificationsList.size
            )
            recyclerViewNotifications.scrollToPosition(scrollPosition)
        } else {
            noNotificationsTextView.setVisible()
        }
    }

    private fun onAddNotification() {
        val bundle = Bundle().apply {
            putString(NotificationDialogFragment.DIALOG_TYPE, ADD)
        }
        NotificationDialogFragment.newInstance(bundle)
            .show(childFragmentManager, NotificationDialogFragment.TAG)
    }

    override fun addNotification(calendar: Calendar) {
        noNotificationsTextView.setInvisible()
        saveNotification(calendar.timeInMillis)
    }

    private fun saveNotification(date: Long) {
        localRepository.insertNotification(date).onSuccess {
            updateNotificationsList()
        }
    }

    private fun updateNotificationsList() {
        localRepository.getLastNotification().onSuccess({ task ->
            notifications.add(task.result)
            notificationListAdapter.notifyItemInserted(notifications.size)
            addAlarm(alarmId = task.result.id, date = task.result.date)
        }, Task.UI_THREAD_EXECUTOR)
    }

    private fun addAlarm(alarmId: Int, date: Long) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(context, alarmId, intent, 0)
        alarmManager?.set(AlarmManager.RTC_WAKEUP, date, pendingIntent)
    }

    override fun editNotification(calendar: Calendar) {
        localRepository.updateNotification(selectedNotificationId, calendar.timeInMillis)
        editAlarm(
            alarmId = selectedNotificationId,
            newDate = calendar.timeInMillis
        )
        val range = notifications.size
        notifications.clear()
        notificationListAdapter.notifyItemRangeRemoved(ADAPTER_START_POSITION, range)
        getNotifications()
    }

    private fun editAlarm(alarmId: Int, newDate: Long) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager?.set(AlarmManager.RTC_WAKEUP, newDate, pendingIntent)
    }

    override fun onItemClick(notification: Notification, position: Int) {
        val bundle = Bundle().apply {
            putString(NotificationDialogFragment.DIALOG_TYPE, EDIT)
            putParcelable(NOTIFICATION_EXTRA, notification)
        }
        selectedNotificationId = notification.id
        NotificationDialogFragment.newInstance(bundle)
            .show(childFragmentManager, NotificationDialogFragment.TAG)
    }

    override fun onDeleteItem(notification: Notification, position: Int) {
        cancelAlarm(notification.id)
        localRepository.deleteNotification(notification.id)
        notifications.remove(notification)
        notificationListAdapter.notifyItemRemoved(position)
        if (notifications.isEmpty()) {
            noNotificationsTextView.setVisible()
        }
    }

    private fun cancelAlarm(alarmId: Int) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        alarmManager?.cancel(pendingIntent)
    }

    override fun onPause() {
        super.onPause()
        scrollPosition =
            (recyclerViewNotifications.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        arguments?.apply {
            putInt(SCROLL_POSITION, scrollPosition)
            putInt(SELECTED_NOTIFICATION_ID, selectedNotificationId)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBundle(SAVED_STATE, arguments)
    }

    override fun onDetach() {
        fragmentContainerActivityCallback = null
        super.onDetach()
    }
}