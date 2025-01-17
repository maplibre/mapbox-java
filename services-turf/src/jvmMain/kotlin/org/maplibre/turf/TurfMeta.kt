package org.maplibre.turf

import org.maplibre.geojson.turf.TurfException
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.LineString
import org.maplibre.geojson.MultiLineString
import org.maplibre.geojson.MultiPoint
import org.maplibre.geojson.MultiPolygon
import org.maplibre.geojson.Point
import org.maplibre.geojson.Polygon
import org.maplibre.geojson.common.toJvm
import org.maplibre.geojson.turf.TurfMeta as CommonTurfMeta

/**
 * Class contains methods that are useful for getting all coordinates from a specific GeoJson
 * geometry.
 *
 * @see [Turf documentation](http://turfjs.org/docs/)
 *
 * @since 2.0.0
 */
@Deprecated(
    message = "Use new common Turf utils instead.",
    replaceWith = ReplaceWith("TurfMeta", "org.maplibre.geojson.turf.TurfMeta"),
)
object TurfMeta {
    /**
     * Get all coordinates from a [Point] object, returning a `List` of Point objects.
     * If you have a geometry collection, you need to break it down to individual geometry objects
     * before using [.coordAll].
     *
     * @param point any [Point] object
     * @return a `List` made up of [Point]s
     * @since 2.0.0
     */
    @JvmStatic
    fun coordAll(point: Point): List<Point> {
        return CommonTurfMeta.coordAll(point).map { pt -> pt.toJvm() }
    }

    /**
     * Get all coordinates from a [MultiPoint] object, returning a `List` of Point
     * objects. If you have a geometry collection, you need to break it down to individual geometry
     * objects before using [.coordAll].
     *
     * @param multiPoint any [MultiPoint] object
     * @return a `List` made up of [Point]s
     * @since 2.0.0
     */
    fun coordAll(multiPoint: MultiPoint): List<Point> {
        return CommonTurfMeta.coordAll(multiPoint).map { pt -> pt.toJvm() }
    }

    /**
     * Get all coordinates from a [LineString] object, returning a `List` of Point
     * objects. If you have a geometry collection, you need to break it down to individual geometry
     * objects before using [.coordAll].
     *
     * @param lineString any [LineString] object
     * @return a `List` made up of [Point]s
     * @since 2.0.0
     */
    @JvmStatic
    fun coordAll(lineString: LineString): List<Point> {
        return CommonTurfMeta.coordAll(lineString).map { pt -> pt.toJvm() }
    }

    /**
     * Get all coordinates from a [Polygon] object, returning a `List` of Point objects.
     * If you have a geometry collection, you need to break it down to individual geometry objects
     * before using [.coordAll].
     *
     * @param polygon          any [Polygon] object
     * @param excludeWrapCoord whether or not to include the final coordinate of LinearRings that
     * wraps the ring in its iteration
     * @return a `List` made up of [Point]s
     * @since 2.0.0
     */
    @JvmStatic
    fun coordAll(polygon: Polygon, excludeWrapCoord: Boolean): List<Point> {
        return CommonTurfMeta.coordAll(polygon, excludeWrapCoord).map { pt -> pt.toJvm() }
    }

    /**
     * Get all coordinates from a [MultiLineString] object, returning
     * a `List` of Point objects. If you have a geometry collection, you
     * need to break it down to individual geometry objects before using
     * [.coordAll].
     *
     * @param multiLineString any [MultiLineString] object
     * @return a `List` made up of [Point]s
     * @since 2.0.0
     */
    fun coordAll(multiLineString: MultiLineString): List<Point> {
        return CommonTurfMeta.coordAll(multiLineString).map { pt -> pt.toJvm() }
    }

    /**
     * Get all coordinates from a [MultiPolygon] object, returning a `List` of Point
     * objects. If you have a geometry collection, you need to break it down to individual geometry
     * objects before using [.coordAll].
     *
     * @param multiPolygon     any [MultiPolygon] object
     * @param excludeWrapCoord whether or not to include the final coordinate of LinearRings that
     * wraps the ring in its iteration. Used to handle [Polygon] and
     * [MultiPolygon] geometries.
     * @return a `List` made up of [Point]s
     * @since 2.0.0
     */
    @JvmStatic
    fun coordAll(
        multiPolygon: MultiPolygon,
        excludeWrapCoord: Boolean
    ): List<Point> {
        return CommonTurfMeta.coordAll(multiPolygon, excludeWrapCoord).map { pt -> pt.toJvm() }
    }

    /**
     * Get all coordinates from a [Feature] object, returning a `List` of [Point]
     * objects.
     *
     * @param feature          the [Feature] that you'd like to extract the Points from.
     * @param excludeWrapCoord whether or not to include the final coordinate of LinearRings that
     * wraps the ring in its iteration. Used if the [Feature]
     * passed through the method is a [Polygon] or [MultiPolygon]
     * geometry.
     * @return a `List` made up of [Point]s
     * @since 4.8.0
     */
    @JvmStatic
    fun coordAll(
        feature: Feature,
        excludeWrapCoord: Boolean
    ): List<Point> {
        return CommonTurfMeta.coordAll(feature, excludeWrapCoord).map { pt -> pt.toJvm() }
    }

    /**
     * Get all coordinates from a [FeatureCollection] object, returning a
     * `List` of [Point] objects.
     *
     * @param featureCollection the [FeatureCollection] that you'd like
     * to extract the Points from.
     * @param excludeWrapCoord  whether or not to include the final coordinate of LinearRings that
     * wraps the ring in its iteration. Used if a [Feature] in the
     * [FeatureCollection] that's passed through this method, is a
     * [Polygon] or [MultiPolygon] geometry.
     * @return a `List` made up of [Point]s
     * @since 4.8.0
     */
    @JvmStatic
    fun coordAll(
        featureCollection: FeatureCollection,
        excludeWrapCoord: Boolean
    ): List<Point> {
        return CommonTurfMeta.coordAll(featureCollection, excludeWrapCoord).map { pt -> pt.toJvm() }
    }

    /**
     * Unwrap a coordinate [Point] from a [Feature] with a [Point] geometry.
     *
     * @param obj any value
     * @return a coordinate
     * @see [Turf getCoord documentation](http://turfjs.org/docs/.getcoord)
     *
     * @since 3.2.0
     */
    @JvmStatic
    fun getCoord(obj: Feature): Point {
        if (obj.geometry() is Point) {
            return obj.geometry() as Point
        }
        throw TurfException("A Feature with a Point geometry is required.")
    }
}