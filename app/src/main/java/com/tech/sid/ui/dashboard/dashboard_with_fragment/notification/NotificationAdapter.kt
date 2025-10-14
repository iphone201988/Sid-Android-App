package com.tech.sid.ui.dashboard.dashboard_with_fragment.notification

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tech.sid.databinding.HolderNotificationBinding
import com.tech.sid.databinding.HolderNotificationNoMoodBinding

class NotificationAdapter(val context: Context, var list: ArrayList<NotificationData>, val listener: MoodListener): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private var myMood:String?=null
    private var selectedPosition:Int?=null

    companion object {
        private const val VIEW_TYPE_NOTIFICATION_NO_MOOD = 0
        private const val VIEW_TYPE_NOTIFICATION_MOOD = 1
    }

    override fun getItemViewType(position: Int): Int {
        val item = list[position]
        return if (item.type == "mood_reminder") {
            VIEW_TYPE_NOTIFICATION_MOOD
        } else {
            VIEW_TYPE_NOTIFICATION_NO_MOOD
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_NOTIFICATION_NO_MOOD -> {
                NotificationNoMoodViewHolder(
                    HolderNotificationNoMoodBinding.inflate(
                        LayoutInflater.from(
                            context
                        ), parent, false
                    )
                )
            }

            VIEW_TYPE_NOTIFICATION_MOOD -> {
                NotificationViewHolder(
                    HolderNotificationBinding.inflate(
                        LayoutInflater.from(
                            context
                        ), parent, false
                    )
                )
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        when (holder) {
            is NotificationViewHolder -> {
                holder.binding.apply {
                    tvNotification.text = item.title
                    ivThriving.setOnClickListener {
                        myMood = "Thriving"
                        selectedPosition =position
                        listener.moodUpdate(context, myMood!!)
                    }
                    ivGrateful.setOnClickListener {
                        myMood = "Grateful"
                        selectedPosition =position
                        listener.moodUpdate(context, myMood!!)
                    }
                    ivDrifting.setOnClickListener {
                        myMood = "Drifting"
                        selectedPosition =position
                        listener.moodUpdate(context, myMood!!)
                    }
                    ivOverwhelmed.setOnClickListener {
                        myMood = "Overwhelmed"
                        selectedPosition =position
                        listener.moodUpdate(context, myMood!!)
                    }
                    ivLow.setOnClickListener {
                        myMood = "Low"
                        selectedPosition =position
                        listener.moodUpdate(context, myMood!!)
                    }
                    createJournal.setOnClickListener {
                        if (myMood!=null && selectedPosition!=null && selectedPosition==position) {
                            listener.updateTodayMood(myMood!!)
                        }
                    }
                }
            }

            is NotificationNoMoodViewHolder -> {
                holder.binding.tvNotification.text = item.title
            }
        }
    }


    class NotificationNoMoodViewHolder(itemView: HolderNotificationNoMoodBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val binding = itemView
    }

    class NotificationViewHolder(itemView: HolderNotificationBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val binding = itemView
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface MoodListener{
        fun moodUpdate(context:Context,mood: String)
        fun updateTodayMood(mood: String)
    }
}