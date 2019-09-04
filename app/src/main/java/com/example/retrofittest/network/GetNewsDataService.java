package com.example.retrofittest.network;

import com.example.retrofittest.model.News;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetNewsDataService {
    @GET("top-headlines")
    Call<News> getNews(

            @Query("country") String country,
            @Query("apiKey") String apiKey

    );

    @GET("everything")
    Call<News> getSpecificNews(

            @Query("language") String language,
            @Query("qInTitle") String qInTitle,
            @Query("sortBy") String sortBy,
            @Query("apiKey") String apiKey

    );
}
