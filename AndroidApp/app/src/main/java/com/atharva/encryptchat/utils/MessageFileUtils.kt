package com.atharva.encryptchat.utils

import com.atharva.encryptchat.data.RuntimeData
import com.atharva.encryptchat.model.Message
import com.google.gson.reflect.TypeToken
import java.io.*

class MessageFileUtils {

    companion object {
        private var saved = false
        private lateinit var messages: HashMap<String, MutableList<Message>>

        fun readFile(file: File) {
            val sb = StringBuilder()

            val reader = BufferedReader(FileReader(file))
            reader.lines().forEach {
                sb.append(it)
            }

            val type = object : TypeToken< HashMap<String, MutableList<Message>>>() {}.type

            messages = if ( sb.isEmpty() ) {
                HashMap()
            } else {
                RuntimeData.gson.fromJson(sb.toString(), type)
            }

            reader.close()
        }

        fun writeFile(file: File) {
            val writer = BufferedWriter(FileWriter(file))
            writer.write(RuntimeData.gson.toJson(messages))
            writer.close()
            saved = true
        }

        fun clearFile(file: File) {
            val writer = BufferedWriter(FileWriter(file))
            writer.write("")
            writer.close()
            messages = HashMap()
            saved = true
        }

        fun addMessage(message: Message, id: String) {
            if (messages.containsKey(id)) {
                messages[id]!!.add(message)
            } else {
                val list = ArrayList<Message>()
                list.add(message)
                messages[id] = list.toMutableList()
            }
            saved = false
        }

        fun getMessages(phone: String): List<Message> {
            return if (messages.containsKey(phone)) {
                val list = messages[phone]!!
                list.sortBy { it.time }
                markRead(list, phone)
                list.toList()
            } else {
                emptyList()
            }
        }

//        private fun copyMessages(list: java.util.ArrayList<Message>): java.util.ArrayList<Message> {
//            val returnList = ArrayList<Message>()
//            list.forEach {
//                returnList.add(Message(it))
//            }
//            return returnList
//        }

        fun getUnreadMessages(phone: String): ArrayList<Message> {
            return if (messages.containsKey(phone)) {
                val mList = ArrayList<Message>()
                messages[phone]!!.forEach {
                    if (!it.isRead) {
                        mList.add(Message(it))
                    }
                }
                mList.sortBy { it.time }
                markRead(mList, phone)
                saved = false
                mList
            } else {
                ArrayList()
            }
        }

        private fun markRead(messageList: MutableList<Message>, id: String) {
            messageList.forEach {
                it.isRead = true
            }
        }

        private fun markRead(messageList: ArrayList<Message>, id: String) {
            messageList.forEach {
                messages[id]!!.set(messages[id]!!.indexOf(it), Message(it, true))
            }
        }
    }
}