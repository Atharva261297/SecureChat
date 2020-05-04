package com.atharva.encryptchat
//
//import android.app.Service
//import android.app.job.JobParameters
//import android.app.job.JobService
//import android.content.Intent
//import android.os.IBinder
//import com.atharva.encryptchat.data.RuntimeData
//
//class CheckMessagesService : JobService() {
//    override fun onStopJob(params: JobParameters?): Boolean {
//        val response = RuntimeData.messageService.receive(RuntimeData.phoneNo).execute()
//
//        if (response.isSuccessful && response.code()==200) {
//            response.body().forEach {
//                it.isRead = false
//                it.save()
//            }
//        }
//
//        return false
//    }
//
//    override fun onStartJob(params: JobParameters?): Boolean {
//        return false
//    }
//}
