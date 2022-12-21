package tv.mta.flutter_playout.video

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import io.flutter.plugin.common.MethodChannel
import tv.mta.flutter_playout.R


private const val EXTRA_VIDEO_URL = "EXTRA_VIDEO_URL"
private const val EXTRA_PLAYBACK_POSITION_MS = "EXTRA_PLAYBACK_POSITION_MS"
private const val STATE_PLAYBACK_POSITION_MS = "STATE_PLAYBACK_POSITION_MS"
private const val EXTRA_PLAYBACK_STATE = "EXTRA_PLAYBACK_STATE"
private const val EXTRA_AUDIO_LANG = "EXTRA_AUDIO_LANG"
private const val EXTRA_TEXT_LANG = "EXTRA_TEXT_LANG"

class FullScreenVideoActivity : AppCompatActivity() {
    lateinit var prop: MethodChannel

    companion object {

        @JvmStatic fun newIntent(packageContext: Context,
                                 videoUrl: String,
                                 playbackPositionMs: Long,
                                 playbackState: Boolean,
                                 preferredAudioLanguage: String,
                                 preferredTextLanguage: String,
                                 channel: MethodChannel
        ): Intent {
            val intent = Intent(packageContext, FullScreenVideoActivity::class.java)
            intent.putExtra(EXTRA_VIDEO_URL, videoUrl)
            intent.putExtra(EXTRA_PLAYBACK_POSITION_MS, playbackPositionMs)
            intent.putExtra(EXTRA_PLAYBACK_STATE, playbackState)
            intent.putExtra(EXTRA_AUDIO_LANG, preferredAudioLanguage)
            intent.putExtra(EXTRA_TEXT_LANG, preferredTextLanguage)
            return intent
        }

        @JvmStatic lateinit var companionProp: MethodChannel

    }

    private lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prop = companionProp

        Log.d("channelProp", prop.toString())

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_fullscreen_video)

        val videoUrl = intent.getStringExtra(EXTRA_VIDEO_URL)
        var playbackPositionMs = intent.getLongExtra(EXTRA_PLAYBACK_POSITION_MS, 0)
        val preferredAudioLanguage = intent.getStringExtra(EXTRA_AUDIO_LANG)
        val preferredTextLanguage = intent.getStringExtra(EXTRA_TEXT_LANG)
        Log.d(EXTRA_PLAYBACK_STATE, intent.getBooleanExtra(EXTRA_PLAYBACK_STATE, true).toString())
        val isPlayerPlaying = intent.getBooleanExtra(EXTRA_PLAYBACK_STATE, true)

        if (savedInstanceState != null) {
            // The user rotated the screen
            playbackPositionMs = savedInstanceState.getLong(STATE_PLAYBACK_POSITION_MS)
        }

        val fullscreenIcon: View = findViewById<View>(R.id.exo_fullscreen_button)

        (fullscreenIcon as ImageButton).setImageResource(R.drawable.ic_fullscreen_exit)

        findViewById<View>(R.id.exo_fullscreen_button).setOnClickListener {
            val args = HashMap<String, Any>()
            args["position"] = player.currentPosition
            args["isPlayerPlaying"] = player.isPlaying

            prop.invokeMethod("onFullScreenChanged", args);

            finish()
        }

        val playerView: StyledPlayerView = findViewById(R.id.player_view)

        val trackSelector = DefaultTrackSelector(this);

        trackSelector.setParameters(
            trackSelector.buildUponParameters()
                .setPreferredAudioLanguage(preferredAudioLanguage)
                .setPreferredTextLanguage(preferredTextLanguage));

        //player = ExoPlayerFactory.newSimpleInstance(this)

        player = ExoPlayer.Builder(this)
            .setUseLazyPreparation(true)
            .setTrackSelector(trackSelector)
            .build()

        val userAgent = Util.getUserAgent(this, "flutter_playout")

        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
            this,
            userAgent,
        )

        //val dataSourceFactory = DefaultDataSourceFactory(this, userAgent)

        if (videoUrl != null) {
            val videoSource: MediaSource = if (videoUrl.contains(".m3u8") || videoUrl.contains(".m3u")) {
                Log.d("tag", videoUrl)
                HlsMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(Uri.parse(videoUrl)))
            } else {
                Log.d("tag", videoUrl)
                ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(
                    MediaItem.fromUri(
                        Uri.parse(videoUrl)
                    )
                )
            }
            player.prepare(videoSource)
        }

        //val mediaSource: MediaSource =
        //    ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(videoUrl)))
        //player.prepare(mediaSource)
        player.seekTo(playbackPositionMs)

        player.playWhenReady = isPlayerPlaying
        playerView.player = player
    }

    override fun onPause() {
        super.onPause()
        player.playWhenReady = false
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(STATE_PLAYBACK_POSITION_MS, player.currentPosition)
    }

    override fun onBackPressed() {
        val args = HashMap<String, Any>()
        args["position"] = player.currentPosition
        args["isPlayerPlaying"] = player.isPlaying

        prop.invokeMethod("onFullScreenChanged", args);

        finish()
    }

}