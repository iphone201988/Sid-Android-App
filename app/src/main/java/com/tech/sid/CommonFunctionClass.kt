package com.tech.sid

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import okhttp3.ResponseBody
import org.json.JSONObject

object CommonFunctionClass {
    fun <T, ID> singleSelectionRV(
        list: List<T>,
        selectedId: ID,
        getId: (T) -> ID,
        isSelectedGetter: (T) -> Boolean,
        isSelectedSetter: (item: T, isSelected: Boolean, selectedItem: T?) -> Unit,
        notifyChanged: (Int) -> Unit
    ): T? {
        var previousSelectedIndex = -1
        var currentSelectedIndex = -1
        var selectedItem: T? = null
        list.forEachIndexed { index, item ->
            val isSelected = getId(item) == selectedId

            if (isSelectedGetter(item)) previousSelectedIndex = index
            if (isSelected) {
                currentSelectedIndex = index
                selectedItem = item
            }
        }
        // Update selection now that we have selectedItem
        list.forEachIndexed { index, item ->
            val isSelected = getId(item) == selectedId
            isSelectedSetter(item, isSelected, selectedItem)
        }

        if (previousSelectedIndex != -1) notifyChanged(previousSelectedIndex)
        if (currentSelectedIndex != -1 && currentSelectedIndex != previousSelectedIndex)
            notifyChanged(currentSelectedIndex)

        return selectedItem
    }

    fun <T> updateItemInRecyclerView(
        recyclerView: RecyclerView,
        list: List<T>,
        position: Int,
        updateItem: (T) -> Unit,
        updateView: (RecyclerView.ViewHolder, T) -> Unit,
        notifyFallback: (Int) -> Unit
    ) {
        if (position !in list.indices) return
        Handler(Looper.getMainLooper()).postDelayed({


            val item = list[position]
            updateItem(item)

            val holder = recyclerView.findViewHolderForAdapterPosition(position)
            if (holder != null) {
                updateView(holder, item)
            } else {
                notifyFallback(position) // If view not visible, notify adapter
            }
        },100)

    }

    fun jsonMessage(response: ResponseBody?): String {
        return try {
            val obj = JSONObject((response ?: "").toString())
            obj.getString("message")
        } catch (e: Exception) {
            return "No message found"
        }

    }
    fun logPrint(tag:String?="DEBUG_LOG",response: String) {
        if (BuildConfig.DEBUG){
            val stackTrace = Throwable().stackTrace
            val element = stackTrace[1]
            val fileName = element.fileName
            val methodName = element.methodName
            val lineNumber = element.lineNumber
            val logMessage = "($fileName:$lineNumber) $methodName() →"
            Log.i(tag, logMessage)
            Log.i(tag, response)
        }

    }







    fun getRandomColor(originalColors:List<String>): String {

          val remainingColors = mutableListOf<String>()
        if (remainingColors.isEmpty()) {
            remainingColors.addAll(originalColors.shuffled())
        }
        return remainingColors.removeAt(0)
    }
}