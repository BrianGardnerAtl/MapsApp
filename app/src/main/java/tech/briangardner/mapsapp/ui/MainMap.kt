package tech.briangardner.mapsapp.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import tech.briangardner.mapsapp.MapLocation
import tech.briangardner.mapsapp.R

private const val ZOOM_MAX = 20f
private const val ZOOM_MIN = 3f

@Composable
fun MainMap(
  modifier: Modifier = Modifier,
  hasLocationPermission: Boolean,
  location: MapLocation,
) {
  val context = LocalContext.current
  val mapStyleOptions = if (isSystemInDarkTheme()) {
    MapStyleOptions.loadRawResourceStyle(
      context,
      R.raw.night_mode_style
    )
  } else {
    null
  }
  val mapProperties by remember(hasLocationPermission) {
    mutableStateOf(
      MapProperties(
        mapStyleOptions = mapStyleOptions,
        maxZoomPreference = ZOOM_MAX,
        minZoomPreference = ZOOM_MIN,
        isMyLocationEnabled = hasLocationPermission,
      )
    )
  }
  val mapUiSettings by remember {
    mutableStateOf(
      MapUiSettings(
        indoorLevelPickerEnabled = false,
        mapToolbarEnabled = false,
        myLocationButtonEnabled = false,
        rotationGesturesEnabled = false,
        tiltGesturesEnabled = false,
        zoomControlsEnabled = false,
      )
    )
  }
  val cameraState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(
      LatLng(location.lat, location.lng),
      location.zoom
    )
  }

  GoogleMap(
    modifier = modifier,
    cameraPositionState = cameraState,
    properties = mapProperties,
    uiSettings = mapUiSettings,
  ){
  }
}