package tv.mta.flutter_playout.video;

import org.jetbrains.annotations.NotNull;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.platform.PlatformView;

import tv.mta.flutter_playout.video.ExoPlayerFragment;

public class PlayerView implements PlatformView, MethodChannel.MethodCallHandler {

    private final PlayerLayout player;

    private final MethodChannel channel;

    PlayerView(Context context, Activity activity, int id, BinaryMessenger messenger, Object args) {

        channel = new MethodChannel(messenger, "tv.mta/NativeVideoPlayerMethodChannel_" + id);

        channel.setMethodCallHandler(this);

        player = new ExoPlayerFragment(context, activity, messenger, id, args, channel).getPlayer();

        //oldVersion
        //new PlayerLayout(context, activity, messenger, id, args);
    }

    @Override
    public View getView() {
        return player;
    }

    public void setActivity(Activity activity) {
        //oldVersion
        //player.setActivity(activity);
    }

    @Override
    public void dispose() {
        player.onDestroy();
    }

    @Override
    public void onMethodCall(MethodCall call, @NotNull MethodChannel.Result result) {
        switch (call.method) {
            case "onMediaChanged":
                player.onMediaChanged(call.arguments);
                result.success(true);
                break;
            case "onShowControlsFlagChanged":
                player.onShowControlsFlagChanged(call.arguments);
                result.success(true);
                break;
            case "resume":
                player.play();
                result.success(true);
                break;
            case "pause":
                player.pause();
                result.success(true);
                break;
            case "setPreferredAudioLanguage":
                player.setPreferredAudioLanguage(call.arguments);
                result.success(true);
                break;
            case "setPreferredTextLanguage":
                player.setPreferredTextLanguage(call.arguments);
                result.success(true);
                break;
            case "seekTo":
                player.seekTo(call.arguments);
                result.success(true);
                break;
            case "dispose":
                dispose();
                result.success(true);
                break;
            case "onFullScreenChanged":
               player.onFullScreenChanged(call.arguments);
               result.success(true);
               break;
            default:
                result.notImplemented();
        }
    }
}