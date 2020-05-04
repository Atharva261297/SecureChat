package com.atharva.encryptchat.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import com.atharva.encryptchat.R
import com.atharva.encryptchat.data.RuntimeData
import com.atharva.encryptchat.model.Friend
import com.atharva.encryptchat.model.Message
import com.atharva.encryptchat.utils.MessageFileUtils
import com.atharva.encryptchat.utils.MessageViewUtils
import kotlinx.android.synthetic.main.activity_chat.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.*

class ChatActivity : AppCompatActivity() {

    private var name: String? = null
    private lateinit var phoneNo: String
    private lateinit var publicKey: String

    private val mHandler = Handler()

    private val task = Runnable {
        t() }

    private fun t() {
        mHandler.postDelayed(task, 2000)
        try {
            showUnreadMessages()
        } catch (ignored: Exception) { }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        name = intent.getStringExtra("name")
        if (name == null) {
            throw ExceptionInInitializerError("name not found")
        }

        supportActionBar?.title = name

        val contact = Friend.find(Friend::class.java, "name = ?", name)
        if (contact.size > 1) {
            throw ExceptionInInitializerError("more than one contact with same name found")
        } else {
            phoneNo = contact[0].phoneNo
            publicKey = contact[0].publicKey
        }

        showMessages()
        task.run()
    }

    private fun showMessages() {
        val find = MessageFileUtils.getMessages(phoneNo)
        find.forEach( this::addNewMessageToView )
        chat_scroll_view.post { chat_scroll_view.fullScroll(View.FOCUS_DOWN) }
    }

    private fun showUnreadMessages() {
        val find = MessageFileUtils.getUnreadMessages(phoneNo)
        find.sortBy { it.time }
        find.forEach( this::addNewMessageToView )
        chat_scroll_view.post { chat_scroll_view.fullScroll(View.FOCUS_DOWN) }
    }

    fun onSend(view: View) {
        val msg = chat_message_box.text.toString()
        val message = Message(phoneNo, RuntimeData.phoneNo, msg, Date(), true)
        RuntimeData.messageService.send(message.encrypted(publicKey)).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                runOnUiThread {
                    Toast.makeText(applicationContext, "Error while sending message", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                if (response == null || !response.isSuccessful || response.code() != 200) {
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Error while sending message", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    MessageFileUtils.addMessage(message, message.receiver)
                    runOnUiThread{
                        addNewMessageToView(message)
                        chat_message_box.text.clear()
                    }
                }
            }

        })

    }

    private fun addNewMessageToView(message: Message) {
        when(message.receiver) {
            phoneNo -> {
                chat_message_view.addView(MessageViewUtils.messageBoxForReadMy(applicationContext, message.message, resources))
            }

            RuntimeData.phoneNo -> {
                chat_message_view.addView(if (message.isRead) {
                        MessageViewUtils.messageBoxForReadTheir(applicationContext, message.message, resources)
                    } else {
                        MessageViewUtils.messageBoxForUnread(applicationContext, message.message, resources)
                    }
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        mHandler.removeCallbacks(task)
    }
}
