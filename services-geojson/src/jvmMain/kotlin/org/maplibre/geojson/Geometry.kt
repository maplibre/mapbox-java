package org.maplibre.geojson

import com.google.gson.GsonBuilder
import org.maplibre.geojson.common.toJvm
import org.maplibre.geojson.model.GeometryCollection
import org.maplibre.geojson.model.LineString
import org.maplibre.geojson.model.MultiLineString
import org.maplibre.geojson.model.MultiPoint
import org.maplibre.geojson.model.MultiPolygon
import org.maplibre.geojson.model.Polygon
import org.maplibre.geojson.model.Point as CommonPoint
import org.maplibre.geojson.model.Geometry as CommonGeometry



/**
 * Each of the six geometries and [GeometryCollection]
 * which make up GeoJson implement this interface.
 *
 * @since 1.0.0
 */
@Deprecated(
    message = "Use new common models instead.",
    replaceWith = ReplaceWith("Geometry", "org.maplibre.geojson.model.Geometry"),
)
interface Geometry : GeoJson {

    companion object {

        /**
         * Create a new instance of Geometry class by passing in a formatted valid JSON String.
         *
         * @param json a formatted valid JSON string defining a GeoJson Geometry
         * @return a new instance of Geometry class defined by the values passed inside
         * this static factory method
         * @since 4.0.0
         */
        @JvmStatic
        fun fromJson(json: String): Geometry {
            return when (val commonGeometry = CommonGeometry.fromJson(json)) {
                is CommonPoint -> commonGeometry.toJvm()
                is LineString -> TODO()
                is MultiLineString -> TODO()
                is MultiPoint -> TODO()
                is MultiPolygon -> TODO()
                is Polygon -> TODO()
                is GeometryCollection -> TODO()
            }
        }
    }
}