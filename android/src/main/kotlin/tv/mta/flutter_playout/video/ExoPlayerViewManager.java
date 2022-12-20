package tv.mta.flutter_playout.video;


import com.google.android.exoplayer2.SimpleExoPlayer;
import java.util.Map;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.ImageView;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import android.net.Uri;
import com.google.android.exoplayer2.util.Util;
import java.util.HashMap;
import io.flutter.plugin.common.MethodChannel;

import android.content.Context;
public class ExoPlayerViewManager {

  private static final String TAG = "ExoPlayerViewManager";

  public static final String EXTRA_VIDEO_URI = "video_uri";

  private static Map<String, ExoPlayerViewManager> instances = new HashMap<>();
  private Uri videoUri;
  private String video_uri;

  public static ExoPlayerViewManager getInstance(String videoUri) {
    ExoPlayerViewManager instance = instances.get(videoUri);
    if (instance == null) {
      instance = new ExoPlayerViewManager(videoUri);
      instances.put(videoUri, instance);
    }
    return instance;
  }

  private SimpleExoPlayer player;
  private boolean isPlayerPlaying;
  private DefaultTrackSelector trackSelector;
  private boolean playerState;

  private ExoPlayerViewManager(String videoUri) {
    this.videoUri = Uri.parse(videoUri);
    this.video_uri = videoUri;
  }

  public void prepareExoPlayer(
  Context context,
  StyledPlayerView exoPlayerView,
  long position,
  String preferredAudioLanguage,
  String preferredTextLanguage,
  boolean playerState) {
    if (context == null || exoPlayerView == null) {
      return;
    }
    if (player == null) {
      // Create a new player if the player is null or
      // we want to play a new video
      // Do all the standard ExoPlayer code here...
      // Prepare the player with the source.
       trackSelector = new DefaultTrackSelector(context);

       trackSelector.setParameters(
               trackSelector.buildUponParameters()
                       .setPreferredAudioLanguage(preferredAudioLanguage)
                       .setPreferredTextLanguage(preferredTextLanguage));

       player = new SimpleExoPlayer.Builder(context)
       .setUseLazyPreparation(true)
       .setTrackSelector(trackSelector)
       .build();

       DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
       Util.getUserAgent(context, "flutter_playout"));

              /* This is the MediaSource representing the media to be played. */
              MediaSource videoSource;
              if (video_uri.contains(".m3u8") || video_uri.contains(".m3u")) {
                  Log.d("tag", video_uri);
                  videoSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(this.videoUri));
              } else {
                  Log.d("tag", video_uri);
                  videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(this.videoUri));
              }

              player.prepare(videoSource);
    }
    player.clearVideoSurface();

    player.setVideoSurfaceView((SurfaceView) exoPlayerView.getVideoSurfaceView());

    player.seekTo(position + 1);

    isPlayerPlaying = playerState;

    goToForeground();

    exoPlayerView.setPlayer(player);
  }

  public void releaseVideoPlayer() {
    if (player != null) {
      player.release();
    }
    player = null;
  }

  public void goToBackground() {
    if (player != null) {
      isPlayerPlaying = player.getPlayWhenReady();
      player.setPlayWhenReady(false);
    }
  }

  public void goToForeground() {
    if (player != null) {
      player.setPlayWhenReady(isPlayerPlaying);
    }
  }

  public long getPosition() {
   if (player != null) {
        return player.getCurrentPosition();
      } else return -1;
  }

   public boolean getPlayerState() {
     if (player != null) {
          return player.isPlaying();
        } else return false;
    }
}
