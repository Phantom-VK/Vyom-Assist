import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LifecycleOwner

@Composable
fun VolumeButtonControls(
    lifecycleOwner: LifecycleOwner,
    onVolumePressed: (Boolean) -> Unit
) {
    val context = LocalContext.current

    DisposableEffect(lifecycleOwner) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        var lastVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

        val volumeReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == "android.media.VOLUME_CHANGED_ACTION") {
                    val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

                    if (currentVolume > lastVolume) {
                        onVolumePressed(true)  // Volume Up pressed
                    } else if (currentVolume < lastVolume) {
                        onVolumePressed(false) // Volume Down pressed
                    }
                    lastVolume = currentVolume
                }
            }
        }

        val intentFilter = IntentFilter("android.media.VOLUME_CHANGED_ACTION")
        context.registerReceiver(volumeReceiver, intentFilter)

        onDispose {
            context.unregisterReceiver(volumeReceiver)
        }
    }
}

