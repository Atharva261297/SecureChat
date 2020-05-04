package com.atharva.encryptchat.data

import android.content.Context
import com.atharva.encryptchat.client.MessageService
import com.atharva.encryptchat.client.PublicKeyService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.security.PrivateKey
import java.security.PublicKey


class RuntimeData {

    companion object {
        var gson: Gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
            .create()

        private val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.0.6:8080/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        lateinit var phoneNo: String
        lateinit var private: PrivateKey
        lateinit var public: PublicKey

        val publicKeyService: PublicKeyService = retrofit
            .create(PublicKeyService::class.java)

        val messageService: MessageService = retrofit
            .create(MessageService::class.java)

        lateinit var messagesFile: File
    }
}