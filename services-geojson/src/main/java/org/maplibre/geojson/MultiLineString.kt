package org.maplibre.geojson

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import org.maplibre.geojson.serializer.PointDoubleArraySerializer

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
 * <pre>
 * {
 * "type": "MultiLineString",
 * "coordinates": [
 * [
 * [100.0, 0.0],
 * [101.0, 1.0]
 * ],
 * [
 * [102.0, 2.0],
 * [103.0, 3.0]
 * ]
 * ]
 * }
</pre> *
 * Look over the [LineString] documentation to get more information about
 * formatting your list of linestring objects correctly.
 *
 * @since 1.0.0
 */
@Serializable
@SerialName("MultiLineString")
data class MultiLineString
@JvmOverloads
constructor(
    override val coordinates: List<List<@Serializable(with = PointDoubleArraySerializer::class) Point>>,
    override val bbox: BoundingBox? = null,
//    override val type: String = TYPE
) : CoordinateContainer<List<List<Point>>> {

    /**
     * Returns a list of LineStrings which are currently making up this MultiLineString.
     *
     * @return a list of [LineString]s
     * @since 3.0.0
     */
    val lineStrings: List<LineString>
        get() = coordinates.map { points -> LineString(points) }

    override fun toJson() = json.encodeToString(this)

    companion object {
        const val TYPE = "MultiLineString"

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
        @JvmOverloads
        fun fromLineStrings(
            lineStrings: List<LineString>,
            bbox: BoundingBox? = null
        ) = MultiLineString(lineStrings.map { lineString -> lineString.coordinates }, bbox)

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
        @JvmOverloads
        @JvmName("fromLineString")
        fun fromLineString(
            lineString: LineString,
            bbox: BoundingBox? = null
        ) = MultiLineString(listOf(lineString.coordinates), bbox)

        @JvmStatic
        fun fromJson(jsonString: String): MultiLineString = json.decodeFromString(jsonString)
    }
}
