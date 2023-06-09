package tech.briangardner.mapsapp.ui

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.clustering.Clustering
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import tech.briangardner.mapsapp.MapLocation
import tech.briangardner.mapsapp.R
import tech.briangardner.mapsapp.ui.MapEvent.OnCameraPositionChanged
import tech.briangardner.mapsapp.ui.MapEvent.OnClusterClicked
import tech.briangardner.mapsapp.ui.MapEvent.OnClusterItemClicked

private const val ZOOM_MAX = 20f
private const val ZOOM_MIN = 3f

@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun MainMap(
  modifier: Modifier = Modifier,
  hasLocationPermission: Boolean,
  location: MapLocation,
  markers: List<MapLocation>,
  onEvent: (MapEvent) -> Unit
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
  LaunchedEffect(cameraState.isMoving) {
    if (!cameraState.isMoving) {
      // notify presenter of new location
      onEvent(
        OnCameraPositionChanged(
          lat = cameraState.position.target.latitude,
          lng = cameraState.position.target.longitude,
          zoom = cameraState.position.zoom
        )
      )
    }
  }

  LaunchedEffect(location) {
    cameraState.move(
      CameraUpdateFactory.newCameraPosition(
        CameraPosition.fromLatLngZoom(
          LatLng(location.lat, location.lng),
          location.zoom
        )
      )
    )
  }

  val clusterItems = remember(markers) {
    markers.map { MapClusterItem(it) }
  }
  GoogleMap(
    modifier = modifier,
    cameraPositionState = cameraState,
    properties = mapProperties,
    uiSettings = mapUiSettings,
  ){
    BasicCluster(markers) {}
  }
}

@Composable
fun BasicMarker(
  location: MapLocation
) {
  Marker(
    state = rememberMarkerState(
      key = location.token,
      position = LatLng(location.lat, location.lng)
    ),
    title = "Cash Money ATM"
  )
}

@Composable
fun MarkerWithCustomInfo(
  location: MapLocation
) {
  MarkerInfoWindow(
    state = rememberMarkerState(
      key = location.token,
      position = LatLng(location.lat, location.lng)
    )
  ) {
    Surface(
      modifier = Modifier
        .background(MaterialTheme.colorScheme.background)
        .padding(8.dp)
    ) {
      Column {
        Text(text = "Cash money ATM")
        Text(text = "Withdraw or deposit here!")
      }
    }
  }
}

@Composable
fun CustomizedMarker(
  location: MapLocation
) {
  val resources = LocalContext.current.resources
  val iconBitmapDescriptor = remember {
    val iconBitmap = ResourcesCompat.getDrawable(
      resources,
      R.drawable.icon_marker,
      null
    )?.toBitmap()!!
    BitmapDescriptorFactory.fromBitmap(iconBitmap)
  }
  Marker(
    state = rememberMarkerState(
      key = location.token,
      position = LatLng(location.lat, location.lng)
    ),
    icon = iconBitmapDescriptor,
  )
}

@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun BasicCluster(
  locations: List<MapLocation>,
  onEvent: (MapEvent) -> Unit
) {
  val clusterItems = locations.map { MapClusterItem(it) }
  Clustering(
    items = clusterItems,
    onClusterClick = { cluster ->
      onEvent(
        OnClusterClicked(
          cluster.position.latitude,
          cluster.position.longitude,
          cluster.items.map { it.location }
        )
      )
      true
    },
    onClusterItemClick = { mapClusterItem ->
      onEvent(
        OnClusterItemClicked(
          mapClusterItem.location.token
        )
      )
      true
    },
  )
}

@Composable
fun CustomClusterContent(cluster: Cluster<MapClusterItem>) {
  Surface {
    Text(
      text = cluster.size.toString(),
      color = MaterialTheme.colorScheme.onPrimary,
      modifier = Modifier
        .background(MaterialTheme.colorScheme.primary)
        .padding(4.dp)
    )
  }
}

@Composable
fun ClusterItemContent(item: MapClusterItem) {
  Image(
    painter = painterResource(R.drawable.icon_marker),
    contentDescription = ""
  )
}

class MapClusterItem(
  val location: MapLocation
) : ClusterItem {
  override fun getPosition() =
    LatLng(location.lat, location.lng)

  override fun getTitle() = location.title
  override fun getSnippet() = location.description
}

class MapClusterRenderer(
  context: Context,
  googleMap: GoogleMap,
  clusterManager: ClusterManager<MapClusterItem>,
): DefaultClusterRenderer<MapClusterItem>(context, googleMap, clusterManager) {

}

sealed class MapEvent {
  class OnCameraPositionChanged(val lat: Double, val lng: Double, val zoom: Float) : MapEvent()
  class OnClusterClicked(val lat: Double, val lng: Double, val items: List<MapLocation>) : MapEvent()
  class OnClusterItemClicked(val token: String) : MapEvent()
}