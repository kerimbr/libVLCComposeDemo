package dev.kerimbr.playerdemo

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.tv.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import dev.kerimbr.playerdemo.ui.theme.PlayerDemoTheme
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.util.VLCVideoLayout

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlayerDemoTheme {
                VLCPlayer(
                    modifier = Modifier.fillMaxSize(),
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    subtitleUrl = null
                )
            }
        }
    }
}

@Composable
fun VLCPlayer(
    modifier: Modifier,
    videoUrl: String,
    subtitleUrl: String?,
) {
    val localContext = LocalContext.current
    val libVLC = remember(key1 = videoUrl, key2 = subtitleUrl) { LibVLC(localContext) }
    val media = remember(key1 = videoUrl, key2 = subtitleUrl) { Media(libVLC, Uri.parse(videoUrl)) }
    val mediaPlayer = remember(key1 = videoUrl, key2 = subtitleUrl) {
        MediaPlayer(libVLC).apply {
            setMedia(media)
        }
    }

    if (!subtitleUrl.isNullOrBlank()) {
        LaunchedEffect(key1 = subtitleUrl) {
            media.addSlave(
                Media.Slave(
                    Media.Slave.Type.Subtitle,
                    4,
                    Uri.parse(subtitleUrl).toString(),
                )
            )
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            VLCVideoLayout(context).apply {
                mediaPlayer.attachViews(this, null, false, false)
                mediaPlayer.play()
            }
        },
        update = {
            mediaPlayer.play()
        },
    )
}