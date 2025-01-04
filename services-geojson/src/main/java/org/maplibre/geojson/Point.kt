package org.maplibre.geojson

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import org.maplibre.geojson.utils.json

/**
 * A point represents a single geographic position and is one of the seven Geometries found in the
 * GeoJson spec.
 *
 *
 * This adheres to the RFC 7946 internet standard when serialized into JSON. When deserialized, this
 * class becomes an immutable object which should be initiated using its static factory methods.
 *
 *
 * Coordinates are in x, y order (easting, northing for projected coordinates), longitude, and
 * latitude for geographic coordinates), precisely in that order and using double values. Altitude
 * or elevation MAY be included as an optional third parameter while creating this object.
 *
 *
 * The size of a GeoJson text in bytes is a major interoperability consideration, and precision of
 * coordinate values has a large impact on the size of texts when serialized. For geographic
 * coordinates with units of degrees, 6 decimal places (a default common in, e.g., sprintf) amounts
 * to about 10 centimeters, a precision well within that of current GPS systems. Implementations
 * should consider the cost of using a greater precision than necessary.
 *
 *
 * Furthermore, pertaining to altitude, the WGS 84 datum is a relatively coarse approximation of the
 * geoid, with the height varying by up to 5 m (but generally between 2 and 3 meters) higher or
 * lower relative to a surface parallel to Earth's mean sea level.
 *
 *
 * A sample GeoJson Point's provided below (in its serialized state).
 * ```json
 * {
 *   "type": "Point",
 *   "coordinates": [100.0, 0.0]
 * }
 * ```
 *
 * @param coordinates a list of double values representing the longitude, latitude, and optionally altitude position of this point
 * @param bbox      optionally include a bbox definition as a double array
 * @since 1.0.0
 */
@Serializable
@SerialName("Point")
data class Point
@JvmOverloads
constructor(
    override val coordinates: List<Double>,
    override val bbox: BoundingBox? = null,
) : CoordinateContainer<List<Double>> {

    /**
     * Create a new instance of this class defining a longitude and latitude value in that respective
     * order. While no limit is placed on decimal precision, for performance reasons
     * when serializing and deserializing it is suggested to limit decimal precision to within 6
     * decimal places. An optional altitude value can be passed in and can vary between negative
     * infinity and positive infinity.
     *
     * @param longitude a double value representing the x position of this point
     * @param latitude  a double value representing the y position of this point
     * @param altitude  a double value which can be negative or positive infinity representing either
     * elevation or altitude
     * @param bbox      optionally include a bbox definition as a double array
     * @return a new instance of this class defined by the values passed inside this static factory
     * method
     * @since 7.0.0
     */
    constructor(
        longitude: Double,
        latitude: Double,
        altitude: Double? = null,
        bbox: BoundingBox? = null
    ) : this(
        listOfNotNull(
            longitude,
            latitude,
            altitude
        ),
        bbox
    )

    /**
     * This returns a double value representing the x or easting position of
     * this point. ideally, this value would be restricted to 6 decimal places to correctly follow the
     * GeoJson spec.
     *
     * @return a double value representing the x or easting position of this
     *   point
     * @since 3.0.0
     */
    val longitude: Double
        get() = coordinates[0]

    /**
     * This returns a double value representing the y or northing position of
     * this point. ideally, this value would be restricted to 6 decimal places to correctly follow the
     * GeoJson spec.
     *
     * @return a double value representing the y or northing position of this
     *   point
     * @since 3.0.0
     */
    val latitude: Double
        get() = coordinates[1]

    /**
     * Optionally, the coordinate spec in GeoJson allows for altitude values to be placed inside the
     * coordinate array. {@link #hasAltitude()} can be used to determine if this value was set during
     * initialization of this Point instance. This double value should only be used to represent
     * either the elevation or altitude value at this particular point.
     *
     * @return a double value ranging from negative to positive infinity
     * @since 3.0.0
     */
    val altitude: Double?
        get() = coordinates.getOrNull(2)

    /**
     * This takes the currently defined values found inside this instance and converts it to a GeoJson
     * string.
     *
     * @return a JSON string which represents this Point geometry
     * @since 1.0.0
     */
    override fun toJson() = json.encodeToString(this)

    companion object {

        /**
         * Create a new instance of this class by passing in a formatted valid JSON String. If you are
         * creating a Point object from scratch it is better to use the constructor.
         * While no limit is placed on decimal precision, for performance reasons when serializing
         * and deserializing it is suggested to limit decimal precision to within 6 decimal places.
         *
         * @param jsonString a formatted valid JSON string defining a GeoJson Point
         * @return a new instance of this class defined by the values in the JSON string method
         * @since 1.0.0
         */
        @JvmStatic
        fun fromJson(jsonString: String): Point = json.decodeFromString(jsonString)
    }
}
