package com.android.example.bakingapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.example.bakingapp.R;
import com.android.example.bakingapp.model.Ingredient;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.CustomViewHolder> {


    private ArrayList<Ingredient> ingredients;

    public IngredientAdapter(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int ingredient_item_layout = R.layout.ingredient_item;
        Context context = parent.getContext();
        boolean attachToParentImmediately = false;

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View itemView = layoutInflater.inflate(ingredient_item_layout, parent, attachToParentImmediately);
        return new CustomViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return this.ingredients.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ingredient_name)
        TextView ingredientName;
        @BindView(R.id.quantity_value)
        TextView quantity;
        @BindView(R.id.measure_value)
        TextView measure;

        public CustomViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(int position) {
            String ingredientName = ingredients.get(position).getIngredient();
            double quantity = ingredients.get(position).getQuantity();
            String measure = ingredients.get(position).getMeasure();

            this.ingredientName.setText(ingredientName);
            this.quantity.setText(String.valueOf(quantity));
            this.measure.setText(measure);
        }
    }
}
