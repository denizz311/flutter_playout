package tv.mta.flutter_playout

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import tv.mta.flutter_playout.audio.AudioPlayer
import tv.mta.flutter_playout.video.PlayerViewFactory
import io.flutter.plugin.common.PluginRegistry

class FlutterPlayoutPlugin: FlutterPlugin, ActivityAware {
  //, PluginRegistry.ActivityResultListener  {

  private lateinit var activity : Activity

  private lateinit var playerViewFactory : PlayerViewFactory

  private lateinit var audioPlayerFactory : AudioPlayer

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    try {
      playerViewFactory = PlayerViewFactory.registerWith(
        flutterPluginBinding.platformViewRegistry,
        flutterPluginBinding.binaryMessenger)
    } catch (e: Exception) {
      throw e
    }

    try {
      audioPlayerFactory = AudioPlayer.registerWith(flutterPluginBinding.binaryMessenger,
              activity, flutterPluginBinding.applicationContext)
    } catch (e: Exception) {
      throw e
    }
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    playerViewFactory.onDetachActivity()
    audioPlayerFactory.onDetachActivity()
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    activity = binding.activity
    playerViewFactory.onAttachActivity(binding.activity)
    playerViewFactory.addActivity(binding.activity)
    audioPlayerFactory.onAttachActivity(binding.activity)
    //binding.addActivityResultListener(this)
  }

  override fun onDetachedFromActivityForConfigChanges() {
    playerViewFactory.onDetachActivity()
    audioPlayerFactory.onDetachActivity()
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    activity = binding.activity
    playerViewFactory.onAttachActivity(binding.activity)
    audioPlayerFactory.onAttachActivity(binding.activity)
    //binding.addActivityResultListener(this)
  }

  override fun onDetachedFromActivity() {
    playerViewFactory.onDetachActivity()
    audioPlayerFactory.onDetachActivity()
  }

  //override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
    //super.onActivityResult(requestCode, resultCode, data)
    //Log.d("onActivityResult", "Result")
    //return false
  //}
}
