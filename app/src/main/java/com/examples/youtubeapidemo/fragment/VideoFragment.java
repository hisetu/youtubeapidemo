package com.examples.youtubeapidemo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.examples.youtubeapidemo.DeveloperKey;
import com.examples.youtubeapidemo.R;
import com.examples.youtubeapidemo.VideoListDemoActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerView;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public final class VideoFragment extends YouTubePlayerFragment
        implements YouTubePlayer.OnInitializedListener {

    private YouTubePlayer player;
    private String videoId;
    private ViewGroup playerLayout;
    private OnClickListener clickListener;

    public static VideoFragment newInstance() {
        return new VideoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialize(DeveloperKey.DEVELOPER_KEY, this);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        YouTubePlayerView youtubeView = (YouTubePlayerView) super.onCreateView(layoutInflater, viewGroup, bundle);

        ViewGroup layout = (ViewGroup) layoutInflater.inflate(R.layout.player_controller, viewGroup);
        layout.addView(youtubeView, 0, new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));

        playerLayout = (ViewGroup) layout.findViewById(R.id.play_layout);

        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpPlayerView();
    }

    private void setUpPlayerView() {
        View playButton = playerLayout.findViewById(R.id.play_button);
        View buyNowButton = playerLayout.findViewById(R.id.buy_now_button);

        View.OnClickListener l = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener == null)
                    return;

                int id = view.getId();
                if (id == R.id.play_button) {
                    clickListener.onPlayClick();
                } else if (id == R.id.buy_now_button) {
                    clickListener.onBuyNowClick();
                }
            }
        };
        playButton.setOnClickListener(l);
        buyNowButton.setOnClickListener(l);
    }

    @Override
    public void onDestroy() {
        if (player != null) {
            player.release();
        }
        super.onDestroy();
    }

    public void setVideoId(String videoId) {
        if (videoId != null && !videoId.equals(this.videoId)) {
            this.videoId = videoId;
            if (player != null) {
                player.cueVideo(videoId);
            }
        }
    }

    public void pause() {
        if (player != null) {
            player.pause();
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean restored) {
        this.player = player;
        player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
        player.setOnFullscreenListener((VideoListDemoActivity) getActivity());
        if (!restored && videoId != null) {
            player.cueVideo(videoId);
        }

        player.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);

        player.setPlaybackEventListener(new YouTubePlayer.PlaybackEventListener() {
            @Override
            public void onPlaying() {
                playerLayout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onPaused() {
                playerLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStopped() {
                playerLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onBuffering(boolean b) {

            }

            @Override
            public void onSeekTo(int i) {

            }
        });
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
        this.player = null;
    }

    public void setOnClickListener(OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface OnClickListener {
        void onPlayClick();

        void onBuyNowClick();
    }
}
