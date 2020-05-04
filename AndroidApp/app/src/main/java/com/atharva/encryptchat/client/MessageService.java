package com.atharva.encryptchat.client;

import com.atharva.encryptchat.model.Message;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface MessageService {

    @POST("/message/send")
    Call<ResponseBody> send(@Body Message message);

    @GET("/message/receive")
    Call<List<Message>> receive(@Query("phoneNo") String phoneNo);
}
