package com.android.example.bakingapp.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.example.bakingapp.R;
import com.android.example.bakingapp.adapter.RecipeAdapter;
import com.android.example.bakingapp.model.Ingredient;
import com.android.example.bakingapp.model.Recipe;
import com.android.example.bakingapp.model.Step;
import com.android.example.bakingapp.rest.ApiClient;
import com.android.example.bakingapp.rest.ApiInterface;
import com.android.example.bakingapp.util.Utility;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.android.example.bakingapp.util.Utility.displayMessage;

public class RecipeActivity extends AppCompatActivity implements RecipeAdapter.onOptionClickListener {

    public static final String RECIPES_ID = "recipes_id";
    public static final String INGREDIENT_EXTRA = "ingredient_extra";
    public static final String STEP_EXTRA = "step_extra";
    @InjectView(R.id.recipe_recycler_view)
    RecyclerView recipeRecyclerView;
    @InjectView(R.id.recipe_progress_bar)
    ProgressBar mProgressBar;

    private RecipeAdapter recipeAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<Recipe> recipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_screen_layout);
        ButterKnife.inject(this);
        boolean tabView = false;
        if (findViewById(R.id.recipe_list_on_tab) != null) tabView = true;
        if (tabView) {
            //set the layout of the recycler view to be grid layout
            layoutManager = new GridLayoutManager(this, 5);

        } else {
            //if landscape,use grid view of 3
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                layoutManager = new GridLayoutManager(this, 3);
            else
                layoutManager = new LinearLayoutManager(this);
        }
        recipeRecyclerView.setLayoutManager(layoutManager);
        recipeRecyclerView.setHasFixedSize(true);

        if (savedInstanceState != null) {
            recipes = savedInstanceState.getParcelableArrayList(RECIPES_ID);
            recipeAdapter = new RecipeAdapter(this, recipes, this);
            recipeRecyclerView.setAdapter(recipeAdapter);
            return;
        }
        //call the Api to get the data
        callToApi();

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        recipes = savedInstanceState.getParcelableArrayList(RECIPES_ID);
        recipeAdapter = new RecipeAdapter(this, recipes, this);
        recipeRecyclerView.setAdapter(recipeAdapter);
    }

    void callToApi() {

        if (!Utility.isOnline(this)) {

            String title = "Connection";
            String message = "No internet connection. Please try again.";
            displayMessage(this, title, message);

            return;
        }
        mProgressBar.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<Recipe>> callToApi = apiInterface.getRecipes();

        callToApi.enqueue(new Callback<ArrayList<Recipe>>() {
            @Override
            public void onResponse(Call<ArrayList<Recipe>> call, Response<ArrayList<Recipe>> response) {

                mProgressBar.setVisibility(View.INVISIBLE);
                //getting the response body
                recipes = response.body();
                if (recipes == null) {
                    ResponseBody responseBody = response.errorBody();
                    String errorTitle;
                    String errorMessage;
                    if (responseBody != null) {
                        errorTitle = "Error";
                        errorMessage = "An error occurred.";
                    } else {
                        errorTitle = "Error";
                        errorMessage = "No data Received.";
                    }
                    Utility.displayMessage(RecipeActivity.this, errorTitle, errorMessage);

                } else {
                    recipes = response.body();

                    if (recipes.size() == 0) {
                        Toast toast = Toast.makeText(getApplicationContext(), "No Recipes", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    } else {
                        //create the recipes list adapter here
                        recipeAdapter = new RecipeAdapter(RecipeActivity.this, recipes, RecipeActivity.this);
                        recipeRecyclerView.setAdapter(recipeAdapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Recipe>> call, Throwable t) {
                mProgressBar.setVisibility(View.INVISIBLE);
                String errorTitle = "Error";
                String errorMessage = "Data request failed.";
                displayMessage(RecipeActivity.this, errorTitle, errorMessage);
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //to avoid any reload when orientation is changed
        outState.putParcelableArrayList(RECIPES_ID, recipes);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onIngredientClick(int position) {
        //create an explicit intent to launch the ingredient activity
        Intent intent = new Intent(RecipeActivity.this, IngredientActivity.class);
        ArrayList<Ingredient> ingredients = recipes.get(position).getIngredients();
        if (ingredients != null && ingredients.size() > 0) {
            intent.putParcelableArrayListExtra(INGREDIENT_EXTRA, ingredients);
            startActivity(intent);
        } else {
            Toast toast = Toast.makeText(this, "No Ingredient for Recipe.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    @Override
    public void onStepsClick(int position) {
        //create an explicit intent to launch the steps activity
        Intent intent = new Intent(RecipeActivity.this, StepActivity.class);
        ArrayList<Step> steps = recipes.get(position).getSteps();
        if (steps != null && steps.size() > 0) {
            intent.putParcelableArrayListExtra(STEP_EXTRA, steps);
            startActivity(intent);
        } else {
            Toast toast = Toast.makeText(this, "No Steps for Recipe.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }
}
