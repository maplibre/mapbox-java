package org.maplibre.geojson

import androidx.annotation.Keep
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import org.maplibre.geojson.serializer.GeometrySerializer

/**
 * Each of the six geometries and [GeometryCollection]
 * which make up GeoJson implement this interface.
 *
 * @since 1.0.0
 */
@Serializable
//@Serializable
//@Polymorphic
sealed interface Geometry : GeoJson {

    companion object {

        @JvmStatic
        fun fromJson(jsonString: String): Geometry = json.decodeFromString(jsonString)
    }
}
