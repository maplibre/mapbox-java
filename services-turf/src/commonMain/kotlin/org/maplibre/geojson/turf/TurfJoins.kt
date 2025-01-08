package org.maplibre.geojson.turf

import org.maplibre.geojson.model.Feature
import org.maplibre.geojson.model.FeatureCollection
import org.maplibre.geojson.model.MultiPolygon
import org.maplibre.geojson.model.Point
import org.maplibre.geojson.model.Polygon
import kotlin.jvm.JvmStatic

/**
 * Class contains methods that can determine if points lie within a polygon or not.
 *
 * @see [Turf documentation](http://turfjs.org/docs/)
 *
 * @since 1.3.0
 */
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
        val coordinates = polygon.coordinates
        return inside(point, MultiPolygon(listOf(coordinates)))
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
        return multiPolygon.coordinates.any { poly ->
            // check if it is in the outer ring first
            if (inRing(point, poly.first())) {
                // check for the point in any of the holes
                poly.drop(1).none { ring ->
                    inRing(point, ring)
                }
            } else {
                false
            }
        }
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
        val inPolygonFeatures = polygons.features.flatMap { polygonFeature ->
            val polygon = polygonFeature.geometry as Polygon
            points.features.mapNotNull { pointFeature ->
                val point = pointFeature.geometry as Point
                if (inside(point, polygon)) {
                    Feature(point)
                } else {
                    null
                }
            }
        }

        return FeatureCollection(inPolygonFeatures)
    }

    // pt is [x,y] and ring is [[x,y], [x,y],..]
    private fun inRing(pt: Point, ring: List<Point>): Boolean {
        var isInside = false

        var i = 0
        var j = ring.size - 1
        while (i < ring.size) {
            val xi = ring[i].longitude
            val yi = ring[i].latitude
            val xj = ring[j].longitude
            val yj = ring[j].latitude
            val intersect = ((yi > pt.latitude) != (yj > pt.latitude))
                    && (pt.longitude < (xj - xi) * (pt.latitude - yi) / (yj - yi) + xi)
            if (intersect) {
                isInside = !isInside
            }
            j = i++
        }
        return isInside
    }
}
