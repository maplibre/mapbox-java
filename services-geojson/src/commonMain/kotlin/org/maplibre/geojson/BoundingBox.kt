package org.maplibre.geojson

import kotlinx.serialization.Serializable
import org.maplibre.geojson.serializer.BoundingBoxSerializer
import kotlin.jvm.JvmOverloads


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
 * @param southwest represents the bottom left corner of the bounding box when the camera is pointing due north
 * @param northeast represents the top right corner of the bounding box when the camera is pointing due north
 *
 * @since 3.0.0
 */
@Serializable(with = BoundingBoxSerializer::class)
data class BoundingBox(val southwest: Point, val northeast: Point) {

    /**
     * Define a new instance of this class by passing in four coordinates in the same order they would
     * appear in the serialized GeoJson form. Limits are placed on the minimum and maximum coordinate
     * values which can exist and comply with the GeoJson spec.
     *
     * @param west              the left side of the bounding box when the map is facing due north
     * @param south             the bottom side of the bounding box when the map is facing due north
     * @param southwestAltitude the southwest corner altitude or elevation when the map is facing due north
     * @param east              the right side of the bounding box when the map is facing due north
     * @param north             the top side of the bounding box when the map is facing due north
     * @param northEastAltitude the northeast corner altitude or elevation when the map is facing due north
     * @return a new instance of this class defined by the provided coordinates
     * @since 3.1.0
     */
    @JvmOverloads
    constructor(
        west: Double,
        south: Double,
        east: Double,
        north: Double,
        southwestAltitude: Double? = null,
        northEastAltitude: Double? = null
    ) : this(
        Point(west, south, southwestAltitude),
        Point(east, north, northEastAltitude)
    )

    /**
     * Convenience method for getting the bounding box most westerly point (longitude) as a double
     * coordinate.
     *
     * @return the most westerly coordinate inside this bounding box
     * @since 3.0.0
     */
    val west: Double
        get() = southwest.longitude

    /**
     * Convenience method for getting the bounding box most southerly point (latitude) as a double
     * coordinate.
     *
     * @return the most southerly coordinate inside this bounding box
     * @since 3.0.0
     */
    val south: Double
        get() = southwest.latitude

    /**
     * Convenience method for getting the bounding box most easterly point (longitude) as a double
     * coordinate.
     *
     * @return the most easterly coordinate inside this bounding box
     * @since 3.0.0
     */
    val east: Double
        get() = northeast.longitude

    /**
     * Convenience method for getting the bounding box most westerly point (longitude) as a double
     * coordinate.
     *
     * @return the most westerly coordinate inside this bounding box
     * @since 3.0.0
     */
    val north: Double
        get() = northeast.latitude
}
