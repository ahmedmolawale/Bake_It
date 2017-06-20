package com.android.example.bakingapp.fragment;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.example.bakingapp.R;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by root on 6/15/17.
 */

public class StepVideoFragment extends Fragment {

    private static final String PLAYBACK_POSITION = "playback_position";
    private static final String MEDIA_URI = "media_url";
    private static final String CURRENT_WINDOW_INDEX = "current_window_index";
    private static final String AUTO_PLAY = "auto_play";
    @BindView(R.id.playerView)
    SimpleExoPlayerView mPlayerView;
    private SimpleExoPlayer mExoPlayer;
    private String mediaUri;
    private long playbackPosition;
    private int currentWindowIndex;
    private boolean autoPlay;
    private Unbinder unbinder;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_recipe_step_video, container, false);
        unbinder = ButterKnife.bind(StepVideoFragment.this,view);
        if(savedInstanceState != null){
            this.mediaUri = savedInstanceState.getString(MEDIA_URI);
            playbackPosition = savedInstanceState.getLong(PLAYBACK_POSITION,0);
            currentWindowIndex = savedInstanceState.getInt(CURRENT_WINDOW_INDEX,0);
            autoPlay = savedInstanceState.getBoolean(AUTO_PLAY,false);
        }

        return view;
    }

    public void setPlayerStepVideoUrl(String uri) {
        this.mediaUri = uri;
        //initialise play position back to zer0, this is is new video play request
        playbackPosition =0;
        currentWindowIndex =0;
        autoPlay = true;
    }

    public void setUpMPlayer(String mediaUri) {
        //release the player before setting up a new one
        releasePlayer();

        if (TextUtils.isEmpty(mediaUri)) {
            mPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(getResources(), R.drawable.action_error));
        } else {
            initializePlayer(Uri.parse(mediaUri));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23 || mExoPlayer == null) {
            setUpMPlayer(this.mediaUri);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //API level 24 and above support multi window...so its safe to set up here
        if (Util.SDK_INT > 23) {
            setUpMPlayer(this.mediaUri);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        //for API lower/equal to 23, release resource early
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
    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        //keep player state before releasing
        if (mExoPlayer != null) {
            //keep track of player before releasing
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
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
            if(currentWindowIndex !=0 && playbackPosition != 0)
            mExoPlayer.seekTo(currentWindowIndex,playbackPosition);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(MEDIA_URI,this.mediaUri);
        if(mExoPlayer != null){
            outState.putLong(PLAYBACK_POSITION,mExoPlayer.getCurrentPosition());
            outState.putInt(CURRENT_WINDOW_INDEX,mExoPlayer.getCurrentWindowIndex());
            outState.putBoolean(AUTO_PLAY,mExoPlayer.getPlayWhenReady());
        }
    }
    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
