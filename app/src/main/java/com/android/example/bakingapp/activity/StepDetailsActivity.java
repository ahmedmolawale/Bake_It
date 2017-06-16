package com.android.example.bakingapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.android.example.bakingapp.R;
import com.android.example.bakingapp.fragment.StepDescFragment;
import com.android.example.bakingapp.fragment.StepNavFragment;
import com.android.example.bakingapp.fragment.StepVideoFragment;
import com.android.example.bakingapp.model.Step;

import java.util.ArrayList;

public class StepDetailsActivity extends AppCompatActivity implements StepNavFragment.OnNavButtonClickListener {

    private ArrayList<Step> steps;
    private int position;
    private final String STEPS = "steps";
    private final String POSITION = "position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step_details_layout);

        ActionBar actionBar = getSupportActionBar();


        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT) && intent.hasExtra(StepActivity.POSITION_EXTRA)) {
            steps = intent.getParcelableArrayListExtra(Intent.EXTRA_TEXT);
            position = intent.getIntExtra(StepActivity.POSITION_EXTRA, -1);
        }
        if(savedInstanceState != null){
            steps = savedInstanceState.getParcelableArrayList(STEPS);
            position = savedInstanceState.getInt(POSITION);
        }
        if(actionBar!=null){
            actionBar.setTitle("Baking...");
        }
        if (findViewById(R.id.step_detail_landscape) != null) {
            //landscape mode
            Log.d(StepDetailsActivity.class.getSimpleName(),"CALLLLLLLLLLLEEEEEDDDDD");
            StepVideoFragment stepVideoFragment = new StepVideoFragment();
            stepVideoFragment.setPlayerStepVideoUrl(this.steps.get(position).getVideoURL());
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.step_video_container, stepVideoFragment)
                    .commit();

        } else {


            StepVideoFragment stepVideoFragment = new StepVideoFragment();
            stepVideoFragment.setPlayerStepVideoUrl(this.steps.get(position).getVideoURL());
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.step_video_container, stepVideoFragment)
                    .commit();

            StepDescFragment stepDescFragment = new StepDescFragment();
            stepDescFragment.setDescStep(this.steps.get(position));
            fragmentManager.beginTransaction()
                    .add(R.id.step_desc_container, stepDescFragment)
                    .commit();

            StepNavFragment stepNavFragment = new StepNavFragment();
            stepNavFragment.setUpNav(this.steps, position);
            fragmentManager.beginTransaction()
                    .add(R.id.step_nav_container, stepNavFragment)
                    .commit();

        }
    }

    @Override
    public void onPrevButtonClickListener(int currentPosition) {
         position = --currentPosition;
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
        StepNavFragment stepNavFragment = new StepNavFragment();
        stepNavFragment.setUpNav(this.steps, position);
        fragmentManager.beginTransaction()
                .replace(R.id.step_nav_container, stepNavFragment)
                .commit();

    }

    @Override
    public void onNextButtonClickListener(int currentPosition) {
         position = ++currentPosition;
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
        StepNavFragment stepNavFragment = new StepNavFragment();
        stepNavFragment.setUpNav(this.steps, position);
        fragmentManager.beginTransaction()
                .replace(R.id.step_nav_container, stepNavFragment)
                .commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STEPS,steps);
        outState.putInt(POSITION,position);
    }
}
