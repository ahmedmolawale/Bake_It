package com.android.example.bakingapp.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
/**
 * Uses the Schematic (https://github.com/SimonVT/schematic) library to define the columns in a
 * content provider baked by a database
 */

public class RecipeContract {



    public final class RecipeEntry{

        @DataType(DataType.Type.INTEGER)
        @PrimaryKey(onConflict = ConflictResolutionType.REPLACE)
        public static final String COLUMN_ID = "recipe_id";

        @DataType(DataType.Type.TEXT)
        @NotNull
        public static final String COLUMN_RECIPE_NAME = "recipe_name";

        @DataType(DataType.Type.TEXT)
        public static final String COLUMN_IMAGE_URL = "imageurl";

        @DataType(DataType.Type.INTEGER)
        public static final String COLUMN_SERVINGS = "servings";

    }


    public final class RecipeIngredientsEntry{

        @DataType(DataType.Type.INTEGER)
        @PrimaryKey(onConflict = ConflictResolutionType.REPLACE)
        @AutoIncrement
        public static final String COLUMN_ID = "_id";

        @DataType(DataType.Type.INTEGER)
        public static final String COLUMN_RECIPE_ID = "recipe_id";

        @DataType(DataType.Type.TEXT)
        @NotNull
        public static final String COLUMN_INGREDIENT_NAME = "ingredient_name";

        @DataType(DataType.Type.TEXT)
        @NotNull
        public static final String COLUMN_MEASURE = "measure";

        @DataType(DataType.Type.REAL)
        @NotNull
        public static final String COLUMN_QUANTITY = "quantity";

    }

    public final class RecipeStepsEntry{

        @DataType(DataType.Type.INTEGER)
        @PrimaryKey(onConflict = ConflictResolutionType.REPLACE)
        @AutoIncrement
        public static final String COLUMN_ID = "_id";

        @DataType(DataType.Type.INTEGER)
        @NotNull
        public static final String COLUMN_RECIPE_ID = "recipe_id";

        @DataType(DataType.Type.INTEGER)
        @NotNull
        public static final String COLUMN_STEP_ID = "step_id";

        @DataType(DataType.Type.TEXT)
        public static final String COLUMN_SHORT_DESC = "short_desc";

        @DataType(DataType.Type.TEXT)
        public static final String COLUMN_LONG_DESC = "long_desc";

        @DataType(DataType.Type.TEXT)
        public static final String COLUMN_THUMBNAIL_URL = "thumbnail_url";

        @DataType(DataType.Type.TEXT)
        public static final String COLUMN_VIDEO_URL = "video_url";

    }
}
