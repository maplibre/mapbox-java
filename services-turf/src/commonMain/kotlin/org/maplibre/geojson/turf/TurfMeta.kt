package org.maplibre.geojson.turf

import org.maplibre.geojson.model.Feature
import org.maplibre.geojson.model.FeatureCollection
import org.maplibre.geojson.model.Geometry
import org.maplibre.geojson.model.GeometryCollection
import org.maplibre.geojson.model.LineString
import org.maplibre.geojson.model.MultiLineString
import org.maplibre.geojson.model.MultiPoint
import org.maplibre.geojson.model.MultiPolygon
import org.maplibre.geojson.model.Point
import org.maplibre.geojson.model.Polygon
import kotlin.jvm.JvmStatic

/**
 * Class contains methods that are useful for getting all coordinates from a specific GeoJson
 * geometry.
 *
 * @see [Turf documentation](http://turfjs.org/docs/)
 *
 * @since 2.0.0
 */
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
        return listOf(point)
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
    @JvmStatic
    fun coordAll(multiPoint: MultiPoint): List<Point> {
        return multiPoint.coordinates
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
        return lineString.coordinates
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
        return polygon.coordinates
            .map { coords -> coords.dropLast(if (excludeWrapCoord) 1 else 0) }
            .flatten()
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
    @JvmStatic
    fun coordAll(multiLineString: MultiLineString): List<Point> {
        return multiLineString.coordinates.flatten()
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
        return multiPolygon.coordinates
            .flatten()
            .map { coords -> coords.dropLast(if (excludeWrapCoord) 1 else 0) }
            .flatten()
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
        return feature.geometry?.let { geometry ->
            coordAllFromSingleGeometry(geometry, excludeWrapCoord)
        } ?: emptyList()
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
        return featureCollection.features
            .mapNotNull { feature ->
                feature.geometry?.let { geometry ->
                    coordAllFromSingleGeometry(geometry, excludeWrapCoord)
                }
            }
            .flatten()
    }

    /**
     * Get all coordinates from a [FeatureCollection] object, returning a
     * `List` of [Point] objects.
     *
     * @param geometry         the [Geometry] object to extract the [Point]s from
     * @param excludeWrapCoord whether or not to include the final coordinate of LinearRings that
     * wraps the ring in its iteration. Used if the [Feature]
     * passed through the method is a [Polygon] or [MultiPolygon]
     * geometry.
     * @return a `List` made up of [Point]s
     * @since 4.8.0
     */
    private fun coordAllFromSingleGeometry(
        geometry: Geometry,
        excludeWrapCoord: Boolean
    ): List<Point> {
        return when (geometry) {
            is Point ->
                listOf(geometry)

            is MultiPoint ->
                geometry.coordinates

            is LineString ->
                geometry.coordinates

            is MultiLineString ->
                coordAll(geometry)

            is Polygon ->
                coordAll(geometry, excludeWrapCoord)

            is MultiPolygon ->
                coordAll(geometry, excludeWrapCoord)

            is GeometryCollection -> {
                geometry.geometries
                    .map { geometryItem ->
                        coordAllFromSingleGeometry(geometryItem, excludeWrapCoord)
                    }
                    .flatten()
            }

            else ->
                throw TurfException("Unsupported geometry type: $geometry")
        }
    }

    /**
     * Unwrap a coordinate [Point] from a [Feature] with a [Point] geometry.
     * If the feature does not have a [Point] geometry, `TurfException` will be thrown.
     *
     * @param feature any [Feature] instance
     * @return a coordinate
     * @see [Turf getCoord documentation](http://turfjs.org/docs/.getcoord)
     *
     * @since 3.2.0
     */
    @JvmStatic
    fun getCoord(feature: Feature): Point {
        return feature.geometry as? Point
            ?: throw TurfException("A Feature with a Point geometry is required.")
    }
}
