package com.android.example.bakingapp;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.view.View;

import com.android.example.bakingapp.activity.RecipeActivity;
import com.android.example.bakingapp.idlingresource.SimpleIdlingResource;
import com.android.example.bakingapp.model.Ingredient;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
@RunWith(AndroidJUnit4.class)
public class RecipeActivityTest {

    @Rule
    public ActivityTestRule<RecipeActivity> mRecipeActivityActivityTestRule
            = new ActivityTestRule<>(RecipeActivity.class);

    public IdlingResource mIdlingResource;
    int itemPosition = 0;

    @Before
    public void registerIdlingResource(){

        mIdlingResource = mRecipeActivityActivityTestRule.getActivity().getIdlingResource();
        //register the idling resource
        Espresso.registerIdlingResources(mIdlingResource);
    }

    @Test
    public void recyclerView_item_click(){

        String firstPie = mRecipeActivityActivityTestRule.getActivity().getResources().getString(R.string.first_pie);

        String ingredient = mRecipeActivityActivityTestRule.getActivity().getResources().getString(R.string.ingredient_button);
        String step = mRecipeActivityActivityTestRule.getActivity().getResources().getString(R.string.steps_button);

        onView(withId(R.id.recipe_recycler_view))
                .perform(RecyclerViewActions.scrollToPosition(itemPosition))
                .check(matches(hasDescendant(withText(firstPie))));

        onView(withId(R.id.recipe_recycler_view))
                .perform(RecyclerViewActions.scrollToPosition(itemPosition))
                .check(matches(hasDescendant(withText(ingredient))));

        onView(withId(R.id.recipe_recycler_view))
                .perform(RecyclerViewActions.scrollToPosition(itemPosition))
                .check(matches(hasDescendant(withText(step))));
    }

    @Test
    public void recyclerView_item_onClick(){
        //testing recylcer views
        onView(withId(R.id.recipe_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(itemPosition, click()));
        String itemElementText = mRecipeActivityActivityTestRule.getActivity().getResources().getString(
                R.string.recipe_ingredient);
        onView(withText(itemElementText)).check(matches(isDisplayed()));
    }


    @Test
    public void click_item_in_view_holder(){
        String measureLabel = mRecipeActivityActivityTestRule.getActivity().getResources().getString(R.string.measure_label);
        String quantityLabel  = mRecipeActivityActivityTestRule.getActivity().getResources().getString(R.string.quantity_label);
        onView(withId(R.id.recipe_recycler_view))
                .perform(
                        RecyclerViewActions.actionOnItemAtPosition(
                                itemPosition,
                                new ViewAction() {
                                    @Override
                                    public Matcher<View> getConstraints() {
                                        return null;
                                    }

                                    @Override
                                    public String getDescription() {
                                        return "Click on specific button";
                                    }
                                    @Override
                                    public void perform(UiController uiController, View view) {
                                        View button = view.findViewById(R.id.recipe_ingredient);
                                        button.performClick();
                                    }
                                })
                );

        //check for quantity and measure label on the ingredient activity
        onView(withId(R.id.ingredient_recycler_view))
                .perform(RecyclerViewActions.scrollToPosition(itemPosition))
                .check(matches(hasDescendant(withText(measureLabel))));
        onView(withId(R.id.ingredient_recycler_view))
                .perform(RecyclerViewActions.scrollToPosition(itemPosition))
                .check(matches(hasDescendant(withText(quantityLabel))));
    }
    // Remember to unregister resources when not needed to avoid malfunction.
    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            Espresso.unregisterIdlingResources(mIdlingResource);
        }
    }
}