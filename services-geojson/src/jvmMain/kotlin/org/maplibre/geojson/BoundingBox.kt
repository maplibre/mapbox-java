package org.maplibre.geojson

import org.maplibre.geojson.common.toJvm
import java.io.Serializable
import org.maplibre.geojson.model.BoundingBox as CommonBoundingBox

/**
 * A GeoJson object MAY have a member named "bbox" to include information on the coordinate range
 * for its Geometries, Features, or FeatureCollections.
 *
 *
 * This class simplifies the build process for creating a bounding box and working with them when
 * deserialized. specific parameter naming helps define which coordinates belong where when a
 * bounding box instance is being created. Note that since GeoJson objects only have the option of
 * including a bounding box JSON element, the `bbox` value returned by a GeoJson object might
 * be null.
 *
 *
 * At a minimum, a bounding box will have two [Point]s or four coordinates which define the
 * box. A 3rd dimensional bounding box can be produced if elevation or altitude is defined.
 *
 * @since 3.0.0
 */
@Deprecated(
    message = "Use new common models instead.",
    replaceWith = ReplaceWith("BoundingBox", "org.maplibre.geojson.model.BoundingBox"),
)
class BoundingBox internal constructor(
    southwest: Point,
    northeast: Point
) : CommonBoundingBox(
    southwest,
    northeast
), Serializable {

    /**
     * Provides the [Point] which represents the southwest corner of this bounding box when the
     * map is facing due north.
     *
     * @return a [Point] which defines this bounding boxes southwest corner
     * @since 3.0.0
     */
    fun southwest(): Point = southwest.toJvm()

    /**
     * Provides the [Point] which represents the northeast corner of this bounding box when the
     * map is facing due north.
     *
     * @return a [Point] which defines this bounding boxes northeast corner
     * @since 3.0.0
     */
    fun northeast(): Point = northeast.toJvm()

    /**
     * Convenience method for getting the bounding box most westerly point (longitude) as a double
     * coordinate.
     *
     * @return the most westerly coordinate inside this bounding box
     * @since 3.0.0
     */
    fun west(): Double = west

    /**
     * Convenience method for getting the bounding box most southerly point (latitude) as a double
     * coordinate.
     *
     * @return the most southerly coordinate inside this bounding box
     * @since 3.0.0
     */
    fun south(): Double = south

    /**
     * Convenience method for getting the bounding box most easterly point (longitude) as a double
     * coordinate.
     *
     * @return the most easterly coordinate inside this bounding box
     * @since 3.0.0
     */
    fun east(): Double = east

    /**
     * Convenience method for getting the bounding box most westerly point (longitude) as a double
     * coordinate.
     *
     * @return the most westerly coordinate inside this bounding box
     * @since 3.0.0
     */
    fun north(): Double = north

    companion object {

        /**
         * Create a new instance of this class by passing in a formatted valid JSON String.
         *
         * @param json a formatted valid JSON string defining a Bounding Box
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        @JvmStatic
        fun fromJson(json: String): BoundingBox = CommonBoundingBox.fromJson(json).toJvm()

        /**
         * Define a new instance of this class by passing in two [Point]s, representing both the
         * southwest and northwest corners of the bounding box.
         *
         * @param southwest represents the bottom left corner of the bounding box when the camera is
         * pointing due north
         * @param northeast represents the top right corner of the bounding box when the camera is
         * pointing due north
         * @return a new instance of this class defined by the provided points
         * @since 3.0.0
         */
        @JvmStatic
        fun fromPoints(southwest: Point, northeast: Point): BoundingBox {
            return BoundingBox(southwest, northeast)
        }

        /**
         * Define a new instance of this class by passing in four coordinates in the same order they would
         * appear in the serialized GeoJson form. Limits are placed on the minimum and maximum coordinate
         * values which can exist and comply with the GeoJson spec.
         *
         * @param west  the left side of the bounding box when the map is facing due north
         * @param south the bottom side of the bounding box when the map is facing due north
         * @param east  the right side of the bounding box when the map is facing due north
         * @param north the top side of the bounding box when the map is facing due north
         * @return a new instance of this class defined by the provided coordinates
         * @since 3.0.0
         */
        @JvmStatic
        @Deprecated("As of 3.1.0, use {@link #fromLngLats} instead.")
        fun fromCoordinates(
            west: Double,
            south: Double,
            east: Double,
            north: Double
        ): BoundingBox {
            return fromLngLats(west, south, east, north)
        }

        /**
         * Define a new instance of this class by passing in four coordinates in the same order they would
         * appear in the serialized GeoJson form. Limits are placed on the minimum and maximum coordinate
         * values which can exist and comply with the GeoJson spec.
         *
         * @param west              the left side of the bounding box when the map is facing due north
         * @param south             the bottom side of the bounding box when the map is facing due north
         * @param southwestAltitude the southwest corner altitude or elevation when the map is facing due
         * north
         * @param east              the right side of the bounding box when the map is facing due north
         * @param north             the top side of the bounding box when the map is facing due north
         * @param northEastAltitude the northeast corner altitude or elevation when the map is facing due
         * north
         * @return a new instance of this class defined by the provided coordinates
         * @since 3.0.0
         */
        @JvmStatic
        @Deprecated("""As of 3.1.0, use {@link #fromLngLats} instead.""")
        fun fromCoordinates(
            west: Double,
            south: Double,
            southwestAltitude: Double,
            east: Double,
            north: Double,
            northEastAltitude: Double
        ): BoundingBox {
            return fromLngLats(west, south, southwestAltitude, east, north, northEastAltitude)
        }

        /**
         * Define a new instance of this class by passing in four coordinates in the same order they would
         * appear in the serialized GeoJson form. Limits are placed on the minimum and maximum coordinate
         * values which can exist and comply with the GeoJson spec.
         *
         * @param west  the left side of the bounding box when the map is facing due north
         * @param south the bottom side of the bounding box when the map is facing due north
         * @param east  the right side of the bounding box when the map is facing due north
         * @param north the top side of the bounding box when the map is facing due north
         * @return a new instance of this class defined by the provided coordinates
         * @since 3.1.0
         */
        @JvmStatic
        fun fromLngLats(
            west: Double,
            south: Double,
            east: Double,
            north: Double
        ): BoundingBox {
            return BoundingBox(Point.fromLngLat(west, south), Point.fromLngLat(east, north))
        }

        /**
         * Define a new instance of this class by passing in four coordinates in the same order they would
         * appear in the serialized GeoJson form. Limits are placed on the minimum and maximum coordinate
         * values which can exist and comply with the GeoJson spec.
         *
         * @param west              the left side of the bounding box when the map is facing due north
         * @param south             the bottom side of the bounding box when the map is facing due north
         * @param southwestAltitude the southwest corner altitude or elevation when the map is facing due
         * north
         * @param east              the right side of the bounding box when the map is facing due north
         * @param north             the top side of the bounding box when the map is facing due north
         * @param northEastAltitude the northeast corner altitude or elevation when the map is facing due
         * north
         * @return a new instance of this class defined by the provided coordinates
         * @since 3.1.0
         */
        @JvmStatic
        fun fromLngLats(
            west: Double,
            south: Double,
            southwestAltitude: Double,
            east: Double,
            north: Double,
            northEastAltitude: Double
        ): BoundingBox {
            return BoundingBox(
                Point.fromLngLat(west, south, southwestAltitude),
                Point.fromLngLat(east, north, northEastAltitude)
            )
        }
    }
}