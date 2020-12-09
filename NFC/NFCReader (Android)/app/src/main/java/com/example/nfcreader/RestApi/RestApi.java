package com.example.nfcreader.RestApi;


import com.example.nfcreader.Response.TambahUserResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RestApi {

    @FormUrlEncoded
    @POST("uid/tambah_user")
    Call<TambahUserResponse> postTambahUser(
            @Field("uid")String uid
    );
}
