package tv.mta.flutter_playout_example

import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import tv.mta.flutter_playout.audio.AudioPlayer
import tv.mta.flutter_playout.video.PlayerViewFactory
import android.content.Intent
import android.util.Log

class MainActivity: FlutterActivity() {
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        PlayerViewFactory.registerWith(
                flutterEngine.platformViewsController.registry,
                flutterEngine.dartExecutor.binaryMessenger)

        AudioPlayer.registerWith(
                flutterEngine.dartExecutor.binaryMessenger,
                this, context)
    }

    override protected fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Log.d("onActivityResult", "OK, I got the result in MainActivity Example")

            }
            if (resultCode == RESULT_CANCELED) {
                //if there's no result
                Log.d("onActivityResult", "CANCELED I got the result in MainActivity Example")
            }
        }
    } //onActivityResult

}