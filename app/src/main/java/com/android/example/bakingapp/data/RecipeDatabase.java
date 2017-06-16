package com.android.example.bakingapp.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Uses the Schematic (https://github.com/SimonVT/schematic) library to create a database with one
 * table for messages
 */

@Database(version = RecipeDatabase.VERSION)
public class RecipeDatabase {


    public static final int VERSION = 1;

    @Table(RecipeContract.RecipeEntry.class)
    public static final String RECIPES = "recipes";

    @Table(RecipeContract.RecipeIngredientsEntry.class)
    public static final String RECIPE_INGREDIENTS = "recipe_ingredients";

    @Table(RecipeContract.RecipeStepsEntry.class)
    public static final String RECIPE_STEPS = "recipe_steps";
}
