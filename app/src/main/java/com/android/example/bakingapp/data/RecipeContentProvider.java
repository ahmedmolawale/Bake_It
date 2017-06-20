package com.android.example.bakingapp.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Uses the Schematic (https://github.com/SimonVT/schematic) library to define the columns in a
 * content provider baked by a database
 */

@ContentProvider(authority = RecipeContentProvider.AUTHORITY, database = RecipeDatabase.class)
public class RecipeContentProvider {

    public static final String AUTHORITY = "com.android.example.bakingapp.provider";


    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    //paths for accessing data...corresponding to the table names, basically
    interface Path {
        String RECIPES = "recipes";
        String RECIPE_INGREDIENTS = "recipe_ingredients";
        String RECIPE_STEPS = "recipe_steps";
    }

    private static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = RecipeDatabase.RECIPES)
    public static class Recipes {

        @ContentUri(
                path = Path.RECIPES,
                type = "vnd.android.cursor.dir/recipe",
                defaultSort = RecipeContract.RecipeEntry.COLUMN_ID + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.RECIPES);

        @InexactContentUri(
                path = Path.RECIPES + "/#",
                name = "COLUMN_ID",
                type = "vnd.android.cursor.item/recipe",
                whereColumn = RecipeContract.RecipeEntry.COLUMN_ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(Path.RECIPES, String.valueOf(id));
        }
    }

    @TableEndpoint(table = RecipeDatabase.RECIPE_INGREDIENTS)
    public static class RecipeIngredients {

        @ContentUri(
                path = Path.RECIPE_INGREDIENTS,
                type = "vnd.android.cursor.dir/recipe_ingredient")
        public static final Uri CONTENT_URI = buildUri(Path.RECIPE_INGREDIENTS);

        @InexactContentUri(
                path = Path.RECIPE_INGREDIENTS + "/#",
                name = "RECIPE_ID",
                type = "vnd.android.cursor.item/recipe_ingredient",
                whereColumn = RecipeContract.RecipeIngredientsEntry.COLUMN_RECIPE_ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(Path.RECIPE_INGREDIENTS, String.valueOf(id));
        }
    }

    @TableEndpoint(table = RecipeDatabase.RECIPE_STEPS)
    public static class RecipeSteps {

        @ContentUri(
                path = Path.RECIPE_STEPS,
                type = "vnd.android.cursor.dir/recipe_step")
        public static final Uri CONTENT_URI = buildUri(Path.RECIPE_STEPS);

        @InexactContentUri(
                path = Path.RECIPE_STEPS + "/#",
                name = "RECIPE_ID",
                type = "vnd.android.cursor.item/recipe_step",
                whereColumn = RecipeContract.RecipeStepsEntry.COLUMN_RECIPE_ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(Path.RECIPE_STEPS, String.valueOf(id));
        }
    }


}
