package com.example.medkit.utils;

import com.example.medkit.model.Prediction;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface APIInterface {
    @POST("predict/")
    public Call<Prediction> getPrediction(@Body HashMap<String, String> map);
}
