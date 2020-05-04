package com.atharva.encryptchat.client;

import com.atharva.encryptchat.model.Account;
import com.atharva.encryptchat.model.Friend;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PublicKeyService {

    @POST("/add")
    Call<ResponseBody> addPublicKey(@Body Account account);

    @GET("/getKey")
    Call<String> getPublicKey(@Query("phoneNo") String phoneNo);

    @POST("/syncKeys")
    Call<List<Friend>> syncKeys(@Body List<Friend> phoneNos);

    @POST("/remove")
    Call<ResponseBody> deletePublicKey(@Query("phoneNo") String phoneNo);

}
