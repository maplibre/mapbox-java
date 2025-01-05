package org.maplibre.geojson

import kotlinx.serialization.Serializable
import org.maplibre.geojson.utils.json
import kotlin.jvm.JvmStatic

/**
 * Each of the six geometries and [GeometryCollection]
 * which make up GeoJson implement this interface.
 *
 * @since 1.0.0
 */
@Serializable
sealed interface Geometry : GeoJson {

    companion object {

        /**
         * Create a new Geometry instance that is a sub class of one of the geometries and [GeometryCollection].
         *
         * @param jsonString a formatted valid JSON string defining a Geometry
         * @return a new instance of this class defined by the values in the JSON string
         * @since 1.0.0
         */
        @JvmStatic
        fun fromJson(jsonString: String): Geometry = json.decodeFromString(jsonString)
    }
}
