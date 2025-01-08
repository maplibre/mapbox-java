package org.maplibre.geojson

import org.maplibre.geojson.common.toJvm
import java.util.Arrays
import org.maplibre.geojson.model.MultiLineString as CommonMultiLineString

/**
 * A multilinestring is an array of LineString coordinate arrays.
 *
 *
 * This adheres to the RFC 7946 internet standard when serialized into JSON. When deserialized, this
 * class becomes an immutable object which should be initiated using its static factory methods.
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
 * A sample GeoJson MultiLineString's provided below (in it's serialized state).
 * ```json
 * {
 *   "type": "MultiLineString",
 *   "coordinates": [
 *     [
 *       [100.0, 0.0],
 *       [101.0, 1.0]
 *     ],
 *     [
 *       [102.0, 2.0],
 *       [103.0, 3.0]
 *     ]
 *   ]
 * }
 * ```
 * Look over the [LineString] documentation to get more information about
 * formatting your list of linestring objects correctly.
 *
 * @since 1.0.0
 */
@Deprecated(
    message = "Use new common models instead.",
    replaceWith = ReplaceWith("MultiLineString", "org.maplibre.geojson.model"),
)
class MultiLineString internal constructor(
    type: String,
    bbox: BoundingBox?,
    coordinates: List<List<Point>>
) : CommonMultiLineString(
    coordinates,
    bbox
), CoordinateContainer<List<List<Point?>?>?> {

    /**
     * This describes the TYPE of GeoJson geometry this object is, thus this will always return
     * [MultiLineString].
     *
     * @return a String which describes the TYPE of geometry, for this object it will always return
     * `MultiLineString`
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
     * Provides the list of list of [Point]s that make up the MultiLineString geometry.
     *
     * @return a list of points
     * @since 3.0.0
     */
    override fun coordinates(): List<List<Point?>> =
        coordinates.map { line -> line.map { coord -> coord.toJvm() } }

    /**
     * Returns a list of LineStrings which are currently making up this MultiLineString.
     *
     * @return a list of [LineString]s
     * @since 3.0.0
     */
    fun lineStrings(): List<LineString> = lineStrings.map { line -> line.toJvm() }

    companion object {
        private const val TYPE = "MultiLineString"

        /**
         * Create a new instance of this class by passing in a formatted valid JSON String. If you are
         * creating a MultiLineString object from scratch it is better to use one of the other provided
         * static factory methods such as [.fromLineStrings].
         *
         * @param json a formatted valid JSON string defining a GeoJson MultiLineString
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 1.0.0
         */
        fun fromJson(json: String): MultiLineString = CommonMultiLineString.fromJson(json).toJvm()

        /**
         * Create a new instance of this class by defining a list of [LineString] objects and
         * passing that list in as a parameter in this method. The LineStrings should comply with the
         * GeoJson specifications described in the documentation.
         *
         * @param lineStrings a list of LineStrings which make up this MultiLineString
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        fun fromLineStrings(lineStrings: List<LineString>): MultiLineString {
            val coordinates: MutableList<List<Point>> = ArrayList(lineStrings.size)
            for (lineString in lineStrings) {
                coordinates.add(lineString.coordinates())
            }
            return MultiLineString(TYPE, null, coordinates)
        }

        /**
         * Create a new instance of this class by defining a list of [LineString] objects and
         * passing that list in as a parameter in this method. The LineStrings should comply with the
         * GeoJson specifications described in the documentation. Optionally, pass in an instance of a
         * [BoundingBox] which better describes this MultiLineString.
         *
         * @param lineStrings a list of LineStrings which make up this MultiLineString
         * @param bbox        optionally include a bbox definition
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        fun fromLineStrings(
            lineStrings: List<LineString>,
            bbox: BoundingBox?
        ): MultiLineString {
            val coordinates: MutableList<List<Point>> = ArrayList(lineStrings.size)
            for (lineString in lineStrings) {
                coordinates.add(lineString.coordinates())
            }
            return MultiLineString(TYPE, bbox, coordinates)
        }

        /**
         * Create a new instance of this class by passing in a single [LineString] object. The
         * LineStrings should comply with the GeoJson specifications described in the documentation.
         *
         * @param lineString a single LineString which make up this MultiLineString
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        fun fromLineString(lineString: LineString): MultiLineString {
            val coordinates = Arrays.asList<List<Point>>(lineString.coordinates())
            return MultiLineString(TYPE, null, coordinates)
        }

        /**
         * Create a new instance of this class by passing in a single [LineString] object. The
         * LineStrings should comply with the GeoJson specifications described in the documentation.
         *
         * @param lineString a single LineString which make up this MultiLineString
         * @param bbox       optionally include a bbox definition
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        fun fromLineString(
            lineString: LineString,
            bbox: BoundingBox?
        ): MultiLineString {
            val coordinates = Arrays.asList<List<Point>>(lineString.coordinates())
            return MultiLineString(TYPE, bbox, coordinates)
        }

        /**
         * Create a new instance of this class by defining a list of a list of [Point]s which follow
         * the correct specifications described in the Point documentation. Note that there should not be
         * any duplicate points inside the list and the points combined should create a LineString with a
         * distance greater than 0.
         *
         * @param points a list of [Point]s which make up the MultiLineString geometry
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        fun fromLngLats(points: List<List<Point>>): MultiLineString {
            return MultiLineString(TYPE, null, points)
        }

        /**
         * Create a new instance of this class by defining a list of a list of [Point]s which follow
         * the correct specifications described in the Point documentation. Note that there should not be
         * any duplicate points inside the list and the points combined should create a LineString with a
         * distance greater than 0.
         *
         * @param points a list of [Point]s which make up the MultiLineString geometry
         * @param bbox   optionally include a bbox definition
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        fun fromLngLats(
            points: List<List<Point>>,
            bbox: BoundingBox?
        ): MultiLineString {
            return MultiLineString(TYPE, bbox, points)
        }

        fun fromLngLats(coordinates: Array<Array<DoubleArray>>): MultiLineString {
            val multiLine: MutableList<List<Point>> = ArrayList(coordinates.size)
            for (i in coordinates.indices) {
                val lineString: MutableList<Point> = ArrayList(
                    coordinates[i].size
                )
                for (j in coordinates[i].indices) {
                    lineString.add(Point.fromLngLat(coordinates[i][j])!!)
                }
                multiLine.add(lineString)
            }

            return MultiLineString(TYPE, null, multiLine)
        }
    }
}