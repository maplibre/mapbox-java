package org.maplibre.turf

import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.MultiPolygon
import org.maplibre.geojson.Point
import org.maplibre.geojson.Polygon
import org.maplibre.geojson.common.toJvm
import org.maplibre.geojson.turf.TurfJoins as CommonTurfJoins

/**
 * Class contains methods that can determine if points lie within a polygon or not.
 *
 * @see [Turf documentation](http://turfjs.org/docs/)
 *
 * @since 1.3.0
 */
@Deprecated(
    message = "Use new common Turf utils instead.",
    replaceWith = ReplaceWith("TurfJoins", "org.maplibre.geojson.turf.TurfJoins"),
)
object TurfJoins {

    /**
     * Takes a [Point] and a [Polygon] and determines if the point resides inside the
     * polygon. The polygon can be convex or concave. The function accounts for holes.
     *
     * @param point   which you'd like to check if inside the polygon
     * @param polygon which you'd like to check if the points inside
     * @return true if the Point is inside the Polygon; false if the Point is not inside the Polygon
     * @see [Turf Inside documentation](http://turfjs.org/docs/.inside)
     *
     * @since 1.3.0
     */
    @JvmStatic
    fun inside(point: Point, polygon: Polygon): Boolean {
        return CommonTurfJoins.inside(point, polygon)
    }

    /**
     * Takes a [Point] and a [MultiPolygon] and determines if the point resides inside
     * the polygon. The polygon can be convex or concave. The function accounts for holes.
     *
     * @param point        which you'd like to check if inside the polygon
     * @param multiPolygon which you'd like to check if the points inside
     * @return true if the Point is inside the MultiPolygon; false if the Point is not inside the
     * MultiPolygon
     * @see [Turf Inside documentation](http://turfjs.org/docs/.inside)
     *
     * @since 1.3.0
     */
    @JvmStatic
    fun inside(point: Point, multiPolygon: MultiPolygon): Boolean {
        return CommonTurfJoins.inside(point, multiPolygon)
    }

    /**
     * Takes a [FeatureCollection] of [Point] and a [FeatureCollection] of
     * [Polygon] and returns the points that fall within the polygons.
     *
     * @param points   input points.
     * @param polygons input polygons.
     * @return points that land within at least one polygon.
     * @since 1.3.0
     */
    @JvmStatic
    fun pointsWithinPolygon(
        points: FeatureCollection,
        polygons: FeatureCollection
    ): FeatureCollection {
        return CommonTurfJoins.pointsWithinPolygon(points, polygons).toJvm()
    }
}