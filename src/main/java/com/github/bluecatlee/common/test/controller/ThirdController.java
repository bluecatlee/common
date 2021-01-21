package com.github.bluecatlee.common.test.controller;

import com.github.bluecatlee.common.retrofit.client.ThirdClient;
import com.github.bluecatlee.common.retrofit.service.WeatherService;
import com.github.bluecatlee.common.test.bean.Weather;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

@RestController
@RequestMapping("/third")
public class ThirdController {

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private ThirdClient thirdClient;

    @GetMapping("/getWeather")
    public String getWeather() {
        Call<ResponseBody> test = weatherService.test("v1");
        Response<ResponseBody> response = null;
        try {
            response = test.execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/getWeather2")
    public Weather getWeather2() {
        Weather v1 = thirdClient.executeRequest(null, Weather.class, () -> weatherService.test("v1"));
        return v1;
    }

}
