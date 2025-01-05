package org.maplibre.geojson

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import org.maplibre.geojson.serializer.PointDoubleArraySerializer
import org.maplibre.geojson.utils.json
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

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
 * @param coordinates a list of {@link Point}s which make up the MultiPolygon geometry
 * @param bbox   optionally include a bbox definition
 * @since 1.0.0
 */
@Serializable
@SerialName("MultiPolygon")
data class MultiPolygon
@JvmOverloads
constructor(
    override val coordinates: List<List<List<@Serializable(with = PointDoubleArraySerializer::class) Point>>>,
    override val bbox: BoundingBox? = null,
) : CoordinateContainer<List<List<List<Point>>>> {

    /**
     * Returns a list of polygons which make up this MultiPolygon instance.
     *
     * @return a list of {@link Polygon}s which make up this MultiPolygon instance
     * @since 3.0.0
     */
    val polygons: List<Polygon>
        get() = coordinates.map { points -> Polygon(points) }

    /**
     * This takes the currently defined values found inside this instance and converts it to a GeoJson
     * string.
     *
     * @return a JSON string which represents this MultiPolygon geometry
     * @since 1.0.0
     */
    override fun toJson() = json.encodeToString(this)

    companion object {

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
        @JvmStatic
        fun fromPolygon(
            polygon: Polygon,
            bbox: BoundingBox? = null,
        ) = MultiPolygon(listOf(polygon.coordinates), bbox)

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
        @JvmStatic
        fun fromPolygons(
            polygons: List<Polygon>,
            bbox: BoundingBox? = null,
        ) = MultiPolygon(polygons.map { polygon -> polygon.coordinates }, bbox)

        /**
         * Create a new instance of this class by passing in a formatted valid JSON String. If you are
         * creating a MultiPolygon object from scratch it is better to use the constructor.
         *
         * @param jsonString a formatted valid JSON string defining a GeoJson MultiPolygon
         * @return  a new instance of this class defined by the values in the JSON string
         * @since 1.0.0
         */
        @JvmStatic
        fun fromJson(jsonString: String): MultiPolygon = json.decodeFromString(jsonString)
    }
}