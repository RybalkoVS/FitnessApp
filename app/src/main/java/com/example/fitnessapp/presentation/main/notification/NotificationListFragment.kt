package com.example.fitnessapp.presentation.main.notification

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.data.model.notification.Notification
import com.example.fitnessapp.presentation.FragmentContainerActivityCallback
import com.example.fitnessapp.presentation.main.notification.dialogs.NotificationDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.sql.Timestamp
import java.util.*

class NotificationListFragment : Fragment(R.layout.fragment_notifications),
    NotificationsFragmentCallback, NotificationListAdapter.OnClickListener {

    companion object {
        const val TAG = "NOTIFICATIONS_FRAGMENT"
        const val EDIT = "EDIT"
        const val ADD = "ADD"

        fun newInstance() = NotificationListFragment().apply {
            arguments = Bundle()
        }
    }

    private var fragmentContainerActivityCallback: FragmentContainerActivityCallback? = null
    private lateinit var recyclerViewNotifications: RecyclerView
    private lateinit var notificationListAdapter: NotificationListAdapter
    private lateinit var fabAddNotification: FloatingActionButton
    private var notifications = mutableListOf<Notification>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentContainerActivityCallback) {
            fragmentContainerActivityCallback = context
        } else {
            throw RuntimeException(context.toString() + getString(R.string.no_callback_implementation_error))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)

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
    }

    private fun onAddNotification() {
        val bundle = Bundle().apply {
            putString(NotificationDialogFragment.DIALOG_TYPE, ADD)
        }
        NotificationDialogFragment.newInstance(bundle)
            .show(childFragmentManager, NotificationDialogFragment.TAG)
    }

    override fun addNotification(calendar: Calendar) {
        val date = Timestamp(calendar.timeInMillis)
        //TODO()
    }

    override fun editNotification(calendar: Calendar) {
        //TODO()
    }

    override fun onItemClick(notification: Notification) {
        val bundle = Bundle().apply {
            putString(NotificationDialogFragment.DIALOG_TYPE, EDIT)
        }
        NotificationDialogFragment.newInstance(bundle)
            .show(childFragmentManager, NotificationDialogFragment.TAG)
    }

    override fun onDeleteItem(notificationId: Int) {
        //TODO()
    }

    override fun onDetach() {
        fragmentContainerActivityCallback = null
        super.onDetach()
    }

}