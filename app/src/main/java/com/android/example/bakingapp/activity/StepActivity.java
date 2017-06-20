package com.android.example.bakingapp.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.android.example.bakingapp.R;
import com.android.example.bakingapp.adapter.StepsFragmentAdapter;
import com.android.example.bakingapp.fragment.StepDescFragment;
import com.android.example.bakingapp.fragment.StepVideoFragment;
import com.android.example.bakingapp.model.Step;

import java.util.ArrayList;

import static com.android.example.bakingapp.activity.RecipeActivity.RECIPE_NAME;

public class StepActivity extends AppCompatActivity implements StepsFragmentAdapter.OnListItemClickListener {

    public static final String POSITION_EXTRA = "position_extra";
    private static final String BAKING_STEPS = " Baking Steps";
    private boolean tabView = false;
    private ArrayList<Step> steps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step_activity_layout);
        ActionBar actionBar = getSupportActionBar();

        String recipeName = "";
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(RecipeActivity.STEP_EXTRA)) {
            steps = intent.getParcelableArrayListExtra(RecipeActivity.STEP_EXTRA);
            recipeName = intent.getStringExtra(RECIPE_NAME);
        }
        if (actionBar != null) {
            actionBar.setTitle(recipeName + BAKING_STEPS);
        }
        if (findViewById(R.id.step_activity_tab) != null) {
            //tablet view
            tabView = true;
            //load the recipe at position 0
            StepVideoFragment stepVideoFragment = new StepVideoFragment();
            stepVideoFragment.setPlayerStepVideoUrl(this.steps.get(0).getVideoURL());
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.step_video_container, stepVideoFragment)
                    .commit();

            StepDescFragment stepDescFragment = new StepDescFragment();
            stepDescFragment.setDescStep(this.steps.get(0));
            fragmentManager.beginTransaction()
                    .replace(R.id.step_desc_container, stepDescFragment)
                    .commit();

        } else {
            //not tab view but landscape
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                //set the ingredient view to gone
                findViewById(R.id.ingredient_list_on_step).setVisibility(View.GONE);
            }


        }

    }


    @Override
    public void onListItemClick(View v, int position) {
        if (steps != null && steps.size() > 0) {
            if (tabView) {
                StepVideoFragment stepVideoFragment = new StepVideoFragment();
                stepVideoFragment.setPlayerStepVideoUrl(this.steps.get(position).getVideoURL());
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.step_video_container, stepVideoFragment)
                        .commit();

                StepDescFragment stepDescFragment = new StepDescFragment();
                stepDescFragment.setDescStep(this.steps.get(position));
                fragmentManager.beginTransaction()
                        .replace(R.id.step_desc_container, stepDescFragment)
                        .commit();

            } else {
                //Toast.makeText(getApplicationContext(), "Step - " + step.getShortDescription(), Toast.LENGTH_LONG).show();
                //launch the step detail activity
                Intent intent = new Intent(StepActivity.this, StepDetailsActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, steps);
                intent.putExtra(POSITION_EXTRA, position);
                startActivity(intent);
            }
        }
    }
}
