package com.android.example.bakingapp.fragment;

import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.example.bakingapp.R;
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
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

/**
 * Created by root on 6/14/17.
 */

public class StepDetailsFragment extends Fragment {


    private static final String PLAYBACK_POSITION = "playback_position";
    private static final String CURRENT_WINDOW_INDEX = "current_window_index";
    private static final String AUTOPLAY = "autoplay";
    private static final String STEPS = "steps";
    private static final String POSITION = "position";
    private static final String PREV_POSITION = "prev_position";
    private static final String NEXT_POSITION = "next_position";
    private SimpleExoPlayerView mPlayerView;
    private SimpleExoPlayer mExoPlayer;
    private TextView shortDesc;
    private TextView description;
    private Button prev;
    private Button next;
    private int position;
    private ArrayList<Step> steps;
    boolean prevEnabled;
    boolean nextEnabled;
    int prevPositionTemp;
    int nextPositionTemp;
    private Step nextStep;
    private Step prevStep;
    private long playBackPosition;
    private int currentWindowIndex;
    private boolean autoPlay = true;
    private Uri mediaUri;

    public StepDetailsFragment() {


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_step_details, container, false);
        mPlayerView = (SimpleExoPlayerView) view.findViewById(R.id.playerView);
        shortDesc = (TextView) view.findViewById(R.id.step_short_desc);
        description = (TextView) view.findViewById(R.id.step_long_desc);
        prev = (Button) view.findViewById(R.id.step_prev);
        next = (Button) view.findViewById(R.id.step_next);
        //on landscape mode, remove the description and buttons
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){

            ScrollView scrollView = (ScrollView) view.findViewById(R.id.description_on_step_details);
            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.navigation_on_step_details);
            scrollView.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
        }


        if(savedInstanceState != null){
//            playBackPosition = savedInstanceState.getLong(PLAYBACK_POSITION,0);
//            currentWindowIndex = savedInstanceState.getInt(CURRENT_WINDOW_INDEX,0);
//            autoPlay = savedInstanceState.getBoolean(AUTOPLAY);
            steps = savedInstanceState.getParcelableArrayList(STEPS);
            position = savedInstanceState.getInt(POSITION);
            prevPositionTemp = savedInstanceState.getInt(PREV_POSITION);
            nextPositionTemp = savedInstanceState.getInt(NEXT_POSITION);
        }

        //its safer to handle the prev and next button clicks here
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dec the position
                setUpMPlayer(prevStep);
                setUpDetails(prevStep);
                --nextPositionTemp;
                --prevPositionTemp;
                setUpButtons(prevPositionTemp, nextPositionTemp);

            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpMPlayer(nextStep);
                setUpDetails(nextStep);
                ++prevPositionTemp;
                ++nextPositionTemp;
                setUpButtons(prevPositionTemp, nextPositionTemp);


                //next.setEnabled(nextEnabled);
            }
        });
        return view;
    }

    private void prepare() {

        setUpButtons(prevPositionTemp, nextPositionTemp);
        Step step = steps.get(position);
        if (step != null) {
            setUpMPlayer(step);
            setUpDetails(step);
        }
    }

    private void setUpDetails(Step step) {
        //also set the short desc and desc

        shortDesc.setText(step.getShortDescription());
        description.setText(step.getDescription());
    }

    private void setUpMPlayer(Step step) {
        releasePlayer();
        if (step.getVideoURL().equals("")) {

            mPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(getResources(), R.drawable.action_error));
        } else {

            mediaUri = Uri.parse(step.getVideoURL());
            initializePlayer(mediaUri);
        }


    }

    public void setRecipeStep(ArrayList<Step> steps, int position) {
        this.position = position;
        this.steps = steps;
        prevPositionTemp = position - 1;
        nextPositionTemp = position + 1;
        //setUpButtons(prevPositionTemp, nextPositionTemp);
    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        //keep player state before releasing
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(Util.SDK_INT <=23){
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(Util.SDK_INT > 23){
            releasePlayer();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(Util.SDK_INT > 23){
            prepare();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if(Util.SDK_INT < 23 || mExoPlayer == null){
            prepare();
        }
    }

    public void setUpButtons(int prev, int next) {
        try {
            prevStep = this.steps.get(prev);
            prevEnabled = true;
            this.prev.setEnabled(true);
        } catch (IndexOutOfBoundsException e) {
            prevEnabled = false;
            this.prev.setEnabled(false);
        }

        try {
            nextStep = this.steps.get(next);
            nextEnabled = true;
            this.next.setEnabled(true);
        } catch (IndexOutOfBoundsException e) {
            nextEnabled = false;
            this.next.setEnabled(false);
        }
    }


    /**
     * Initialize ExoPlayer.
     *
     * @param mediaUri The URI of the sample to play.
     */
    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            //mExoPlayer.addListener(this);
            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getContext(), "BakingApp");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultHttpDataSourceFactory(
                    userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(autoPlay);
            mExoPlayer.seekTo(currentWindowIndex,playBackPosition);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mExoPlayer == null) {
//            outState.putLong(PLAYBACK_POSITION, playBackPosition);
//            outState.putInt(CURRENT_WINDOW_INDEX, currentWindowIndex);
//            outState.putBoolean(AUTOPLAY, autoPlay);
            outState.putParcelableArrayList(STEPS,this.steps);
            outState.putInt(POSITION,this.position);
            outState.putInt(PREV_POSITION,this.prevPositionTemp);
            outState.putInt(NEXT_POSITION,this.nextPositionTemp);
        }
    }
}

