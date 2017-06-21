package com.android.example.bakingapp.rest;


import com.android.example.bakingapp.model.Recipe;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;


public interface ApiInterface {
    /**
     * Defines a method signature to simulate the HTTP GET request with @GET annotation
     */
    @GET("/topher/2017/May/59121517_baking/baking.json")
    Call<ArrayList<Recipe>> getRecipes();

}
