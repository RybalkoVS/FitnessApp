package com.example.fitnessapp.presentation.main.notification

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.presentation.FragmentContainerActivityCallback
import com.example.fitnessapp.presentation.main.notification.dialogs.AddNotificationDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NotificationsFragment : Fragment(R.layout.fragment_notifications) {

    companion object {
        const val TAG = "NOTIFICATIONS_FRAGMENT"

        fun newInstance() = NotificationsFragment().apply {
            arguments = Bundle()
        }
    }

    private var fragmentContainerActivityCallback: FragmentContainerActivityCallback? = null
    private lateinit var recyclerViewNotifications: RecyclerView
    private lateinit var fabAddNotification: FloatingActionButton

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
        fabAddNotification = v.findViewById(R.id.fab_add_notification)
    }

    private fun onAddNotification() {
        AddNotificationDialogFragment().show(childFragmentManager, AddNotificationDialogFragment.TAG)
    }

    override fun onDetach() {
        fragmentContainerActivityCallback = null
        super.onDetach()
    }
}