package com.android.example.bakingapp.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.example.bakingapp.R;
import com.android.example.bakingapp.model.Recipe;
import com.android.example.bakingapp.util.Utility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.CustomViewHolder> {

    private ArrayList<Recipe> recipes;
    private Context context;
    private onOptionClickListener onOptionClickListener;

    public RecipeAdapter(Context context, ArrayList<Recipe> recipes, onOptionClickListener onOptionClickListener) {
        this.context = context;
        this.recipes = recipes;
        this.onOptionClickListener = onOptionClickListener;
    }

    public interface onOptionClickListener {
        void onIngredientClick(int position);

        void onStepsClick(int position);

        void onCardViewClick(int position);
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int recipe_item_layout = R.layout.recipe_item;
        Context context = parent.getContext();
        boolean attachToParentImmediately = false;

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View itemView = layoutInflater.inflate(recipe_item_layout, parent, attachToParentImmediately);
        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return this.recipes.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.recipe_name)
        TextView recipeName;
        @BindView(R.id.recipe_image)
        ImageView recipeImage;
        @BindView(R.id.recipe_ingredient)
        Button recipeIngredient;
        @BindView(R.id.recipe_steps)
        Button recipeSteps;
        @BindView(R.id.recipe_card_view)
        CardView cardView;


        public CustomViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        public void bind(final int position) {

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onOptionClickListener.onCardViewClick(position);
                }
            });
            recipeIngredient.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onOptionClickListener.onIngredientClick(position);
                }
            });
            recipeSteps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onOptionClickListener.onStepsClick(position);
                }
            });

            String recipeName = recipes.get(position).getName();
            String recipeImageUrl = recipes.get(position).getImage();
            this.recipeName.setText(recipeName);
            if (!recipeImageUrl.equals("")) loadImage(recipeImageUrl);
            else
                recipeImage.setImageBitmap(Utility.loadImageAsset(context, position));
        }

        private void loadImage(String recipeImageUrl) {
            //use Picasso to load the image
            Picasso.with(context).load(recipeImageUrl)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.drawable.action_error)
                    .into(recipeImage);
        }
    }
}
