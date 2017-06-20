package com.android.example.bakingapp.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.android.example.bakingapp.R;
import com.android.example.bakingapp.activity.RecipeActivity;
import com.android.example.bakingapp.data.RecipeContentProvider;
import com.android.example.bakingapp.data.RecipeContract;

/**
 * Created by root on 6/17/17.
 */

public class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private Cursor mCursor;

    public ListRemoteViewsFactory(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        //retrieve the
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        int id = sharedPreferences.getInt(RecipeActivity.RECIPES_ID, -1);
        //get the ingredients of the recipe
        mCursor = mContext.getContentResolver().query(RecipeContentProvider.RecipeIngredients.withId(id),
                null, null, null, null);

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    /**
     * This method acts like the onBindViewHolder method in an Adapter
     *
     * @param position The current position of the item in the GridView to be displayed
     * @return The RemoteViews object to display for the provided postion
     */

    @Override
    public RemoteViews getViewAt(int position) {
        //we build each list item here...its a RemoteViews

        if (mCursor == null || mCursor.getCount() == 0) return null;
        mCursor.moveToPosition(position);

        String ingredientName = mCursor.getString(mCursor.getColumnIndex(RecipeContract.RecipeIngredientsEntry.COLUMN_INGREDIENT_NAME));
        String measure = mCursor.getString(mCursor.getColumnIndex(RecipeContract.RecipeIngredientsEntry.COLUMN_MEASURE));
        double quantity = mCursor.getDouble(mCursor.getColumnIndex(RecipeContract.RecipeIngredientsEntry.COLUMN_QUANTITY));

        //get the RemoteViews
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.ingredient_widget);
        StringBuilder ingredientDetail = new StringBuilder();
        ingredientDetail.append(ingredientName)
                .append(" (" + quantity)
                .append(" " + measure + " )");

        remoteViews.setTextViewText(R.id.ingredient_widget, ingredientName);
        remoteViews.setTextViewText(R.id.measure_widget, measure);
        remoteViews.setTextViewText(R.id.quatity_widget, String.valueOf(quantity));
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
