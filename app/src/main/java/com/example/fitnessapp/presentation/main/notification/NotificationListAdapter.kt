package com.example.fitnessapp.presentation.main.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.data.model.notification.Notification

class NotificationListAdapter(
    private val notifications: List<Notification>,
    private val onClickListener: OnClickListener
) : RecyclerView.Adapter<NotificationListAdapter.NotificationViewHolder>() {

    interface OnClickListener {
        fun onItemClick(notification: Notification, position: Int)
        fun onDeleteItem(notification: Notification, position: Int)
    }

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val dateTextView: TextView = itemView.findViewById(R.id.text_notification_date)
        private val timeTextView: TextView = itemView.findViewById(R.id.text_notification_time)
        private val deleteBtn: ImageButton =
            itemView.findViewById(R.id.image_button_delete_notification)

        fun bind(notification: Notification) {
            dateTextView.text = notification.dateInDateFormat
            timeTextView.text = notification.time
            itemView.setOnClickListener {
                onClickListener.onItemClick(notification, adapterPosition)
            }
            deleteBtn.setOnClickListener {
                onClickListener.onDeleteItem(notification, adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(notifications[position])
    }

    override fun getItemCount(): Int {
        return notifications.size
    }
}