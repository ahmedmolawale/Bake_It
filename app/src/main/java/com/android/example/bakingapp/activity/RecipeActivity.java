package com.android.example.bakingapp.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.core.deps.guava.annotations.VisibleForTesting;
import android.support.v7.app.ActionBar;
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
import com.android.example.bakingapp.data.RecipeContentProvider;
import com.android.example.bakingapp.data.RecipeContract;
import com.android.example.bakingapp.idlingresource.SimpleIdlingResource;
import com.android.example.bakingapp.model.Ingredient;
import com.android.example.bakingapp.model.Recipe;
import com.android.example.bakingapp.model.Step;
import com.android.example.bakingapp.rest.ApiClient;
import com.android.example.bakingapp.rest.ApiInterface;
import com.android.example.bakingapp.util.Utility;
import com.android.example.bakingapp.widget.RecipeService;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.android.example.bakingapp.util.Utility.displayMessage;

public class RecipeActivity extends AppCompatActivity
        implements RecipeAdapter.onOptionClickListener {

    public static final String RECIPES_ID = "recipes_id";
    public static final String INGREDIENT_EXTRA = "ingredient_extra";
    public static final String STEP_EXTRA = "step_extra";
    public static final String RECIPE_NAME = "recipe_name";
    public static final String RECIPES_TITLE = "Recipes";

    @BindView(R.id.recipe_recycler_view)
    RecyclerView recipeRecyclerView;
    @BindView(R.id.recipe_progress_bar)
    ProgressBar mProgressBar;

    private RecipeAdapter recipeAdapter;
    private LinearLayoutManager layoutManager;
    private ArrayList<Recipe> recipes;

    // The Idling Resource which will be null in production.
    @Nullable
    private SimpleIdlingResource mIdlingResource;

    /**
     * Only called from test, creates and returns a new {@link SimpleIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_screen_layout);
        ButterKnife.bind(RecipeActivity.this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setTitle(RECIPES_TITLE);

        boolean tabView = false;
        if (findViewById(R.id.recipe_list_on_tab) != null) tabView = true;
        if (tabView) {
            //set the layout of the recycler view to be grid layout
            layoutManager = new GridLayoutManager(this, 2);
        } else {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                layoutManager = new GridLayoutManager(this, 2);
            else
                layoutManager = new LinearLayoutManager(this);
        }
        recipeRecyclerView.setLayoutManager(layoutManager);
        recipeRecyclerView.setHasFixedSize(true);

        if (savedInstanceState != null) {
            recipes = savedInstanceState.getParcelableArrayList(RECIPES_ID);
            sail();
            return;
        }
        getIdlingResource();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            if (Utility.isOnline(this))
                callToApi();
            else {
                //if recipes is available locally, load it
                if (checkRecipesLocally()) {
                    loadRecipeLocally();
                } else {
                    //try api again
                    callToApi();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //if we do have internet, load remotely otherwise load locally
        if (Util.SDK_INT <= 23) {
            if (Utility.isOnline(this))
                callToApi();
            else {
                //if recipes is available locally, load it
                if (checkRecipesLocally()) {
                    loadRecipeLocally();
                } else {
                    //try api again
                    callToApi();
                }
            }
        }
    }

    private void loadRecipeLocally() {
        AsyncTask<Void, Void, ArrayList<Recipe>> task = new AsyncTask<Void, Void, ArrayList<Recipe>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //set the idle state to false before
                /**
                 * The IdlingResource is null in production as set by the @Nullable annotation which means
                 * the value is allowed to be null.
                 *
                 * If the idle state is true, Espresso can perform the next action.
                 * If the idle state is false, Espresso will wait until it is true before
                 * performing the next action.
                 */
                if (mIdlingResource != null) {
                    mIdlingResource.setIdleState(false);
                }
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected ArrayList<Recipe> doInBackground(Void... params) {
                //get the recipes first
                ArrayList<Recipe> recipes = new ArrayList<>();
                Cursor cursor = null;
                try {
                    cursor = getContentResolver().query(RecipeContentProvider.Recipes.CONTENT_URI, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        for (int i = 0; i < cursor.getCount(); i++) {
                            Recipe recipe = new Recipe();
                            cursor.moveToPosition(i);
                            int id = cursor.getInt(cursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_ID));
                            String name = cursor.getString(cursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME));
                            String imageUrl = cursor.getString(cursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_IMAGE_URL));
                            int servings = cursor.getInt(cursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_SERVINGS));
                            recipe.setId(id);
                            recipe.setName(name);
                            recipe.setImage(imageUrl);
                            recipe.setServings(servings);
                            //load ingredient for this id
                            Cursor cursor1 = null;
                            ArrayList<Ingredient> ingredients = new ArrayList<>();
                            try {
                                cursor1 = getContentResolver().query(RecipeContentProvider.RecipeIngredients.withId(id), null, null, null, null);

                                if (cursor1 != null && cursor1.moveToFirst()) {
                                    for (int j = 0; j < cursor1.getCount(); j++) {
                                        Ingredient ingredient = new Ingredient();
                                        cursor1.moveToPosition(j);
                                        String ingredientName = cursor1.getString(cursor1.getColumnIndex(RecipeContract.RecipeIngredientsEntry.COLUMN_INGREDIENT_NAME));
                                        String measure = cursor1.getString(cursor1.getColumnIndex(RecipeContract.RecipeIngredientsEntry.COLUMN_MEASURE));
                                        double quantity = cursor1.getDouble(cursor1.getColumnIndex(RecipeContract.RecipeIngredientsEntry.COLUMN_QUANTITY));
                                        ingredient.setIngredient(ingredientName);
                                        ingredient.setMeasure(measure);
                                        ingredient.setQuantity(quantity);
                                        ingredients.add(ingredient);

                                    }
                                    recipe.setIngredients(ingredients);
                                }
                            } finally {
                                if (cursor1 != null) cursor1.close();
                            }
                            //load steps for this recipe
                            ArrayList<Step> steps = new ArrayList<>();
                            Cursor cursor2 = null;
                            try {
                                cursor2 = getContentResolver().query(RecipeContentProvider.RecipeSteps.withId(id), null, null, null, null);
                                if (cursor2 != null && cursor2.moveToFirst()) {
                                    for (int k = 0; k < cursor2.getCount(); k++) {
                                        Step step = new Step();
                                        cursor2.moveToPosition(k);
                                        int stepId = cursor2.getInt(cursor2.getColumnIndex(RecipeContract.RecipeStepsEntry.COLUMN_STEP_ID));
                                        String shortDesc = cursor2.getString(cursor2.getColumnIndex(RecipeContract.RecipeStepsEntry.COLUMN_SHORT_DESC));
                                        String longDesc = cursor2.getString(cursor2.getColumnIndex(RecipeContract.RecipeStepsEntry.COLUMN_LONG_DESC));
                                        String videoUrl = cursor2.getString(cursor2.getColumnIndex(RecipeContract.RecipeStepsEntry.COLUMN_VIDEO_URL));
                                        String thumbnailUrl = cursor2.getString(cursor2.getColumnIndex(RecipeContract.RecipeStepsEntry.COLUMN_THUMBNAIL_URL));
                                        step.setId(stepId);
                                        step.setShortDescription(shortDesc);
                                        step.setDescription(longDesc);
                                        step.setVideoURL(videoUrl);
                                        step.setThumbnailURL(thumbnailUrl);
                                        steps.add(step);
                                    }
                                    recipe.setSteps(steps);
                                }
                            } finally {
                                if (cursor2 != null)
                                    cursor2.close();
                            }
                            recipes.add(recipe);
                        }
                    }
                    return recipes;
                } finally {
                    if (cursor != null)
                        cursor.close();
                }
            }

            @Override
            protected void onPostExecute(ArrayList<Recipe> recipes) {
                super.onPostExecute(recipes);
                mProgressBar.setVisibility(View.INVISIBLE);
                RecipeActivity.this.recipes = recipes;
                sail();
            }
        };

        task.execute();
    }

    private void sail() {
        recipeAdapter = new RecipeAdapter(this, recipes, this);
        recipeAdapter.notifyDataSetChanged();
        recipeRecyclerView.setAdapter(recipeAdapter);
        //once we give the recycler view its adapter, we set the idle state to true so that test the can run
        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(true);
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        recipes = savedInstanceState.getParcelableArrayList(RECIPES_ID);
        sail();
    }

    public void callToApi() {

        if (!Utility.isOnline(this)) {
            String title = "Connection";
            String message = "No internet connection. Please try again.";
            displayMessage(this, title, message);
            return;
        }
        mProgressBar.setVisibility(View.VISIBLE);
        /**
         * The IdlingResource is null in production as set by the @Nullable annotation which means
         * the value is allowed to be null.
         *
         * If the idle state is true, Espresso can perform the next action.
         * If the idle state is false, Espresso will wait until it is true before
         * performing the next action.
         */
        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(false);
        }
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
                        sail();
                        //save into database if data is not available locally
                        if (!checkRecipesLocally())
                            saveRecipesLocally();
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

    private boolean checkRecipesLocally() {
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(RecipeContentProvider.Recipes.CONTENT_URI, null, null, null, null);
            if (cursor != null && cursor.moveToFirst())
                return true;
            else
                return false;
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    private void saveRecipesLocally() {
        //perform insertion operation in a background thread
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                for (Recipe recipe : recipes) {
                    int recipeId = recipe.getId();
                    String recipeName = recipe.getName();
                    String recipeImage = recipe.getImage();
                    int servings = recipe.getServings();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(RecipeContract.RecipeEntry.COLUMN_ID, recipeId);
                    contentValues.put(RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME, recipeName);
                    contentValues.put(RecipeContract.RecipeEntry.COLUMN_IMAGE_URL, recipeImage);
                    contentValues.put(RecipeContract.RecipeEntry.COLUMN_SERVINGS, servings);
                    getContentResolver().insert(RecipeContentProvider.Recipes.CONTENT_URI, contentValues);
                    //insert the ingredients for the current recipe
                    ArrayList<Ingredient> ingredients = recipe.getIngredients();
                    for (Ingredient ingredient : ingredients) {
                        ContentValues contentValues1 = new ContentValues();
                        contentValues1.put(RecipeContract.RecipeIngredientsEntry.COLUMN_RECIPE_ID, recipeId);
                        contentValues1.put(RecipeContract.RecipeIngredientsEntry.COLUMN_INGREDIENT_NAME, ingredient.getIngredient());
                        contentValues1.put(RecipeContract.RecipeIngredientsEntry.COLUMN_MEASURE, ingredient.getMeasure());
                        contentValues1.put(RecipeContract.RecipeIngredientsEntry.COLUMN_QUANTITY, ingredient.getQuantity());
                        getContentResolver().insert(RecipeContentProvider.RecipeIngredients.CONTENT_URI, contentValues1);
                    }
                    //insert the steps for the current recipe
                    ArrayList<Step> steps = recipe.getSteps();
                    for (Step step : steps) {
                        ContentValues contentValues2 = new ContentValues();
                        contentValues2.put(RecipeContract.RecipeStepsEntry.COLUMN_RECIPE_ID, recipeId);
                        contentValues2.put(RecipeContract.RecipeStepsEntry.COLUMN_STEP_ID, step.getId());
                        contentValues2.put(RecipeContract.RecipeStepsEntry.COLUMN_SHORT_DESC, step.getShortDescription());
                        contentValues2.put(RecipeContract.RecipeStepsEntry.COLUMN_LONG_DESC, step.getDescription());
                        contentValues2.put(RecipeContract.RecipeStepsEntry.COLUMN_VIDEO_URL, step.getVideoURL());
                        contentValues2.put(RecipeContract.RecipeStepsEntry.COLUMN_THUMBNAIL_URL, step.getThumbnailURL());
                        getContentResolver().insert(RecipeContentProvider.RecipeSteps.CONTENT_URI, contentValues2);
                    }

                }
                return null;
            }
        };
        task.execute();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //to avoid any reload when orientation is changed
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(RECIPES_ID, recipes);

    }

    @Override
    public void onIngredientClick(int position) {
        //create an explicit intent to launch the ingredient activity
        Intent intent = new Intent(RecipeActivity.this, IngredientActivity.class);
        ArrayList<Ingredient> ingredients = recipes.get(position).getIngredients();
        if (ingredients != null && ingredients.size() > 0) {
            intent.putParcelableArrayListExtra(INGREDIENT_EXTRA, ingredients);
            intent.putExtra(RECIPE_NAME, recipes.get(position).getName());
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
        launchStepDetails(position);
    }

    @Override
    public void onCardViewClick(int position) {
        launchStepDetails(position);
    }

    public void launchStepDetails(int position) {
        Intent intent = new Intent(RecipeActivity.this, StepActivity.class);
        Recipe recipe = recipes.get(position);
        ArrayList<Step> steps = recipes.get(position).getSteps();
        //grab the ingredients also
        ArrayList<Ingredient> ingredients = recipes.get(position).getIngredients();

        //any clicked recipe becomes the desired recipe and thus update the widget appropriately
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putInt(RECIPES_ID, recipe.getId()).apply();
        RecipeService.startActionUpdateRecipeWidgets(this);
        if (steps != null && steps.size() > 0) {
            intent.putParcelableArrayListExtra(STEP_EXTRA, steps);
            intent.putExtra(RECIPE_NAME, recipe.getName());

        } else {
            Toast toast = Toast.makeText(this, "No Steps for Recipe.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        if (ingredients != null && ingredients.size() > 0) {
            intent.putParcelableArrayListExtra(INGREDIENT_EXTRA, ingredients);
            intent.putExtra(RECIPE_NAME, recipe.getName());
        } else {
            Toast toast = Toast.makeText(this, "No Ingredients for Recipe.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        startActivity(intent);
    }
}
