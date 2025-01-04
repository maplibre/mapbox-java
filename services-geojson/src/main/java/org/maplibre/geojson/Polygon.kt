package org.maplibre.geojson

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import org.maplibre.geojson.exception.GeoJsonException
import org.maplibre.geojson.serializer.PointDoubleArraySerializer
import org.maplibre.geojson.utils.json

/**
 * This class represents a GeoJson Polygon which may or may not include polygon holes.
 *
 *
 * To specify a constraint specific to Polygons, it is useful to introduce the concept of a linear
 * ring:
 *
 *  * A linear ring is a closed LineString with four or more coordinates.
 *  * The first and last coordinates are equivalent, and they MUST contain identical values; their
 * representation SHOULD also be identical.
 *  * A linear ring is the boundary of a surface or the boundary of a hole in a surface.
 *  * A linear ring MUST follow the right-hand rule with respect to the area it bounds, i.e.,
 * exterior rings are counterclockwise, and holes are clockwise.
 *
 * Note that most of the rules listed above are checked when a Polygon instance is created (the
 * exception being the last rule). If one of the rules is broken, a [RuntimeException] will
 * occur.
 *
 *
 * Though a linear ring is not explicitly represented as a GeoJson geometry TYPE, it leads to a
 * canonical formulation of the Polygon geometry TYPE. When initializing a new instance of this
 * class, a LineString for the outer and optionally an inner are checked to ensure a valid linear
 * ring.
 *
 *
 * An example of a serialized polygon with no holes is given below:
 * ```json
 * {
 *   "TYPE": "Polygon",
 *   "coordinates": [
 *     [[100.0, 0.0],
 *     [101.0, 0.0],
 *     [101.0, 1.0],
 *     [100.0, 1.0],
 *     [100.0, 0.0]]
 *   ]
 * }
 * ```
 *
 * @param coordinates a list of a list of points which represent the polygon geometry
 * @param bbox        optionally include a bbox definition as a double array
 * @since 1.0.0
 */
@Serializable
@SerialName("Polygon")
data class Polygon
@JvmOverloads
constructor(
    override val coordinates: List<List<@Serializable(with = PointDoubleArraySerializer::class) Point>>,
    override val bbox: BoundingBox? = null,
) : CoordinateContainer<List<List<Point>>> {

    /**
     * Convenience method to get the outer [LineString] which defines the outer perimeter of
     * the polygon.
     *
     * @return a [LineString] defining the outer perimeter of this polygon
     * @since 3.0.0
     */
    val outerLine: LineString
        get() = LineString(coordinates.first())

    /**
     * Convenience method to get a list of inner [LineString]s defining holes inside the
     * polygon. It is not guaranteed that this instance of Polygon contains holes and thus, might
     * return a null or empty list.
     *
     * @return a List of [LineString]s defining holes inside the polygon
     * @since 3.0.0
     */
    val innerLines: List<LineString>
        get() = coordinates.drop(1).map { points -> LineString(points) }

    /**
     * This takes the currently defined values found inside this instance and converts it to a GeoJson
     * string.
     *
     * @return a JSON string which represents this Polygon geometry
     * @since 1.0.0
     */
    override fun toJson(): String = json.encodeToString(this)

    companion object {

        /**
         * Create a new instance of this class by passing in an outer [LineString] and optionally
         * one or more inner LineStrings. Each of these LineStrings should follow the linear ring rules.
         *
         *
         * Note that if a LineString breaks one of the linear ring rules, a [RuntimeException] will
         * be thrown.
         *
         * @param outer a LineString which defines the outer perimeter of the polygon
         * @param bbox  optionally include a bbox definition as a double array
         * @param inner one or more LineStrings representing holes inside the outer perimeter
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        @JvmStatic
        @JvmOverloads
        fun fromOuterInner(
            outer: LineString,
            inner: List<LineString> = emptyList(),
            bbox: BoundingBox? = null,
        ): Polygon {
            ensureIsLinearRing(outer)

            val coordinates = listOf(
                outer.coordinates,
                *inner.map { innerLine ->
                    ensureIsLinearRing(innerLine)
                    innerLine.coordinates
                }.toTypedArray()
            )

            return Polygon(coordinates, bbox)
        }

        /**
         * Checks to ensure that the LineStrings defining the polygon correctly and adhering to the linear
         * ring rules.
         *
         * @param lineString [LineString] the polygon geometry
         * @throws GeoJsonException if number of coordinates are less than 4,
         * or first and last coordinates are not identical (it is not linear ring)
         * @since 3.0.0
         */
        private fun ensureIsLinearRing(lineString: LineString) {
            if (lineString.coordinates.size < 4) {
                throw GeoJsonException("LinearRings need to be made up of 4 or more coordinates.")
            }

            if (lineString.coordinates.first() != lineString.coordinates.last()) {
                throw GeoJsonException("LinearRings require first and last coordinate to be identical.")
            }
        }

        /**
         * Create a new instance of this class by passing in a formatted valid JSON String. If you are
         * creating a Polygon object from scratch it is better to use the constructor.
         * For a valid Polygon to exist, it must follow the linear ring rules and the first list of
         * coordinates are considered the outer ring by default.
         *
         * @param jsonString a formatted valid JSON string defining a GeoJson Polygon
         * @return a new instance of this class defined by the values in the JSON string method
         * @since 1.0.0
         */
        @JvmStatic
        fun fromJson(jsonString: String): Polygon = json.decodeFromString(jsonString)
    }
}