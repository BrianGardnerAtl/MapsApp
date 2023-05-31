package tech.briangardner.mapsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import tech.briangardner.mapsapp.ui.MainMap
import tech.briangardner.mapsapp.ui.theme.MapsAppTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      val savannahLocation = remember {
        MapLocation(
          lat = 32.0675,
          lng = -81.0962,
          zoom = 12f,
        )
      }
      MapsAppTheme {
        MainMap(
          modifier = Modifier.fillMaxSize(),
          hasLocationPermission = false,
          location = savannahLocation
        )
      }
    }
  }
}
