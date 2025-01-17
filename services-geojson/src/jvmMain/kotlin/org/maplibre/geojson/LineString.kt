package org.maplibre.geojson

import org.maplibre.geojson.common.toJvm
import org.maplibre.geojson.utils.PolylineUtils.decode
import org.maplibre.geojson.model.LineString as CommonLineString

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
 * Look over the [Point] documentation to get more
 * information about formatting your list of point objects correctly.
 *
 * @since 1.0.0
 */
@Deprecated(
    message = "Use new common models instead.",
    replaceWith = ReplaceWith("LineString", "org.maplibre.geojson.model.LineString"),
)
class LineString internal constructor(
    type: String,
    bbox: BoundingBox?,
    coordinates: List<Point>
) : CommonLineString(
    coordinates,
    bbox,
), CoordinateContainer<List<Point>> {

    /**
     * This describes the TYPE of GeoJson geometry this object is, thus this will always return
     * [LineString].
     *
     * @return a String which describes the TYPE of geometry, for this object it will always return
     * `LineString`
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
     * Provides the list of [Point]s that make up the LineString geometry.
     *
     * @return a list of points
     * @since 3.0.0
     */
    override fun coordinates(): List<Point> = coordinates.map { point -> point.toJvm() }

    companion object {
        private const val TYPE = "LineString"

        /**
         * Create a new instance of this class by passing in a formatted valid JSON String. If you are
         * creating a LineString object from scratch it is better to use one of the other provided static
         * factory methods such as [.fromLngLats]. For a valid lineString to exist, it must
         * have at least 2 coordinate entries. The LineString should also have non-zero distance and zero
         * area.
         *
         * @param json a formatted valid JSON string defining a GeoJson LineString
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 1.0.0
         */
        @JvmStatic
        fun fromJson(json: String): LineString = CommonLineString.fromJson(json).toJvm()

        /**
         * Create a new instance of this class by defining a [MultiPoint] object and passing. The
         * multipoint object should comply with the GeoJson specifications described in the documentation.
         *
         * @param multiPoint which will make up the LineString geometry
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        @JvmStatic
        fun fromLngLats(multiPoint: MultiPoint): LineString {
            return LineString(TYPE, null, multiPoint.coordinates())
        }

        /**
         * Create a new instance of this class by defining a list of [Point]s which follow the
         * correct specifications described in the Point documentation. Note that there should not be any
         * duplicate points inside the list and the points combined should create a LineString with a
         * distance greater than 0.
         *
         *
         * Note that if less than 2 points are passed in, a runtime exception will occur.
         *
         *
         * @param points a list of [Point]s which make up the LineString geometry
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        @JvmStatic
        fun fromLngLats(points: List<Point>): LineString {
            return LineString(TYPE, null, points)
        }

        /**
         * Create a new instance of this class by defining a list of [Point]s which follow the
         * correct specifications described in the Point documentation. Note that there should not be any
         * duplicate points inside the list and the points combined should create a LineString with a
         * distance greater than 0.
         *
         *
         * Note that if less than 2 points are passed in, a runtime exception will occur.
         *
         *
         * @param points a list of [Point]s which make up the LineString geometry
         * @param bbox   optionally include a bbox definition as a double array
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        @JvmStatic
        fun fromLngLats(points: List<Point>, bbox: BoundingBox?): LineString {
            return LineString(TYPE, bbox, points)
        }

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
        @JvmStatic
        fun fromLngLats(multiPoint: MultiPoint, bbox: BoundingBox?): LineString {
            return LineString(TYPE, bbox, multiPoint.coordinates())
        }

        @JvmStatic
        fun fromLngLats(coordinates: Array<DoubleArray>): LineString {
            val converted = ArrayList<Point>(coordinates.size)
            for (coordinate in coordinates) {
                converted.add(Point.fromLngLat(coordinate)!!)
            }
            return fromLngLats(converted)
        }

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
        @JvmStatic
        fun fromPolyline(polyline: String, precision: Int): LineString {
            return fromLngLats(decode(polyline, precision).map { pt -> pt.toJvm() }, null)
        }
    }
}