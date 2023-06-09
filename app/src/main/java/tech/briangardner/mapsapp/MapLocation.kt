package tech.briangardner.mapsapp

data class MapLocation(
  val token: String,
  val title: String,
  val description: String,
  val lat: Double,
  val lng: Double,
  val zoom: Float,
)