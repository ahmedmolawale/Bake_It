package com.android.example.bakingapp.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.android.example.bakingapp.R;
import com.android.example.bakingapp.activity.RecipeActivity;
import com.android.example.bakingapp.data.RecipeContentProvider;
import com.android.example.bakingapp.data.RecipeContract;
import com.android.example.bakingapp.util.Utility;

/**
 * Created by root on 6/16/17.
 */

public class RecipeService extends IntentService {

    private static final String ACTION_UPDATE_RECIPE_WIDGET = "com.android.example.baking.update_recipe_widget";
    public RecipeService(){
        super(RecipeService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        String action = intent.getAction();
        if (ACTION_UPDATE_RECIPE_WIDGET.equals(action)) {
            handleActionUpdateRecipeWidgets();
        }
    }

    private void handleActionUpdateRecipeWidgets() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RecipeWidgetProvider.class));
        //grab the recipe name and name and pass to update widgetRecipeWidget

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int id = sharedPreferences.getInt(RecipeActivity.RECIPES_ID,-1);
        Cursor cursor = getContentResolver().query(RecipeContentProvider.Recipes.withId(id),
                null,null,null,null);
        String recipeName = null;
        if(cursor != null && cursor.moveToFirst()){
            recipeName = cursor.getString(cursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME));
        }

        //recipe id starts from 1 while position starts from zero
        int imgRes = Utility.loadImageRes(id-1);
        //Trigger data update to handle the GridView widgets and force a data refresh
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.list_view_widget);
        //Now update all widgets
        RecipeWidgetProvider.updateRecipeWidgets(this, appWidgetManager,appWidgetIds,recipeName,imgRes);


    }
    public static void startActionUpdateRecipeWidgets(Context context) {
        Intent intent = new Intent(context, RecipeService.class);
        intent.setAction(ACTION_UPDATE_RECIPE_WIDGET);
        context.startService(intent);
    }
}
