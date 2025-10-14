package com.tech.sid.ui.dashboard.chat_screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tech.sid.LoadingDotsView
import com.tech.sid.R

data class ChatMessage(var message: String, var isSentByCurrentUser: Boolean)

//class ChatAdapter() :
//    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//    private var messages = ArrayList<GetChatApiResponse.Data.Chat.Message>()
//
//    companion object {
//        private const val VIEW_TYPE_SENT = 1
//        private const val VIEW_TYPE_RECEIVED = 2
//    }
//
//    override fun getItemViewType(position: Int): Int {
//        return if (messages[position].from == "user") {
//            VIEW_TYPE_SENT
//        } else {
//            VIEW_TYPE_RECEIVED
//        }
//    }
//
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        return when (viewType) {
//            VIEW_TYPE_SENT -> {
//                val view = LayoutInflater.from(parent.context)
//                    .inflate(R.layout.item_message_sent, parent, false)
//                SentMessageViewHolder(view)
//            }
//
//            else -> {
//                val view = LayoutInflater.from(parent.context)
//                    .inflate(R.layout.item_message_received, parent, false)
//                ReceivedMessageViewHolder(view)
//            }
//        }
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val message = messages[position]
//        when (holder) {
//            is SentMessageViewHolder -> holder.bind(message)
//            is ReceivedMessageViewHolder -> holder.bind(message)
//        }
//    }
//
//    override fun getItemCount(): Int = messages.size
//    class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//         val messageText: TextView = itemView.findViewById(R.id.text_message_sent)
//        fun bind(message: GetChatApiResponse.Data) {
//            messageText.text = message.message
//        }
//    }
//
//    class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//         val messageText: TextView = itemView.findViewById(R.id.text_message_received)
//         val loadingDotsView: LoadingDotsView = itemView.findViewById(R.id.loadingDots)
//        fun bind(message: GetChatApiResponse.Data) {
//            messageText.text = message.message
//        }
//    }
//
//    fun setListData(chatMessage: GetChatApiResponse.Data) {
//        messages.add(chatMessage)
//        notifyItemChanged(messages.size)
//    }
//
//    fun getList(): ArrayList<GetChatApiResponse.Data> {
//        return messages
//    }
//}


class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var messages = ArrayList<GetChatApiResponse.Data.Chat.Message>()

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].from == "user") {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SENT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_sent, parent, false)
                SentMessageViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_received, parent, false)
                ReceivedMessageViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        when (holder) {
            is SentMessageViewHolder -> holder.bind(message)
            is ReceivedMessageViewHolder -> holder.bind(message)
        }
    }

    override fun getItemCount(): Int = messages.size

    // --- ViewHolders ---
    class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.text_message_sent)
        fun bind(message: GetChatApiResponse.Data.Chat.Message) {
            messageText.text = message.message
        }
    }

    class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.text_message_received)
        val loadingDotsView: LoadingDotsView = itemView.findViewById(R.id.loadingDots)

        fun bind(message: GetChatApiResponse.Data.Chat.Message) {
            if (message.message.isNullOrBlank()) {
                // Show typing dots
                loadingDotsView.visibility = View.VISIBLE
                messageText.visibility = View.GONE
            } else {
                // Show actual message
                loadingDotsView.visibility = View.GONE
                messageText.visibility = View.VISIBLE
                messageText.text = message.message
            }
        }
    }

    // --- Helper functions ---
    fun setList(newDataList: List<GetChatApiResponse.Data.Chat.Message>?) {
        messages.clear()
        if (newDataList != null) {
            messages.addAll(newDataList)
        }
        notifyDataSetChanged()
    }


    fun getList(): MutableList<GetChatApiResponse.Data.Chat.Message>? {
        return messages
    }
    fun addToListSendMessage(list: List<GetChatApiResponse.Data.Chat.Message>?) {
        val newDataList: List<GetChatApiResponse.Data.Chat.Message>? = list
        if (newDataList != null) {
            val initialSize = messages.size
            messages.addAll(newDataList)
            notifyItemRangeInserted(
                initialSize, newDataList.size
            )


        }
    }
}

