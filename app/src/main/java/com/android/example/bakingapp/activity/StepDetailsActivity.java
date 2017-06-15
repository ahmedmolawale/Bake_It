package com.android.example.bakingapp.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ScrollingView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.android.example.bakingapp.R;
import com.android.example.bakingapp.fragment.StepDetailsFragment;
import com.android.example.bakingapp.model.Step;

import java.util.ArrayList;

public class StepDetailsActivity extends AppCompatActivity {

    ArrayList<Step> steps;
    int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step_details_layout);

        Intent intent= getIntent();
        if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT) && intent.hasExtra(StepActivity.POSITION_EXTRA)){
            steps = intent.getParcelableArrayListExtra(Intent.EXTRA_TEXT);
            position = intent.getIntExtra(StepActivity.POSITION_EXTRA,-1);

        }
        StepDetailsFragment stepDetailsFragment = new StepDetailsFragment();
        stepDetailsFragment.setRecipeStep(steps,position);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.step_detail_container,stepDetailsFragment)
                .commit();
    }
}
