package org.maplibre.geojson

import org.maplibre.geojson.common.toJvm
import org.maplibre.geojson.model.MultiPoint as CommonMultiPoint

/**
 * A MultiPoint represents two or more geographic points that share a relationship and is one of the
 * seven geometries found in the GeoJson spec.
 *
 * This adheres to the RFC 7946 internet standard
 * when serialized into JSON. When deserialized, this class becomes an immutable object which should
 * be initiated using its static factory methods. The list of points must be equal to or greater
 * than 2.
 *
 * A sample GeoJson MultiPoint's provided below (in it's serialized state).
 * ```json
 * {
 *   "TYPE": "MultiPoint",
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
 * @since 1.0.0
 */
@Deprecated(
    message = "Use new common models instead.",
    replaceWith = ReplaceWith("MultiPoint", "org.maplibre.geojson.model.MultiPoint"),
)
class MultiPoint internal constructor(
    type: String,
    bbox: BoundingBox?,
    coordinates: List<Point>
) : CommonMultiPoint(
    coordinates,
    bbox,
), CoordinateContainer<List<Point?>?> {

    /**
     * This describes the TYPE of GeoJson this object is, thus this will always return [ ].
     *
     * @return a String which describes the TYPE of geometry, for this object it will always return
     * `MultiPoint`
     * @since 1.0.0
     */
    override fun type(): String = TYPE

    /**
     * A Feature Collection might have a member named `bbox` to include information on the
     * coordinate range for it's [Feature]s. The value of the bbox member MUST be a list of size
     * 2*n where n is the number of dimensions represented in the contained feature geometries, with
     * all axes of the most southwesterly point followed by all axes of the more northeasterly point.
     * The axes order of a bbox follows the axes order of geometries.
     *
     * @return a list of double coordinate values describing a bounding box
     * @since 3.0.0
     */
    override fun bbox(): BoundingBox? = bbox?.toJvm()

    /**
     * provides the list of [Point]s that make up the MultiPoint geometry.
     *
     * @return a list of points
     * @since 3.0.0
     */
    override fun coordinates(): List<Point> = coordinates.map { point -> point.toJvm() }

    companion object {
        private const val TYPE = "MultiPoint"

        /**
         * Create a new instance of this class by passing in a formatted valid JSON String. If you are
         * creating a MultiPoint object from scratch it is better to use one of the other provided static
         * factory methods such as [.fromLngLats]. For a valid MultiPoint to exist, it must
         * have at least 2 coordinate entries.
         *
         * @param json a formatted valid JSON string defining a GeoJson MultiPoint
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 1.0.0
         */
        @JvmStatic
        fun fromJson(json: String): MultiPoint = CommonMultiPoint.fromJson(json).toJvm()

        /**
         * Create a new instance of this class by defining a list of [Point]s which follow the
         * correct specifications described in the Point documentation. Note that there should not be any
         * duplicate points inside the list.
         *
         * Note that if less than 2 points are passed in, a runtime
         * exception will occur.
         *
         * @param points a list of [Point]s which make up the LineString geometry
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        @JvmStatic
        fun fromLngLats(points: List<Point>): MultiPoint {
            return MultiPoint(TYPE, null, points)
        }

        /**
         * Create a new instance of this class by defining a list of [Point]s which follow the
         * correct specifications described in the Point documentation. Note that there should not be any
         * duplicate points inside the list.
         *
         * Note that if less than 2 points are passed in, a runtime
         * exception will occur.
         *
         * @param points a list of [Point]s which make up the LineString geometry
         * @param bbox   optionally include a bbox definition as a double array
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        @JvmStatic
        fun fromLngLats(points: List<Point>, bbox: BoundingBox?): MultiPoint {
            return MultiPoint(TYPE, bbox, points)
        }

        @JvmStatic
        fun fromLngLats(coordinates: Array<DoubleArray>): MultiPoint {
            val converted = ArrayList<Point>(coordinates.size)
            for (i in coordinates.indices) {
                converted.add(Point.fromLngLat(coordinates[i])!!)
            }

            return MultiPoint(TYPE, null, converted)
        }
    }
}