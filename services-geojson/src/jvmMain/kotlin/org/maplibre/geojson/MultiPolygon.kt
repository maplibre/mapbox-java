package org.maplibre.geojson

import org.maplibre.geojson.common.toJvm
import java.util.Arrays
import org.maplibre.geojson.model.MultiPolygon as CommonMultiPolygon

/**
 * A multiPolygon is an array of Polygon coordinate arrays.
 *
 *
 * This adheres to the RFC 7946 internet standard when serialized into JSON. When deserialized, this
 * class becomes an immutable object which should be initiated using its static factory methods.
 *
 *
 * When representing a Polygon that crosses the antimeridian, interoperability is improved by
 * modifying their geometry. Any geometry that crosses the antimeridian SHOULD be represented by
 * cutting it in two such that neither part's representation crosses the antimeridian.
 *
 *
 * For example, a line extending from 45 degrees N, 170 degrees E across the antimeridian to 45
 * degrees N, 170 degrees W should be cut in two and represented as a MultiLineString.
 *
 *
 * A sample GeoJson MultiPolygon's provided below (in it's serialized state).
 * ```json
 * {
 *   "type": "MultiPolygon",
 *   "coordinates": [
 *     [
 *       [
 *         [102.0, 2.0],
 *         [103.0, 2.0],
 *         [103.0, 3.0],
 *         [102.0, 3.0],
 *         [102.0, 2.0]
 *       ]
 *     ],
 *     [
 *       [
 *         [100.0, 0.0],
 *         [101.0, 0.0],
 *         [101.0, 1.0],
 *         [100.0, 1.0],
 *         [100.0, 0.0]
 *       ],
 *       [
 *         [100.2, 0.2],
 *         [100.2, 0.8],
 *         [100.8, 0.8],
 *         [100.8, 0.2],
 *         [100.2, 0.2]
 *       ]
 *     ]
 *   ]
 * }
 * ```
 *
 * Look over the [Polygon] documentation to get more information about
 * formatting your list of Polygon objects correctly.
 *
 * @since 1.0.0
 */
@Deprecated(
    message = "Use new common models instead.",
    replaceWith = ReplaceWith("MultiPolygon", "org.maplibre.geojson.model"),
)
class MultiPolygon
internal constructor(
    type: String,
    bbox: BoundingBox?,
    coordinates: List<List<List<Point>>>
) : CommonMultiPolygon(
    coordinates,
    bbox,
), CoordinateContainer<List<List<List<Point?>?>?>?> {

    /**
     * Returns a list of polygons which make up this MultiPolygon instance.
     *
     * @return a list of [Polygon]s which make up this MultiPolygon instance
     * @since 3.0.0
     */
    fun polygons(): List<Polygon> = polygons.map { poly -> poly.toJvm() }

    /**
     * This describes the TYPE of GeoJson geometry this object is, thus this will always return
     * [MultiPolygon].
     *
     * @return a String which describes the TYPE of geometry, for this object it will always return
     * `MultiPolygon`
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
     * Provides the list of list of list of [Point]s that make up the MultiPolygon geometry.
     *
     * @return a list of points
     * @since 3.0.0
     */
    override fun coordinates(): List<List<List<Point>>> =
        coordinates.map { polygon -> polygon.map { ring -> ring.map { pt -> pt.toJvm() } } }


    companion object {
        private const val TYPE = "MultiPolygon"

        /**
         * Create a new instance of this class by passing in a formatted valid JSON String. If you are
         * creating a MultiPolygon object from scratch it is better to use one of the other provided
         * static factory methods such as [.fromPolygons].
         *
         * @param json a formatted valid JSON string defining a GeoJson MultiPolygon
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 1.0.0
         */
        fun fromJson(json: String): MultiPolygon = CommonMultiPolygon.fromJson(json).toJvm()

        /**
         * Create a new instance of this class by defining a list of [Polygon] objects and passing
         * that list in as a parameter in this method. The Polygons should comply with the GeoJson
         * specifications described in the documentation.
         *
         * @param polygons a list of Polygons which make up this MultiPolygon
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        fun fromPolygons(polygons: List<Polygon>): MultiPolygon {
            val coordinates: MutableList<List<List<Point>>> = ArrayList(polygons.size)
            for (polygon in polygons) {
                coordinates.add(polygon.coordinates())
            }
            return MultiPolygon(TYPE, null, coordinates)
        }

        /**
         * Create a new instance of this class by defining a list of [Polygon] objects and passing
         * that list in as a parameter in this method. The Polygons should comply with the GeoJson
         * specifications described in the documentation. Optionally, pass in an instance of a
         * [BoundingBox] which better describes this MultiPolygon.
         *
         * @param polygons a list of Polygons which make up this MultiPolygon
         * @param bbox     optionally include a bbox definition
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        fun fromPolygons(
            polygons: List<Polygon>,
            bbox: BoundingBox?
        ): MultiPolygon {
            val coordinates: MutableList<List<List<Point>>> = ArrayList(polygons.size)
            for (polygon in polygons) {
                coordinates.add(polygon.coordinates())
            }
            return MultiPolygon(TYPE, bbox, coordinates)
        }

        /**
         * Create a new instance of this class by defining a single [Polygon] objects and passing
         * it in as a parameter in this method. The Polygon should comply with the GeoJson
         * specifications described in the documentation.
         *
         * @param polygon a single Polygon which make up this MultiPolygon
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        fun fromPolygon(polygon: Polygon): MultiPolygon {
            val coordinates = Arrays.asList<List<List<Point>>>(polygon.coordinates())
            return MultiPolygon(TYPE, null, coordinates)
        }

        /**
         * Create a new instance of this class by defining a single [Polygon] objects and passing
         * it in as a parameter in this method. The Polygon should comply with the GeoJson
         * specifications described in the documentation.
         *
         * @param polygon a single Polygon which make up this MultiPolygon
         * @param bbox    optionally include a bbox definition
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        fun fromPolygon(polygon: Polygon, bbox: BoundingBox?): MultiPolygon {
            val coordinates = Arrays.asList<List<List<Point>>>(polygon.coordinates())
            return MultiPolygon(TYPE, bbox, coordinates)
        }

        /**
         * Create a new instance of this class by defining a list of a list of a list of [Point]s
         * which follow the correct specifications described in the Point documentation.
         *
         * @param points a list of [Point]s which make up the MultiPolygon geometry
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        fun fromLngLats(points: List<List<List<Point>>>): MultiPolygon {
            return MultiPolygon(TYPE, null, points)
        }

        /**
         * Create a new instance of this class by defining a list of a list of a list of [Point]s
         * which follow the correct specifications described in the Point documentation.
         *
         * @param points a list of [Point]s which make up the MultiPolygon geometry
         * @param bbox   optionally include a bbox definition
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        fun fromLngLats(
            points: List<List<List<Point>>>,
            bbox: BoundingBox?
        ): MultiPolygon {
            return MultiPolygon(TYPE, bbox, points)
        }

        fun fromLngLats(coordinates: Array<Array<Array<DoubleArray>>>): MultiPolygon {
            val converted: MutableList<List<List<Point>>> = ArrayList(coordinates.size)
            for (i in coordinates.indices) {
                val innerOneList: MutableList<List<Point>> = ArrayList(
                    coordinates[i].size
                )
                for (j in coordinates[i].indices) {
                    val innerTwoList: MutableList<Point> = ArrayList(
                        coordinates[i][j].size
                    )
                    for (k in coordinates[i][j].indices) {
                        innerTwoList.add(Point.fromLngLat(coordinates[i][j][k])!!)
                    }
                    innerOneList.add(innerTwoList)
                }
                converted.add(innerOneList)
            }

            return MultiPolygon(TYPE, null, converted)
        }
    }
}