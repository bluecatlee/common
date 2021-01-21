package com.github.bluecatlee.common.retrofit.service;


import com.github.bluecatlee.common.retrofit.annotation.BaseUrl;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.Map;

@BaseUrl("https://www.tianqiapi.com")
@Deprecated
public interface WeatherService {

    // FormUrlEncoded can only be specified on HTTP methods with request body (e.g., @POST).
    // @FormUrlEncoded
    @GET("/api")
    Call<ResponseBody> test(@Query("version") String version);

}
