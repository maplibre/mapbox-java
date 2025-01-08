package org.maplibre.turf

import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.LineString
import org.maplibre.geojson.MultiLineString
import org.maplibre.geojson.MultiPoint
import org.maplibre.geojson.MultiPolygon
import org.maplibre.geojson.Point
import org.maplibre.geojson.Polygon
import org.maplibre.geojson.common.toJvm
import org.maplibre.geojson.common.toKtxJsonMap
import org.maplibre.turf.TurfConstants.TurfUnitCriteria
import org.maplibre.turf.common.toUnit
import org.maplibre.geojson.turf.TurfConversion as CommonTurfConversion
import com.google.gson.JsonObject as GsonJsonObject

/**
 * This class is made up of methods that take in an object, convert it, and then return the object
 * in the desired units or object.
 *
 * @see [Turfjs documentation](http://turfjs.org/docs/)
 *
 * @since 1.2.0
 */
@Deprecated(
    message = "Use new common Turf utils instead.",
    replaceWith = ReplaceWith("TurfConversion", "org.maplibre.geojson.turf.TurfConversion"),
)
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
    fun lengthToDegrees(distance: Double, @TurfUnitCriteria units: String): Double {
        return CommonTurfConversion.lengthToDegrees(distance, units.toUnit())
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
        return CommonTurfConversion.degreesToRadians(degrees)
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
        return CommonTurfConversion.radiansToDegrees(radians)
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
    fun radiansToLength(
        radians: Double,
        @TurfUnitCriteria units: String = TurfConstants.UNIT_DEFAULT
    ): Double {
        return CommonTurfConversion.radiansToLength(radians, units.toUnit())
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
    fun lengthToRadians(
        distance: Double,
        @TurfUnitCriteria units: String = TurfConstants.UNIT_DEFAULT
    ): Double {
        return CommonTurfConversion.lengthToRadians(distance, units.toUnit())
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
        return CommonTurfConversion.convertLength(
            distance,
            originalUnit.toUnit(),
            finalUnit.toUnit()
        )
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
        return CommonTurfConversion.explode(featureCollection).toJvm()
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
        return CommonTurfConversion.explode(feature).toJvm()
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
    fun polygonToLine(feature: Feature, properties: GsonJsonObject? = null): Feature? {
        return CommonTurfConversion.polygonToLine(feature, properties?.toKtxJsonMap()).toJvm()
    }

    /**
     * Takes a [Polygon] and
     * covert it to a [Feature] that contains [LineString] or [MultiLineString].
     *
     * @param polygon a [Polygon] object
     * @return  a [Feature] object that contains [LineString] or [MultiLineString]
     * @since 4.9.0
     */
    @JvmStatic
    fun polygonToLine(polygon: Polygon): Feature? {
        return CommonTurfConversion.polygonToLine(polygon)?.toJvm()
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
    fun polygonToLine(
        polygon: Polygon, properties: GsonJsonObject?
    ): Feature? {
        return CommonTurfConversion.polygonToLine(polygon, properties?.toKtxJsonMap())?.toJvm()
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
        properties: GsonJsonObject? = null
    ): FeatureCollection {
        return CommonTurfConversion.polygonToLine(multiPolygon, properties?.toKtxJsonMap()).toJvm()
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
        properties: GsonJsonObject? = null
    ): FeatureCollection {
        return CommonTurfConversion.multiPolygonToLine(feature, properties?.toKtxJsonMap()).toJvm()
    }

    /**
     *
     *
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
        return CommonTurfConversion.combine(originalFeatureCollection).toJvm()
    }
}