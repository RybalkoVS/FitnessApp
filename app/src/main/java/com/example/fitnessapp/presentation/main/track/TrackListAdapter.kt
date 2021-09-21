package com.example.fitnessapp.presentation.main.track

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.data.model.track.TrackDbo

class TrackListAdapter(
    private var tracks: List<TrackDbo>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<TrackListAdapter.TrackViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(track: TrackDbo)
    }

    inner class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val beginDateTextView: TextView = itemView.findViewById(R.id.text_track_date)
        private val distanceTextView: TextView = itemView.findViewById(R.id.text_track_distance)
        private val durationTextView: TextView = itemView.findViewById(R.id.text_track_duration)

        fun bind(track: TrackDbo) {
            beginDateTextView.text = track.beginTimeDateFormat
            distanceTextView.text = track.distance.toString()
            durationTextView.text = track.durationInMinutes
            itemView.setOnClickListener {
                onItemClickListener.onItemClick(track)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int {
        return tracks.size
    }
}