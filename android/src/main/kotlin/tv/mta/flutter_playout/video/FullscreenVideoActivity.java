package tv.mta.flutter_playout.video;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.exoplayer2.ExoPlayer;
import android.app.Activity;
import android.util.Log;
import android.widget.ImageView;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import tv.mta.flutter_playout.R;
import android.os.Handler;


public class FullscreenVideoActivity extends AppCompatActivity {
  /**
   * Some older devices needs a small delay between UI widget updates
   * and a change of the status and navigation bar.
   */

  private static final int UI_ANIMATION_DELAY = 300;
  private final Handler mHideHandler = new Handler();
  private View mContentView;
  private Activity activity;
  private long position = -1;
  private String preferredAudioLanguage = "mul";
  private String preferredTextLanguage = "";
  private Boolean isPlayerPlaying = false;

  private final Runnable mHidePart2Runnable = new Runnable() {

    @Override
    public void run() {
      // Delayed removal of status and navigation bar

      // Note that some of these constants are new as of
      // API 19 (KitKat). It is safe to use them, as they are inlined
      // at compile-time and do nothing on earlier devices.
      mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
  };

  private final Runnable mHideRunnable = new Runnable() {
    @Override
    public void run() {
      hide();
    }
  };

  private String mVideoUri;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_fullscreen_video);

    mContentView = findViewById(R.id.enclosing_layout);

    StyledPlayerView playerView = findViewById(R.id.player_view);

    mVideoUri = getIntent().getStringExtra("video_uri");
    this.position = getIntent().getLongExtra("position", -1);
    this.preferredAudioLanguage = getIntent().getStringExtra("preferredAudioLanguage");
    this.preferredTextLanguage = getIntent().getStringExtra("preferredTextLanguage");
    this.isPlayerPlaying = getIntent().getBooleanExtra("playerState", false);

    ExoPlayerViewManager.getInstance(mVideoUri)
            .prepareExoPlayer(this, playerView,
             position,
             preferredAudioLanguage,
             preferredTextLanguage,
             isPlayerPlaying);

    // Set the fullscreen button to "close fullscreen" icon
    View controlView = playerView.findViewById(R.id.exo_controller);

    ImageView fullscreenIcon = controlView.findViewById(R.id.exo_fullscreen_button);

    fullscreenIcon.setImageResource(R.drawable.ic_fullscreen_exit);

    fullscreenIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             Log.d("print", "result");
             Intent data = new Intent();
             data.putExtra("position", ExoPlayerViewManager.getInstance(mVideoUri).getPosition());
             data.putExtra("playerState", isPlayerPlaying);
             setResult(RESULT_OK, data);
             finish();
            }
          });
  }
  @Override
  public void onResume() {
    super.onResume();
    ExoPlayerViewManager.getInstance(mVideoUri).goToForeground();
  }

  @Override
  public void onPause() {
    super.onPause();
    ExoPlayerViewManager.getInstance(mVideoUri).goToBackground();
  }

  @Override
  public void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);

    // Trigger the initial hide() shortly after the activity has been
    // created, to briefly hint to the user that UI controls
    // are available.
    delayedHide();
  }

  private void hide() {
    // Schedule a runnable to remove the status and navigation bar after a delay
    mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
  }

  /**
   * Schedules a call to hide() in delay milliseconds, canceling any
   * previously scheduled calls.
   */
  private void delayedHide() {
    mHideHandler.removeCallbacks(mHideRunnable);
    mHideHandler.postDelayed(mHideRunnable, 100);
  }
}