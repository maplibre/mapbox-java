package org.maplibre.geojson

import kotlinx.serialization.Serializable
import org.maplibre.geojson.utils.json

/**
 * Each of the six geometries and [GeometryCollection]
 * which make up GeoJson implement this interface.
 *
 * @since 1.0.0
 */
@Serializable
sealed interface Geometry : GeoJson {

    companion object {

        @JvmStatic
        fun fromJson(jsonString: String): Geometry = json.decodeFromString(jsonString)
    }
}
