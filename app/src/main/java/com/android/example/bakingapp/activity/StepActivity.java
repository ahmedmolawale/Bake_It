package com.android.example.bakingapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.android.example.bakingapp.R;
import com.android.example.bakingapp.adapter.StepsFragmentAdapter;
import com.android.example.bakingapp.fragment.StepDescFragment;
import com.android.example.bakingapp.fragment.StepVideoFragment;
import com.android.example.bakingapp.model.Step;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

public class StepActivity extends AppCompatActivity implements StepsFragmentAdapter.OnListItemClickListener {

    public static final String POSITION_EXTRA = "position_extra";
    private boolean tabView = false;
    private ArrayList<Step> steps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step_activity_layout);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle("Recipe Baking Steps");
        }
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(RecipeActivity.STEP_EXTRA)) {
            steps = intent.getParcelableArrayListExtra(RecipeActivity.STEP_EXTRA);
        }
        if (findViewById(R.id.step_activity_tab) != null) {
            //tablet view
            tabView = true;
            //load the recipe at position 0
            StepVideoFragment stepVideoFragment = new StepVideoFragment();
            stepVideoFragment.setPlayerStepVideoUrl(this.steps.get(0).getVideoURL());
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.step_video_container, stepVideoFragment)
                    .commit();

            StepDescFragment stepDescFragment = new StepDescFragment();
            stepDescFragment.setDescStep(this.steps.get(0));
            fragmentManager.beginTransaction()
                    .add(R.id.step_desc_container, stepDescFragment)
                    .commit();

        }

    }


    @Override
    public void onListItemClick(View v, int position) {
        if (steps != null && steps.size() > 0) {
            if (tabView) {
                //mark the view i.e button as clicked
                if (Util.SDK_INT > 22) {
                    v.setBackgroundColor(getResources().getColor(R.color.colorAccent, null));
                } else {
                    v.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }
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
                Step step = steps.get(position);
                Toast.makeText(getApplicationContext(), "Step - " + step.getShortDescription(), Toast.LENGTH_LONG).show();
                //launch the step detail activity
                Intent intent = new Intent(StepActivity.this, StepDetailsActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, steps);
                intent.putExtra(POSITION_EXTRA, position);
                startActivity(intent);
            }
        }
    }
}
