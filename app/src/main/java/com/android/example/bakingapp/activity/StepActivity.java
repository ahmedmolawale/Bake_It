package com.android.example.bakingapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.example.bakingapp.R;
import com.android.example.bakingapp.adapter.StepsFragmentAdapter;
import com.android.example.bakingapp.fragment.StepDetailsFragment;
import com.android.example.bakingapp.model.Step;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
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
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(RecipeActivity.STEP_EXTRA)) {
            steps = intent.getParcelableArrayListExtra(RecipeActivity.STEP_EXTRA);
        }
        if (findViewById(R.id.step_activity_tab) != null) {
            //tablet view
            tabView = true;
            //remove navigation buttons
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.navigation_on_step_details);
            linearLayout.setVisibility(View.GONE);
        }

    }



    @Override
    public void onListItemClick(int position) {
        if (steps != null && steps.size() > 0) {
            if (tabView) {
                //grab the simple player view and description
                StepDetailsFragment stepDetailsFragment = new StepDetailsFragment();
                stepDetailsFragment.setRecipeStep(steps,position);

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.step_detail_container,stepDetailsFragment)
                        .commit();

            } else {
                Step step = steps.get(position);
                Toast.makeText(getApplicationContext(), "Step - " + step.getShortDescription(), Toast.LENGTH_LONG).show();
                //launch the step detail activity
                Intent intent = new Intent(StepActivity.this, StepDetailsActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, steps);
                intent.putExtra(POSITION_EXTRA,position);
                startActivity(intent);
            }
        }
    }
}
