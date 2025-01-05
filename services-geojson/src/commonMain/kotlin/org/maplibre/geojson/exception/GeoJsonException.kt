package org.maplibre.geojson.exception

/**
 * A form of `Throwable` that indicates an issue occurred during a GeoJSON operation.
 *
 * @param message the detail message (which is saved for later retrieval by the [.getMessage] method)
 * @since 3.0.0
 */
class GeoJsonException (message: String?) : RuntimeException(message)

