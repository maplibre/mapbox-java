package org.maplibre.turf

import androidx.annotation.FloatRange
import com.google.gson.JsonObject
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.LineString
import org.maplibre.geojson.MultiLineString
import org.maplibre.geojson.MultiPoint
import org.maplibre.geojson.MultiPolygon
import org.maplibre.geojson.Point
import org.maplibre.geojson.Polygon
import org.maplibre.turf.TurfConstants.TurfUnitCriteria
import kotlin.math.PI

/**
 * This class is made up of methods that take in an object, convert it, and then return the object
 * in the desired units or object.
 *
 * @see [Turfjs documentation](http://turfjs.org/docs/)
 *
 * @since 1.2.0
 */
object TurfConversion {

    private val factors = mapOf(
        TurfConstants.UNIT_MILES to 3960.0,
        TurfConstants.UNIT_NAUTICAL_MILES to 3441.145,
        TurfConstants.UNIT_DEGREES to 57.2957795,
        TurfConstants.UNIT_RADIANS to 1.0,
        TurfConstants.UNIT_INCHES to 250905600.0,
        TurfConstants.UNIT_YARDS to 6969600.0,
        TurfConstants.UNIT_METERS to 6373000.0,
        TurfConstants.UNIT_CENTIMETERS to 6.373e+8,
        TurfConstants.UNIT_KILOMETERS to 6373.0,
        TurfConstants.UNIT_FEET to 20908792.65,
        TurfConstants.UNIT_CENTIMETRES to 6.373e+8,
        TurfConstants.UNIT_METRES to 6373000.0,
        TurfConstants.UNIT_KILOMETRES to 6373.0,
    )

    /**
     * Convert a distance measurement (assuming a spherical Earth) from a real-world unit into degrees
     * Valid units: miles, nauticalmiles, inches, yards, meters, metres, centimeters, kilometres,
     * feet.
     *
     * @param distance in real units
     * @param units    can be degrees, radians, miles, or kilometers inches, yards, metres, meters,
     * kilometres, kilometers.
     * @return a double value representing the distance in degrees
     * @since 3.0.0
     */
    @JvmStatic
    fun lengthToDegrees(distance: Double, @TurfUnitCriteria units: String): Double {
        return radiansToDegrees(lengthToRadians(distance, units))
    }

    /**
     * Converts an angle in degrees to radians.
     *
     * @param degrees angle between 0 and 360 degrees
     * @return angle in radians
     * @since 3.1.0
     */
    @JvmStatic
    fun degreesToRadians(degrees: Double): Double {
        val radians = degrees % 360
        return radians * PI / 180
    }

    /**
     * Converts an angle in radians to degrees.
     *
     * @param radians angle in radians
     * @return degrees between 0 and 360 degrees
     * @since 3.0.0
     */
    @JvmStatic
    fun radiansToDegrees(radians: Double): Double {
        val degrees: Double = radians % (2 * PI)
        return degrees * 180 / PI
    }

    /**
     * Convert a distance measurement (assuming a spherical Earth) from radians to a more friendly
     * unit.
     *
     * @param radians a double using unit radian
     * @param units   pass in one of the units defined in [TurfUnitCriteria]
     * @return converted radian to distance value
     * @since 1.2.0
     */
    @JvmStatic
    @JvmOverloads
    fun radiansToLength(radians: Double, units: String = TurfConstants.UNIT_DEFAULT): Double {
        return radians * factors[units]!! //TODO fabi755
    }

    /**
     * Convert a distance measurement (assuming a spherical Earth) from a real-world unit into
     * radians.
     *
     * @param distance double representing a distance value
     * @param units    pass in one of the units defined in [TurfUnitCriteria]
     * @return converted distance to radians value
     * @since 1.2.0
     */
    @JvmStatic
    @JvmOverloads
    fun lengthToRadians(distance: Double, units: String = TurfConstants.UNIT_DEFAULT): Double {
        check(distance >= 0) { "Distance must be greater than or equal to 0" }
        return distance / factors[units]!! //TODO fabi755
    }

    /**
     * Converts a distance to a different unit specified.
     *
     * @param distance     the distance to be converted
     * @param originalUnit of the distance, must be one of the units defined in
     * [TurfUnitCriteria]
     * @param finalUnit    returned unit, [TurfConstants.UNIT_DEFAULT] if not specified
     * @return the converted distance
     * @since 2.2.0
     */
    @JvmStatic
    @JvmOverloads
    fun convertLength(
        distance: Double,
        @TurfUnitCriteria originalUnit: String,
        @TurfUnitCriteria finalUnit: String = TurfConstants.UNIT_DEFAULT
    ): Double {
        return radiansToLength(lengthToRadians(distance, originalUnit), finalUnit)
    }

    /**
     * Takes a [FeatureCollection] and
     * returns all positions as [Point] objects.
     *
     * @param featureCollection a [FeatureCollection] object
     * @return a new [FeatureCollection] object with [Point] objects
     * @since 4.8.0
     */
    @JvmStatic
    fun explode(featureCollection: FeatureCollection): FeatureCollection {
        val points = TurfMeta.coordAll(featureCollection, true)
            .map { point -> Feature.fromGeometry(point) }

        return FeatureCollection.fromFeatures(points)
    }

    /**
     * Takes a [Feature]  and
     * returns its position as a [Point] objects.
     *
     * @param feature a [Feature] object
     * @return a new [FeatureCollection] object with [Point] objects
     * @since 4.8.0
     */
    @JvmStatic
    fun explode(feature: Feature): FeatureCollection {
        val points = TurfMeta.coordAll(feature, true)
            .map { point -> Feature.fromGeometry(point) }

        return FeatureCollection.fromFeatures(points)
    }

    /**
     * Takes a [Feature] that contains [Polygon] and a properties [JsonObject] and
     * covert it to a [Feature] that contains [LineString] or [MultiLineString].
     *
     * @param feature a [Feature] object that contains [Polygon]
     * @param properties a [JsonObject] that represents a feature's properties
     * @return  a [Feature] object that contains [LineString] or [MultiLineString]
     * @since 4.9.0
     */
    @JvmStatic
    @JvmOverloads
    fun polygonToLine(feature: Feature, properties: JsonObject? = null): Feature {
        return (feature.geometry() as? Polygon)?.let { polygon ->
            polygonToLine(
                polygon,
                properties
                    ?: feature.properties().takeIf { feature.type() == "Feature" }
            )
        } ?: throw TurfException("Feature's geometry must be Polygon")
    }

    /**
     * Takes a [Polygon] and a properties [JsonObject] and
     * covert it to a [Feature] that contains [LineString] or [MultiLineString].
     *
     * @param polygon a [Polygon] object
     * @param properties a [JsonObject] that represents a feature's properties
     * @return  a [Feature] object that contains [LineString] or [MultiLineString]
     * @since 4.9.0
     */
    @JvmStatic
    @JvmOverloads
    fun polygonToLine(polygon: Polygon, properties: JsonObject? = null): Feature? {
        return coordsToLine(polygon.coordinates(), properties)
    }

    /**
     * Takes a [MultiPolygon] and a properties [JsonObject] and
     * covert it to a [FeatureCollection] that contains list
     * of [Feature] of [LineString] or [MultiLineString].
     *
     * @param multiPolygon a [MultiPolygon] object
     * @param properties a [JsonObject] that represents a feature's properties
     * @return  a [FeatureCollection] object that contains
     * list of [Feature] of [LineString] or [MultiLineString]
     * @since 4.9.0
     */
    @JvmStatic
    @JvmOverloads
    fun polygonToLine(
        multiPolygon: MultiPolygon,
        properties: JsonObject? = null
    ): FeatureCollection {
        val features = multiPolygon.coordinates()
            .map { polygonCoordinates -> coordsToLine(polygonCoordinates, properties) }

        return FeatureCollection.fromFeatures(features)
    }

    /**
     * Takes a [Feature] that contains [MultiPolygon] and a
     * properties [JsonObject] and
     * covert it to a [FeatureCollection] that contains
     * list of [Feature] of [LineString] or [MultiLineString].
     *
     * @param feature a [Feature] object that contains [MultiPolygon]
     * @param properties a [JsonObject] that represents a feature's properties
     * @return  a [FeatureCollection] object that contains
     * list of [Feature] of [LineString] or [MultiLineString]
     * @since 4.9.0
     */
    @JvmStatic
    @JvmOverloads
    fun multiPolygonToLine(
        feature: Feature,
        properties: JsonObject? = null
    ): FeatureCollection {
        return (feature.geometry() as? MultiPolygon)?.let { multiPolygon ->
            polygonToLine(
                multiPolygon,
                properties
                    ?: feature.properties().takeIf { feature.type() == "Feature" }
            )
        } ?: throw TurfException("Feature's geometry must be MultiPolygon")
    }

    private fun coordsToLine(coordinates: List<List<Point>>, properties: JsonObject?): Feature? {
        return when {
            coordinates.isEmpty() ->
                null

            coordinates.size == 1 -> {
                val lineString = LineString.fromLngLats(coordinates[0])
                return Feature.fromGeometry(lineString, properties)
            }

            else -> {
                val multiLineString = MultiLineString.fromLngLats(coordinates)
                return Feature.fromGeometry(multiLineString, properties)
            }
        }
    }

    /**
     * Combines a FeatureCollection of geometries and returns
     * a [FeatureCollection] with "Multi-" geometries in it.
     * If the original FeatureCollection parameter has [Point](s)
     * and/or [MultiPoint]s), the returned
     * FeatureCollection will include a [MultiPoint] object.
     *
     *
     * If the original FeatureCollection parameter has
     * [LineString](s) and/or [MultiLineString]s), the returned
     * FeatureCollection will include a [MultiLineString] object.
     *
     *
     * If the original FeatureCollection parameter has
     * [Polygon](s) and/or [MultiPolygon]s), the returned
     * FeatureCollection will include a [MultiPolygon] object.
     *
     * @param originalFeatureCollection a [FeatureCollection]
     *
     * @return a [FeatureCollection] with a "Multi-" geometry
     * or "Multi-" geometries.
     *
     * @since 4.10.0
     */
    @JvmStatic
    fun combine(originalFeatureCollection: FeatureCollection): FeatureCollection {
        val features = originalFeatureCollection.features()
        if (features == null) {
            throw TurfException("Your FeatureCollection is null.")
        } else if (features.size == 0) {
            throw TurfException("Your FeatureCollection doesn't have any Feature objects in it.")
        }

        val pointList = mutableListOf<Point>()
        val lineStringList = mutableListOf<LineString>()
        val polygonList = mutableListOf<Polygon>()
        features.forEach { feature ->
            when (val geometry = feature.geometry()) {
                is Point -> pointList.add(geometry)
                is MultiPoint -> pointList.addAll(geometry.coordinates())
                is LineString -> lineStringList.add(geometry)
                is MultiLineString -> lineStringList.addAll(geometry.lineStrings())
                is Polygon -> polygonList.add(geometry)
                is MultiPolygon -> polygonList.addAll(geometry.polygons())
            }
        }

        val combinedFeatures = mutableListOf<Feature>()
        if (pointList.isNotEmpty()) {
            combinedFeatures.add(Feature.fromGeometry(MultiPoint.fromLngLats(pointList)))
        }
        if (lineStringList.isNotEmpty()) {
            combinedFeatures.add(Feature.fromGeometry(MultiLineString.fromLineStrings(lineStringList)))
        }
        if (polygonList.isNotEmpty()) {
            combinedFeatures.add(Feature.fromGeometry(MultiPolygon.fromPolygons(polygonList)))
        }

        return if (combinedFeatures.isEmpty()) {
            originalFeatureCollection
        } else {
            FeatureCollection.fromFeatures(combinedFeatures)
        }
    }
}
