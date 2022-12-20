package tv.mta.flutter_playout.video;

import android.app.Fragment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.os.Bundle;
import android.util.Log;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.StyledPlayerView;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.JSONMessageCodec;
import io.flutter.plugin.common.MethodChannel;

import tv.mta.flutter_playout.R;

public class ExoPlayerFragment extends Activity {

    //@BindView(R.id.player_view) StyledPlayerView mPlayerView;
    private StyledPlayerView mPlayerView;
    private SimpleExoPlayer mPlayer;
    private Activity activity;
    private Context context;
    private BinaryMessenger messenger;
    private Object args;
    private int id;
    private MethodChannel channel;

    public ExoPlayerFragment(
    Context context,
    Activity activity,
    BinaryMessenger messenger,
    int id,
    Object args,
    MethodChannel channel
    ) {
        this.context = context;
        this.activity = activity;
        this.id = id;
        this.messenger = messenger;
        this.args = args;
        this.channel = channel;
    }


     protected void onCreate(Bundle savedInstanceState) {

     super.onCreate(savedInstanceState); //Bundle savedInstanceState) {

        setContentView(R.id.player_view);

        //View rootView = inflater.inflate(R.layout.activity_fullscreen_video, container, false);
        //return rootView;

        Intent intent = new Intent(this, FullscreenVideoActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //intent.putExtra("preferredAudioLanguage", preferredAudioLanguage);
        //intent.putExtra("preferredTextLanguage", preferredTextLanguage);

        startActivityForResult(intent, 201);

    }

  public PlayerLayout getPlayer() {
        //oldVersion
        //mPlayerView = getView().findViewById(R.id.player_view);

        return new PlayerLayout(context, this , messenger, id, args, channel);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Log.d("onActivityResult", "FRAGMENT");
    }
}