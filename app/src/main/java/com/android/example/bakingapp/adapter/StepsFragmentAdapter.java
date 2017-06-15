package com.android.example.bakingapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.example.bakingapp.R;
import com.android.example.bakingapp.model.Step;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by root on 6/13/17.
 */

public class StepsFragmentAdapter extends RecyclerView.Adapter<StepsFragmentAdapter.CustomViewHolder> {

    Context context;
    ArrayList<Step> recipeSteps;
    OnListItemClickListener onListItemClickListener;

    public StepsFragmentAdapter(Context context,ArrayList<Step> recipeSteps, OnListItemClickListener onListItemClickListener) {
        this.context = context;
        this.recipeSteps = recipeSteps;
        this.onListItemClickListener = onListItemClickListener;
    }

    //interface to handle clicks
    public interface OnListItemClickListener {
        void onListItemClick(int position);
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
        private ImageView stepImage;

        public CustomViewHolder(View itemView) {
            super(itemView);
            stepShorDesc = (TextView) itemView.findViewById(R.id.recipe_step_short_desc);
            stepImage = (ImageView) itemView.findViewById(R.id.recipe_step_image);
            itemView.setOnClickListener(this);
        }

        public void bind(final int position) {
            String stepShortDes = recipeSteps.get(position).getShortDescription();
            String stepThumbnailURL = recipeSteps.get(position).getThumbnailURL();

            this.stepShorDesc.setText(stepShortDes);
            if (!stepThumbnailURL.equals("")) loadImage(stepThumbnailURL);

        }
        private void loadImage(String url) {
            //use Picasso to load the image
            Picasso.with(context).load(url)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.drawable.action_error)
                    .into(stepImage);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            onListItemClickListener.onListItemClick(position);
        }
    }
}
