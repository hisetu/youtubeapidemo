package com.hisetu.youtubefragment;

import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerView;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public final class YouTubeFragment extends YouTubePlayerFragment
        implements YouTubePlayer.OnInitializedListener {

    private YouTubePlayer player;
    private String videoId;
    private View playerControllerBackground;
    private ViewGroup playerControllerContainer;

    public static void initialize(String developKey) {
        YouTubeFragment.developKey = developKey;
    }

    private static String developKey;

    public static YouTubeFragment newInstance() {
        return new YouTubeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialize(developKey, this);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        YouTubePlayerView youtubeView = (YouTubePlayerView) super.onCreateView(layoutInflater, viewGroup, bundle);

        ViewGroup layout = (ViewGroup) layoutInflater.inflate(R.layout.player_controller, viewGroup);
        layout.addView(youtubeView, 0, new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));

        setUpPlayerView(layout);

        return layout;
    }

    private void setUpPlayerView(View layout) {
        playerControllerBackground = layout.findViewById(R.id.player_controller_background);
        playerControllerBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.pause();
            }
        });
        playerControllerContainer = (ViewGroup) layout.findViewById(R.id.player_controller_container);
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
        player.setOnFullscreenListener((YouTubePlayer.OnFullscreenListener) getActivity());
        if (!restored && videoId != null) {
            player.cueVideo(videoId);
        }

        player.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
        player.setPlaybackEventListener(new YouTubePlayer.PlaybackEventListener() {
            @Override
            public void onPlaying() {
                playerControllerBackground.setVisibility(View.INVISIBLE);
                playerControllerContainer.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onPaused() {
                playerControllerBackground.setVisibility(View.VISIBLE);
                playerControllerContainer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStopped() {
                playerControllerBackground.setVisibility(View.VISIBLE);
                playerControllerContainer.setVisibility(View.VISIBLE);
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

    public void setControllerBackground(@ColorRes int colorRes) {
        playerControllerBackground.setBackgroundColor(getResources().getColor(colorRes));
    }

    public void addItemView(TextView itemView) {
        playerControllerContainer.addView(itemView);
    }

    public void addItem(@DrawableRes int icon, String text, @Nullable final OnItemClickListener clickListener) {
        TextView textView = new TextView(getActivity());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener == null)
                    return;
                clickListener.onClick(view, player);
            }
        });

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        params.topMargin = (int) getPxFromDp(10, displayMetrics);
        textView.setLayoutParams(params);

        textView.setCompoundDrawablesWithIntrinsicBounds(
                getResources().getDrawable(icon), null, null, null);
        textView.setCompoundDrawablePadding((int) getPxFromDp(5, displayMetrics));
        textView.setText(text);
        textView.setTextColor(getResources().getColor(android.R.color.white));
        textView.setTextSize((int) getPxFromDp(9, displayMetrics));

        playerControllerContainer.addView(textView);
    }

    private float getPxFromDp(int value, DisplayMetrics displayMetrics) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, displayMetrics);
    }

    public interface OnItemClickListener {
        void onClick(View view, YouTubePlayer player);
    }
}
