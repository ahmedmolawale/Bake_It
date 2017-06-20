package com.android.example.bakingapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.example.bakingapp.R;
import com.android.example.bakingapp.model.Step;

import java.util.ArrayList;

/**
 * Created by root on 6/13/17.
 */

public class StepsFragmentAdapter extends RecyclerView.Adapter<StepsFragmentAdapter.CustomViewHolder> {

    private Context context;
    private ArrayList<Step> recipeSteps;
    private OnListItemClickListener onListItemClickListener;

    public StepsFragmentAdapter(Context context, ArrayList<Step> recipeSteps, OnListItemClickListener onListItemClickListener) {
        this.context = context;
        this.recipeSteps = recipeSteps;
        this.onListItemClickListener = onListItemClickListener;
    }

    //interface to handle clicks
    public interface OnListItemClickListener {
        void onListItemClick(View v, int position);
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int step_item_layout = R.layout.fragment_recipe_step_item;
        Context context = parent.getContext();
        boolean attachToParentImmediately = false;

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View itemView = layoutInflater.inflate(step_item_layout, parent, attachToParentImmediately);
        CustomViewHolder customViewHolder = new CustomViewHolder(itemView);
        return customViewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return this.recipeSteps.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView stepShorDesc;

        public CustomViewHolder(View itemView) {
            super(itemView);
            stepShorDesc = (TextView) itemView.findViewById(R.id.recipe_step_short_desc);
            itemView.setOnClickListener(this);
        }

        public void bind(final int position) {
            String stepShortDes = recipeSteps.get(position).getShortDescription();

            this.stepShorDesc.setText(stepShortDes);


        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            onListItemClickListener.onListItemClick(v, position);
        }
    }
}
