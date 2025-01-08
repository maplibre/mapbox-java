package org.maplibre.geojson

import org.maplibre.geojson.common.toJvm
import org.maplibre.geojson.exception.GeoJsonException
import org.maplibre.geojson.model.Polygon as CommonPolygon

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
 * @since 1.0.0
 */
@Deprecated(
    message = "Use new common models instead.",
    replaceWith = ReplaceWith("Polygon", "org.maplibre.geojson.model"),
)
class Polygon internal constructor(
    type: String,
    bbox: BoundingBox?,
    coordinates: List<List<Point>>
) : CommonPolygon(
    coordinates,
    bbox,
), CoordinateContainer<List<List<Point?>?>?> {

    /**
     * Convenience method to get the outer [LineString] which defines the outer perimeter of
     * the polygon.
     *
     * @return a [LineString] defining the outer perimeter of this polygon
     * @since 3.0.0
     */
    fun outer(): LineString = outerLine.toJvm()

    /**
     * Convenience method to get a list of inner [LineString]s defining holes inside the
     * polygon. It is not guaranteed that this instance of Polygon contains holes and thus, might
     * return a null or empty list.
     *
     * @return a List of [LineString]s defining holes inside the polygon
     * @since 3.0.0
     */
    fun inner(): List<LineString> = innerLines.map { line -> line.toJvm() }

    /**
     * This describes the TYPE of GeoJson geometry this object is, thus this will always return
     * [Polygon].
     *
     * @return a String which describes the TYPE of geometry, for this object it will always return
     * `Polygon`
     * @since 1.0.0
     */
    override fun type(): String = TYPE

    /**
     * A Feature Collection might have a member named `bbox` to include information on the
     * coordinate range for it's [Feature]s. The value of the bbox member MUST be a list of
     * size 2*n where n is the number of dimensions represented in the contained feature geometries,
     * with all axes of the most southwesterly point followed by all axes of the more northeasterly
     * point. The axes order of a bbox follows the axes order of geometries.
     *
     * @return a list of double coordinate values describing a bounding box
     * @since 3.0.0
     */
    override fun bbox(): BoundingBox? = bbox?.toJvm()

    /**
     * Provides the list of [Point]s that make up the Polygon geometry. The first list holds the
     * different LineStrings, first being the outer ring and the following entries being inner holes
     * (if they exist).
     *
     * @return a list of points
     * @since 3.0.0
     */
    override fun coordinates(): List<List<Point>> = coordinates.map { line -> line.map { point -> point.toJvm() } }

    companion object {
        private const val TYPE = "Polygon"

        /**
         * Create a new instance of this class by passing in a formatted valid JSON String. If you are
         * creating a Polygon object from scratch it is better to use one of the other provided static
         * factory methods such as [.fromOuterInner]. For a valid
         * For a valid Polygon to exist, it must follow the linear ring rules and the first list of
         * coordinates are considered the outer ring by default.
         *
         * @param json a formatted valid JSON string defining a GeoJson Polygon
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 1.0.0
         */
        fun fromJson(json: String): Polygon = CommonPolygon.fromJson(json).toJvm()

        /**
         * Create a new instance of this class by defining a list of [Point]s which follow the
         * correct specifications described in the Point documentation. Note that the first and last point
         * in the list should be the same enclosing the linear ring.
         *
         * @param coordinates a list of a list of points which represent the polygon geometry
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        fun fromLngLats(coordinates: List<List<Point>>): Polygon {
            return Polygon(TYPE, null, coordinates)
        }

        /**
         * Create a new instance of this class by defining a list of [Point]s which follow the
         * correct specifications described in the Point documentation. Note that the first and last point
         * in the list should be the same enclosing the linear ring.
         *
         * @param coordinates a list of a list of points which represent the polygon geometry
         * @param bbox        optionally include a bbox definition as a double array
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        fun fromLngLats(
            coordinates: List<List<Point>>,
            bbox: BoundingBox?
        ): Polygon {
            return Polygon(TYPE, bbox, coordinates)
        }

        /**
         * Create a new instance of this class by passing in three dimensional double array which defines
         * the geometry of this polygon.
         *
         * @param coordinates a three dimensional double array defining this polygons geometry
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        fun fromLngLats(coordinates: Array<Array<DoubleArray>>): Polygon {
            val converted: MutableList<List<Point>> = ArrayList(coordinates.size)
            for (coordinate in coordinates) {
                val innerList: MutableList<Point> = ArrayList(coordinate.size)
                for (pointCoordinate in coordinate) {
                    innerList.add(Point.fromLngLat(pointCoordinate)!!)
                }
                converted.add(innerList)
            }
            return Polygon(TYPE, null, converted)
        }

        /**
         * Create a new instance of this class by passing in an outer [LineString] and optionally
         * one or more inner LineStrings. Each of these LineStrings should follow the linear ring rules.
         *
         *
         * Note that if a LineString breaks one of the linear ring rules, a [RuntimeException] will
         * be thrown.
         *
         * @param outer a LineString which defines the outer perimeter of the polygon
         * @param inner one or more LineStrings representing holes inside the outer perimeter
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        fun fromOuterInner(
            outer: LineString,
            vararg inner: LineString
        ): Polygon {
            ensureIsLinearRing(outer)
            val coordinates: MutableList<List<Point>> = ArrayList()
            coordinates.add(outer.coordinates())
            // If inner rings are set to null, return early.
            if (inner == null) {
                return Polygon(TYPE, null, coordinates)
            }
            for (lineString in inner) {
                ensureIsLinearRing(lineString)
                coordinates.add(lineString.coordinates())
            }
            return Polygon(TYPE, null, coordinates)
        }

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
        fun fromOuterInner(
            outer: LineString, bbox: BoundingBox?,
            vararg inner: LineString
        ): Polygon {
            ensureIsLinearRing(outer)
            val coordinates: MutableList<List<Point>> = ArrayList()
            coordinates.add(outer.coordinates())
            // If inner rings are set to null, return early.
            if (inner == null) {
                return Polygon(TYPE, bbox, coordinates)
            }
            for (lineString in inner) {
                ensureIsLinearRing(lineString)
                coordinates.add(lineString.coordinates())
            }
            return Polygon(TYPE, bbox, coordinates)
        }

        /**
         * Create a new instance of this class by passing in an outer [LineString] and optionally
         * one or more inner LineStrings contained within a list. Each of these LineStrings should follow
         * the linear ring rules.
         *
         *
         * Note that if a LineString breaks one of the linear ring rules, a [RuntimeException] will
         * be thrown.
         *
         * @param outer a LineString which defines the outer perimeter of the polygon
         * @param inner one or more LineStrings inside a list representing holes inside the outer
         * perimeter
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        fun fromOuterInner(
            outer: LineString,
            inner: List<LineString>?
        ): Polygon {
            ensureIsLinearRing(outer)
            val coordinates: MutableList<List<Point>> = ArrayList()
            coordinates.add(outer.coordinates())
            // If inner rings are set to null, return early.
            if (inner == null || inner.isEmpty()) {
                return Polygon(TYPE, null, coordinates)
            }
            for (lineString in inner) {
                ensureIsLinearRing(lineString)
                coordinates.add(lineString.coordinates())
            }
            return Polygon(TYPE, null, coordinates)
        }

        /**
         * Create a new instance of this class by passing in an outer [LineString] and optionally
         * one or more inner LineStrings contained within a list. Each of these LineStrings should follow
         * the linear ring rules.
         *
         *
         * Note that if a LineString breaks one of the linear ring rules, a [RuntimeException] will
         * be thrown.
         *
         * @param outer a LineString which defines the outer perimeter of the polygon
         * @param bbox  optionally include a bbox definition as a double array
         * @param inner one or more LineStrings inside a list representing holes inside the outer
         * perimeter
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        fun fromOuterInner(
            outer: LineString, bbox: BoundingBox?,
            inner: List<LineString>?
        ): Polygon {
            ensureIsLinearRing(outer)
            val coordinates: MutableList<List<Point>> = ArrayList()
            coordinates.add(outer.coordinates())
            // If inner rings are set to null, return early.
            if (inner == null) {
                return Polygon(TYPE, bbox, coordinates)
            }
            for (lineString in inner) {
                ensureIsLinearRing(lineString)
                coordinates.add(lineString.coordinates())
            }
            return Polygon(TYPE, bbox, coordinates)
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
            if (lineString.coordinates().size < 4) {
                throw GeoJsonException("LinearRings need to be made up of 4 or more coordinates.")
            }
            if (!(lineString.coordinates()[0].equals(
                    lineString.coordinates()[lineString.coordinates().size - 1]
                ))
            ) {
                throw GeoJsonException("LinearRings require first and last coordinate to be identical.")
            }
        }
    }
}