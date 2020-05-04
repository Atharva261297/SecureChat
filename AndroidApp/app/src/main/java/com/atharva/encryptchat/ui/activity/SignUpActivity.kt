package com.atharva.encryptchat.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.atharva.encryptchat.R
import com.atharva.encryptchat.data.RuntimeData
import com.atharva.encryptchat.model.Account
import com.atharva.encryptchat.utils.RSA
import kotlinx.android.synthetic.main.activity_sign_up.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
    }

    fun onSignUp(view: View) {
        showProgressBar()
        val phoneNo = sign_up_phone_no.text.toString()
        RuntimeData.phoneNo = phoneNo

//        Generate Keys
        val keyPair = RSA.instance.createAsymmetricKeyPair()
        RuntimeData.public = keyPair.public
        RuntimeData.private = keyPair.private

//        Create Call for sharing public key with server
        val callAddPublicKey = RuntimeData.publicKeyService.addPublicKey(
            Account(
                phoneNo,
                String(Base64.encode(RuntimeData.public.encoded, Base64.NO_WRAP))
            )
        )

//        start the call
       callAddPublicKey.enqueue(object : Callback<ResponseBody> {
           override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
               clearKeys()
               hideProgressBar()
               Toast.makeText(applicationContext, "Something went wrong.\nPlease Try Again Later.", Toast.LENGTH_LONG).show()
           }

           override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
               if (response != null && response.isSuccessful && response.code() == 200) {
                   completeSignUp(phoneNo)
                   hideProgressBar()
                   Toast.makeText(applicationContext, "Sign Up Successful", Toast.LENGTH_SHORT).show()
                   startNewActivity(MainActivity::class.java)
               } else {
                   clearKeys()
                   Toast.makeText(applicationContext, "Something went wrong.\nPlease Try Again Later.", Toast.LENGTH_LONG).show()
               }
           }

       })
    }

    private fun startNewActivity(activity: Class<MainActivity>) {
        val intent = Intent(applicationContext, activity)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

    private fun completeSignUp(phoneNo : String) {
        val pref = getSharedPreferences("EncryptChat", Context.MODE_PRIVATE)
        if (pref.contains("phoneNo")) {
            val editor = pref.edit()
            editor.remove("phoneNo")
            editor.putString("phoneNo", phoneNo)
            editor.apply()
        } else {
            pref.edit().putString("phoneNo", phoneNo).apply()
        }
    }

    private fun clearKeys() {
        getSharedPreferences("EncryptChat", Context.MODE_PRIVATE)
            .edit()
            .remove("phoneNo")
            .apply()
        RSA.instance.removeKeyStoreKey()
    }

    private fun hideProgressBar() {
        sign_up_layout?.alpha = 1f
        progress_bar_sign_up?.visibility = View.GONE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun showProgressBar() {
        sign_up_layout?.alpha = 0.2f
        progress_bar_sign_up?.visibility = View.VISIBLE
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }
}
