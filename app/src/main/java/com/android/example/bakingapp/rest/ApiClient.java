package com.android.example.bakingapp.rest;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {

    private static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net";
    private static Retrofit.Builder retrofitBuilder = null;
    private static Retrofit retrofit;


    /**
     * A static method to initialize the retrofit object with the base url and converter
     *
     * @return Retrofit object
     */
    public static Retrofit getClient() {

        if (retrofit == null) {
            //initializing the Retrofit builder, adding the base url and Converter to use
            retrofitBuilder = new Retrofit.Builder();
            retrofitBuilder.baseUrl(BASE_URL);
            retrofitBuilder.addConverterFactory(GsonConverterFactory.create());
            retrofit = retrofitBuilder.build();

        }
        return retrofit;

    }

}
