package org.maplibre.geojson.turf

import kotlinx.serialization.json.JsonElement
import org.maplibre.geojson.model.Feature
import org.maplibre.geojson.model.FeatureCollection
import org.maplibre.geojson.model.LineString
import org.maplibre.geojson.model.MultiLineString
import org.maplibre.geojson.model.MultiPoint
import org.maplibre.geojson.model.MultiPolygon
import org.maplibre.geojson.model.Point
import org.maplibre.geojson.model.Polygon
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
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
    fun lengthToDegrees(distance: Double, units: TurfUnit): Double {
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
        val degrees = radians % (2 * PI)
        return degrees * 180 / PI
    }

    /**
     * Convert a distance measurement (assuming a spherical Earth) from radians to a more friendly
     * unit.
     *
     * @param radians a double using unit radian
     * @param unit   pass in one of the units defined in [TurfUnit]
     * @return converted radian to distance value
     * @since 1.2.0
     */
    @JvmStatic
    @JvmOverloads
    fun radiansToLength(radians: Double, unit: TurfUnit = TurfUnit.DEFAULT): Double {
        return radians * unit.factor
    }

    /**
     * Convert a distance measurement (assuming a spherical Earth) from a real-world unit into
     * radians.
     *
     * @param distance double representing a distance value
     * @param unit    pass in one of the units defined in [TurfUnit]
     * @return converted distance to radians value
     * @since 1.2.0
     */
    @JvmStatic
    @JvmOverloads
    fun lengthToRadians(distance: Double, unit: TurfUnit = TurfUnit.DEFAULT): Double {
        return distance / unit.factor
    }

    /**
     * Converts a distance to a different unit specified.
     *
     * @param distance     the distance to be converted
     * @param originalUnit of the distance, must be one of the units defined in [TurfUnit]
     * @param finalUnit    returned unit, [TurfUnit.DEFAULT] if not specified
     * @return the converted distance
     * @since 2.2.0
     */
    @JvmStatic
    @JvmOverloads
    fun convertLength(
        distance: Double,
        originalUnit: TurfUnit,
        finalUnit: TurfUnit = TurfUnit.DEFAULT
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
            .map { point -> Feature(point) }

        return FeatureCollection(points)
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
            .map { point -> Feature(point) }

        return FeatureCollection(points)
    }

    /**
     * Takes a [Feature] that contains [Polygon] and a properties [Map] and
     * covert it to a [Feature] that contains [LineString] or [MultiLineString].
     *
     * @param feature a [Feature] object that contains [Polygon]
     * @param properties a [Map] that represents a feature's properties
     * @return  a [Feature] object that contains [LineString] or [MultiLineString]
     * @since 4.9.0
     */
    @JvmStatic
    @JvmOverloads
    fun polygonToLine(feature: Feature, properties: MutableMap<String, JsonElement>? = null): Feature {
        return (feature.geometry as? Polygon)?.let { polygon ->
            polygonToLine(
                polygon,
                properties ?: feature.properties
            )
        } ?: throw TurfException("Feature's geometry must be Polygon")
    }

    /**
     * Takes a [Polygon] and a properties [Map] and
     * covert it to a [Feature] that contains [LineString] or [MultiLineString].
     *
     * @param polygon a [Polygon] object
     * @param properties a [Map] that represents a feature's properties
     * @return  a [Feature] object that contains [LineString] or [MultiLineString]
     * @since 4.9.0
     */
    @JvmStatic
    @JvmOverloads
    fun polygonToLine(polygon: Polygon, properties: MutableMap<String, JsonElement>? = null): Feature? {
        return coordsToLine(polygon.coordinates, properties)
    }

    /**
     * Takes a [MultiPolygon] and a properties [Map] and
     * covert it to a [FeatureCollection] that contains list
     * of [Feature] of [LineString] or [MultiLineString].
     *
     * @param multiPolygon a [MultiPolygon] object
     * @param properties a [Map] that represents a feature's properties
     * @return  a [FeatureCollection] object that contains
     * list of [Feature] of [LineString] or [MultiLineString]
     * @since 4.9.0
     */
    @JvmStatic
    @JvmOverloads
    fun polygonToLine(
        multiPolygon: MultiPolygon,
        properties: MutableMap<String, JsonElement>? = null
    ): FeatureCollection {
        val features = multiPolygon.coordinates
            .mapNotNull { polygonCoordinates -> coordsToLine(polygonCoordinates, properties) }

        return FeatureCollection(features)
    }

    /**
     * Takes a [Feature] that contains [MultiPolygon] and a
     * properties [Map] and
     * covert it to a [FeatureCollection] that contains
     * list of [Feature] of [LineString] or [MultiLineString].
     *
     * @param feature a [Feature] object that contains [MultiPolygon]
     * @param properties a [Map] that represents a feature's properties
     * @return  a [FeatureCollection] object that contains
     * list of [Feature] of [LineString] or [MultiLineString]
     * @since 4.9.0
     */
    @JvmStatic
    @JvmOverloads
    fun multiPolygonToLine(
        feature: Feature,
        properties: MutableMap<String, JsonElement>? = null
    ): FeatureCollection {
        return (feature.geometry as? MultiPolygon)?.let { multiPolygon ->
            polygonToLine(
                multiPolygon,
                properties
                    ?: feature.properties
            )
        } ?: throw TurfException("Feature's geometry must be MultiPolygon")
    }

    private fun coordsToLine(
        coordinates: List<List<Point>>,
        properties: MutableMap<String, JsonElement>?
    ): Feature? {
        return when {
            coordinates.isEmpty() ->
                null

            coordinates.size == 1 -> {
                val lineString = LineString(coordinates[0])
                return Feature(lineString, properties)
            }

            else -> {
                val multiLineString = MultiLineString(coordinates)
                return Feature(multiLineString, properties)
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
        val features = originalFeatureCollection.features
        if (features.isEmpty()) {
            throw TurfException("Your FeatureCollection doesn't have any Feature objects in it.")
        }

        val pointList = mutableListOf<Point>()
        val lineStringList = mutableListOf<LineString>()
        val polygonList = mutableListOf<Polygon>()
        features.forEach { feature ->
            when (val geometry = feature.geometry) {
                is Point -> pointList.add(geometry)
                is MultiPoint -> pointList.addAll(geometry.coordinates)
                is LineString -> lineStringList.add(geometry)
                is MultiLineString -> lineStringList.addAll(geometry.lineStrings)
                is Polygon -> polygonList.add(geometry)
                is MultiPolygon -> polygonList.addAll(geometry.polygons)
                else -> {}
            }
        }

        val combinedFeatures = mutableListOf<Feature>()
        if (pointList.isNotEmpty()) {
            combinedFeatures.add(Feature(MultiPoint(pointList)))
        }
        if (lineStringList.isNotEmpty()) {
            combinedFeatures.add(Feature(MultiLineString.fromLineStrings(lineStringList)))
        }
        if (polygonList.isNotEmpty()) {
            combinedFeatures.add(Feature(MultiPolygon.fromPolygons(polygonList)))
        }

        return if (combinedFeatures.isEmpty()) {
            originalFeatureCollection
        } else {
            FeatureCollection(combinedFeatures)
        }
    }
}
