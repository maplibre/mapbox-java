package org.maplibre.geojson

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import org.maplibre.geojson.serializer.PointDoubleArraySerializer
import org.maplibre.geojson.utils.PolylineUtils
import org.maplibre.geojson.utils.json
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * A linestring represents two or more geographic points that share a relationship and is one of the
 * seven geometries found in the GeoJson spec.
 *
 *
 * This adheres to the RFC 7946 internet standard when serialized into JSON. When deserialized, this
 * class becomes an immutable object which should be initiated using its static factory methods.
 *
 *
 * The list of points must be equal to or greater than 2. A LineString has non-zero length and
 * zero area. It may approximate a curve and need not be straight. Unlike a LinearRing, a LineString
 * is not closed.
 *
 *
 * When representing a LineString that crosses the antimeridian, interoperability is improved by
 * modifying their geometry. Any geometry that crosses the antimeridian SHOULD be represented by
 * cutting it in two such that neither part's representation crosses the antimeridian.
 *
 *
 * For example, a line extending from 45 degrees N, 170 degrees E across the antimeridian to 45
 * degrees N, 170 degrees W should be cut in two and represented as a MultiLineString.
 *
 *
 * A sample GeoJson LineString's provided below (in it's serialized state).
 * ```json
 * <pre>
 * {
 *   "TYPE": "LineString",
 *   "coordinates": [
 *     [100.0, 0.0],
 *     [101.0, 1.0]
 *   ]
 * }
 * ```
 *
 * Look over the [Point] documentation to get more
 * information about formatting your list of point objects correctly.
 *
 * @param coordinates a list of {@link Point}s which make up the LineString geometry
 * @param bbox   optionally include a bbox definition as a double array
 * @since 1.0.0
 */
@Serializable
@SerialName("LineString")
data class LineString
@JvmOverloads
constructor(
    override val coordinates: List<@Serializable(with = PointDoubleArraySerializer::class) Point>,
    override val bbox: BoundingBox? = null,
) : CoordinateContainer<List<Point>> {

    /**
     * Create a new instance of this class by defining a [MultiPoint] object and passing. The
     * multipoint object should comply with the GeoJson specifications described in the documentation.
     *
     * @param multiPoint which will make up the LineString geometry
     * @param bbox       optionally include a bbox definition as a double array
     * @return a new instance of this class defined by the values passed inside this static factory
     * method
     * @since 3.0.0
     */
    @JvmOverloads
    constructor(multiPoint: MultiPoint, bbox: BoundingBox? = null) : this(
        multiPoint.coordinates,
        bbox
    )

    /**
     * Create a new instance of this class by convert a polyline string into a lineString. This is
     * handy when an API provides you with an encoded string representing the line geometry and you'd
     * like to convert it to a useful LineString object. Note that the precision that the string
     * geometry was encoded with needs to be known and passed into this method using the precision
     * parameter.
     *
     * @param polyline  encoded string geometry to decode into a new LineString instance
     * @param precision The encoded precision which must match the same precision used when the string
     * was first encoded
     * @return a new instance of this class defined by the values passed inside this static factory
     * method
     * @since 1.0.0
     */
    @JvmOverloads
    constructor(polyline: String, precision: Int, bbox: BoundingBox? = null) : this(
        PolylineUtils.decode(polyline, precision),
        bbox
    )

    /**
     * Encode this LineString into a Polyline string for easier serializing. When passing geometry
     * information over a mobile network connection, encoding the geometry first will generally result
     * in less bandwidth usage.
     *
     * @param precision the encoded precision which fits your best use-case
     * @return a string describing the geometry of this LineString
     * @since 1.0.0
     */
    fun toPolyline(precision: Int): String {
        return PolylineUtils.encode(coordinates, precision)
    }

    /**
     * This takes the currently defined values found inside this instance and converts it to a GeoJson
     * string.
     *
     * @return a JSON string which represents this LineString geometry
     * @since 1.0.0
     */
    override fun toJson() = json.encodeToString(this)

    companion object {

        /**
         * Create a new instance of this class by passing in a formatted valid JSON String. If you are
         * creating a LineString object from scratch it is better to use the constructor.
         * For a valid lineString to exist, it must have at least 2 coordinate entries.
         * The LineString should also have non-zero distance and zero area.
         *
         * @param jsonString a formatted valid JSON string defining a GeoJson LineString
         * @return a new instance of this class defined by the values in the JSON string
         * @since 1.0.0
         */
        @JvmStatic
        fun fromJson(jsonString: String): LineString = json.decodeFromString(jsonString)
    }
}

