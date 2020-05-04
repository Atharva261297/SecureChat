package com.atharva.encryptchat.ui.activity

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.provider.ContactsContract
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.atharva.encryptchat.R
import com.atharva.encryptchat.data.RuntimeData
import com.atharva.encryptchat.model.Friend
import com.atharva.encryptchat.model.Message
import com.atharva.encryptchat.utils.MessageFileUtils
import com.atharva.encryptchat.utils.RSA
import com.orm.query.Condition
import com.orm.query.Select
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_CODE: Int = 110

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (initData()) {
//            If already sign-up is done
            startActivity(Intent(applicationContext, SignUpActivity::class.java))
            finish()

        } else {
            if (checkPermissions()) {
                syncContacts()
            }
            startReceivingMessages()
        }
    }

    private fun initData(): Boolean {
        val pref = getSharedPreferences("EncryptChat", Context.MODE_PRIVATE)
        val phone = pref.getString("phoneNo", "")

        return if (phone.isNullOrBlank()) {
            Log.d("EncryptChat", "Phone no not found")
            true
        } else {
            Log.d("EncryptChat", "Phone no found - $phone")
            RuntimeData.phoneNo = phone

            val keyPair = RSA.instance.getAsymmetricKeyPair()
            if (keyPair != null) {
                Log.d("EncryptChat", "Keys found")
                RuntimeData.private = keyPair.private
                RuntimeData.public = keyPair.public
                false
            } else {
                Log.d("EncryptChat", "Keys not found")
                true
            }
        }
    }

    private fun checkPermissions(): Boolean {
        return when {
            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_CALENDAR)
                    != PackageManager.PERMISSION_GRANTED -> {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    PERMISSIONS_REQUEST_CODE)
                false
            }
            else -> {
                true
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    syncContacts()
                }
            }
        }
    }

    private fun syncContacts() {
//        get all contacts form phone
        val query =
            contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                null)

        val list = ArrayList<Friend>()

        if (query != null && query.count > 0) {
            val nameIndex =
                query.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val hasNumberIndex =
                query.getColumnIndex(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER)
            val numberIndex =
                query.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (query.moveToNext()) {
                if (query.getInt(hasNumberIndex) != 0) {
                    list.add(Friend(query.getString(nameIndex), query.getString(numberIndex), null))
                }
            }
        }

        query?.close()

        if (list.size < 10) {
            RuntimeData.publicKeyService.syncKeys(list).enqueue(object : Callback<List<Friend>> {
                override fun onFailure(call: Call<List<Friend>>?, t: Throwable?) {
                    Toast.makeText(applicationContext, "Error while synchronizing contact list", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<List<Friend>>?, response: Response<List<Friend>>?) =
                    if (response != null && response.isSuccessful && response.code() == 200) {
                        val listFriends = response.body()
                        listFriends.forEach { friend: Friend ->
                            run {
                                val find =
                                    Friend.find(Friend::class.java, "PHONE_NO = ?", friend.phoneNo)
                                if (find.size > 0) {
                                    Friend.deleteAll(Friend::class.java, "PHONE_NO = ?", friend.phoneNo)
                                }
                                friend.save()
                            }
                        }
                        searchContacts(null)
                    } else {
                        Toast.makeText(applicationContext, "Error while synchronizing contact list", Toast.LENGTH_LONG).show()
                    }
            })
        }
    }


    private val mHandlerCheckMessage = Handler()
    private val taskCheckMessage = Runnable { tCheckMessage() }
    private fun tCheckMessage() {
        mHandlerCheckMessage.postDelayed(taskCheckMessage, 5000)
        checkMessages()
    }

    private val mHandlerSyncMessage = Handler()
    private val taskSyncMessage = Runnable { tSyncMessage() }
    private fun tSyncMessage() {
        mHandlerSyncMessage.postDelayed(taskSyncMessage, 5000)
        MessageFileUtils.writeFile(RuntimeData.messagesFile)
    }

    private fun startReceivingMessages() {
        val file = File(filesDir, "messages")
        if (!file.exists()) {
            file.createNewFile()
        }
        MessageFileUtils.readFile(file)
        RuntimeData.messagesFile = file
        taskCheckMessage.run()
        taskSyncMessage.run()
    }

    private fun checkMessages() {
        Log.e("EncryptChat - msg", "Checking messages")

        RuntimeData.messageService.receive(RuntimeData.phoneNo).enqueue(object : Callback<List<Message>> {
            override fun onFailure(call: Call<List<Message>>?, t: Throwable?) {
                Log.e("EncryptChat - error", "Checking messages", t)
            }

            override fun onResponse(call: Call<List<Message>>?, response: Response<List<Message>>?) {
                if (response == null || !response.isSuccessful || response.code() != 200) {
                    Log.e("EncryptChat - error", "Checking messages ${response!!.errorBody()}")
                } else {
                    response.body().forEach {
                        it.isRead = false
                        MessageFileUtils.addMessage(it.decrypted(), it.sender)
                    }
                }
            }

        })

        Log.e("EncryptChat - msg", "Checking messages")
    }



//    private fun initContacts() {
//        val allContacts = Friend.findAll(Friend::class.java)
////        main_show_contacts.removeAllViews()
//        allContacts.forEach {
//            main_show_contacts.addView(getContactDisplayBox(it.name))
//        }
//    }

    private fun searchContacts(text : String?) {
        if (text.isNullOrBlank()) {
            val allContacts = Friend.findAll(Friend::class.java)
            main_show_contacts.removeAllViews()
            allContacts.forEach {
                main_show_contacts.addView(getContactDisplayBox(it.name))
            }
        } else {

            val allContacts = Select.from(Friend::class.java).where(Condition.prop("name").like(text)).list()
            main_show_contacts.removeAllViews()
            allContacts.forEach {
                main_show_contacts.addView(getContactDisplayBox(it.name))
            }
        }
    }






    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.refresh_option -> {
                //                refreshList()
            }
            R.id.contact_option -> {
                sendMail()
            }
            R.id.about_option -> {
//                startActivity(Intent(applicationContext, AboutActivity::class.java))
//                finish()
            }
            R.id.sign_out_option -> {
                onClearKeys()
                startActivity(Intent(applicationContext, SignUpActivity::class.java))
                finish()
            }
        }
        return true
    }

    private fun sendMail() {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", resources.getString(R.string.contact_mail), null))
        intent.putExtra(Intent.EXTRA_SUBJECT, "Regarding EncryptChat android application")
        intent.putExtra(Intent.EXTRA_TEXT, "(Remove this line and add your message. If Changing subject of mail " +
                "then please mention name of application in subject)")
        startActivity(intent)
        finish()
    }

    private fun onClearKeys() {
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())
        val response = RuntimeData.publicKeyService.deletePublicKey(RuntimeData.phoneNo).execute()

        if (response.isSuccessful && response.code() == 200) {
            Friend.deleteAll(Friend::class.java)
            MessageFileUtils.clearFile(RuntimeData.messagesFile)

            getSharedPreferences("EncryptChat", Context.MODE_PRIVATE)
                .edit()
                .remove("phoneNo")
                .apply()

            RSA.instance.removeKeyStoreKey()
        }
    }

    fun onAddNewContact(view: View) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_add_contact)

        dialog.findViewById<Button>(R.id.add_contact_save_btn).setOnClickListener {
            val name = dialog.findViewById<EditText>(R.id.add_contact_name).text.toString()
            val number = dialog.findViewById<EditText>(R.id.add_contact_number).text.toString()

            if (Friend.find(Friend::class.java, "phone_no = ? or name = ?", number, name).size ==0 ) {
                val callGetPublicKey = RuntimeData.publicKeyService.getPublicKey(number)

                StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())
                val response = callGetPublicKey.execute()

                if (response.isSuccessful && response.code() == 200) {
                    if (response.body() != null) {
                        Friend(name, number, response.body()).save()
                        Toast.makeText(applicationContext, "Added $name as friend", Toast.LENGTH_LONG).show()
                        dialog.dismiss()
                        searchContacts(null)
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Please check entered Phone number and try again",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(applicationContext, "Something went wrong.\nTry again later", Toast.LENGTH_LONG)
                        .show()
                }
            } else {
                Toast.makeText(applicationContext, "Already in friend list", Toast.LENGTH_LONG)
                    .show()
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun getContactDisplayBox(name: String) : TextView {
        val textView = TextView(ContextThemeWrapper(this, R.style.Contact), null, 0)
        textView.text = name
        textView.layoutParams = getLayoutParamsForTextView()
        textView.setOnClickListener(this::onOpenChat)
        return textView
    }

    private fun getLayoutParamsForTextView(): LinearLayout.LayoutParams {
        val layoutParams =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val margin = resources.getDimensionPixelSize(R.dimen.margin_size)
        layoutParams.setMargins(margin, margin/2, margin, margin/2)
        return layoutParams
    }

    private fun onOpenChat(view: View) {
        val chatIntent = Intent(applicationContext, ChatActivity::class.java)
        chatIntent.putExtra("name", (view as TextView).text)
        startActivity(chatIntent)
    }
}
