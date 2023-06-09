package tech.briangardner.mapsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.MapsInitializer
import tech.briangardner.mapsapp.ui.MainMap
import tech.briangardner.mapsapp.ui.theme.MapsAppTheme
import kotlin.random.Random

private const val LAT_ROOT = 32.0
private const val LNG_ROOT = -81.15

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    MapsInitializer.initialize(
      this,
      MapsInitializer.Renderer.LATEST
    ) {}
    setContent {
      val savannahLocation = remember {
        MapLocation(
          token = "forsyth-park",
          title = "Forsyth Park",
          description = "A big park with a nice fountain",
          lat = 32.0675,
          lng = -81.0962,
          zoom = 12f,
        )
      }
      MapsAppTheme {
        MainMap(
          modifier = Modifier.fillMaxSize(),
          hasLocationPermission = false,
          location = savannahLocation,
          markers = generateMarkers(),
          onEvent = {}
        )
      }
    }
  }

  private fun generateMarkers() = List(200) { index ->
    val latVariance = Random.nextInt(100) * 0.001
    val lngVariance = Random.nextInt(100) * 0.001
    MapLocation(
      token = "location-$index",
      title = "Location #$index",
      description = "Description for location #$index",
      lat = LAT_ROOT + latVariance,
      lng = LNG_ROOT + lngVariance,
      zoom = 12f,
    )
  }
}
