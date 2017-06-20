package com.android.example.bakingapp.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.android.example.bakingapp.R;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId,String recipeName,int imgRes) {
        // Construct the RemoteViews object
        RemoteViews views = getListRemoteViews(context,recipeName,imgRes);
        //views.setTextViewText(R.id.appwidget_text, widgetText);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        RecipeService.startActionUpdateRecipeWidgets(context);
    }
    public static void updateRecipeWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds,String recipeName,int imgRes) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId,recipeName,imgRes);
        }
    }

    private static RemoteViews getListRemoteViews(Context context,String recipeName,int imgRes) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_list_view);
        //create an adapter for the remoteviews by creating an intent for the service
        Intent intent = new Intent(context, ListWidgetService.class);
        remoteViews.setRemoteAdapter(R.id.list_view_widget, intent);
        remoteViews.setEmptyView(R.id.list_view_widget, R.id.empty_view);
        remoteViews.setTextViewText(R.id.recipe_name_widget,recipeName);
        remoteViews.setImageViewResource(R.id.recipe_image_widget,imgRes);
        return remoteViews;
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

