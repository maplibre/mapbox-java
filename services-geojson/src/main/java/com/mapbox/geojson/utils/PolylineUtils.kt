package com.mapbox.geojson.utils

import com.mapbox.geojson.Point
import com.mapbox.geojson.Point.Companion.fromLngLat

/**
 * Polyline utils class contains method that can decode/encode a polyline, simplify a line, and
 * more.
 *
 * @since 1.0.0
 */
object PolylineUtils {
    // 1 by default (in the same metric as the point coordinates)
    private const val SIMPLIFY_DEFAULT_TOLERANCE = 1.0

    // False by default (excludes distance-based preprocessing step which leads to highest quality
    // simplification but runs slower)
    private const val SIMPLIFY_DEFAULT_HIGHEST_QUALITY = false

    /**
     * Decodes an encoded path string into a sequence of [Point].
     *
     * @param encodedPath a String representing an encoded path string
     * @param precision   OSRMv4 uses 6, OSRMv5 and Google uses 5
     * @return list of [Point] making up the line
     * @see [Part of algorithm came from this source](https://github.com/mapbox/polyline/blob/master/src/polyline.js)
     *
     * @see [Part of algorithm came from this source.](https://github.com/googlemaps/android-maps-utils/blob/master/library/src/com/google/maps/android/PolyUtil.java)
     *
     * @since 1.0.0
     */
    @JvmStatic
    fun decode(encodedPath: String, precision: Int): List<Point> {
        val len = encodedPath.length

        // OSRM uses precision=6, the default Polyline spec divides by 1E5, capping at precision=5
        val factor = Math.pow(10.0, precision.toDouble())

        // For speed we preallocate to an upper bound on the final length, then
        // truncate the array before returning.
        val path: MutableList<Point> = ArrayList()
        var index = 0
        var lat = 0
        var lng = 0
        while (index < len) {
            var result = 1
            var shift = 0
            var temp: Int
            do {
                temp = encodedPath[index++].code - 63 - 1
                result += temp shl shift
                shift += 5
            } while (temp >= 0x1f)
            lat += if (result and 1 != 0) (result shr 1).inv() else result shr 1
            result = 1
            shift = 0
            do {
                temp = encodedPath[index++].code - 63 - 1
                result += temp shl shift
                shift += 5
            } while (temp >= 0x1f)
            lng += if (result and 1 != 0) (result shr 1).inv() else result shr 1
            path.add(fromLngLat(lng / factor, lat / factor))
        }
        return path
    }

    /**
     * Encodes a sequence of Points into an encoded path string.
     *
     * @param path      list of [Point]s making up the line
     * @param precision OSRMv4 uses 6, OSRMv5 and Google uses 5
     * @return a String representing a path string
     * @since 1.0.0
     */
    fun encode(path: List<Point>, precision: Int): String {
        var lastLat: Long = 0
        var lastLng: Long = 0
        val result = StringBuilder()

        // OSRM uses precision=6, the default Polyline spec divides by 1E5, capping at precision=5
        val factor = Math.pow(10.0, precision.toDouble())
        for (point in path) {
            val lat = Math.round(point.latitude() * factor)
            val lng = Math.round(point.longitude() * factor)
            val varLat = lat - lastLat
            val varLng = lng - lastLng
            encode(varLat, result)
            encode(varLng, result)
            lastLat = lat
            lastLng = lng
        }
        return result.toString()
    }

    private fun encode(param: Long, result: StringBuilder) {
        var variable = if (param < 0) (param shl 1).inv() else param shl 1
        while (variable >= 0x20) {
            result.append(Character.toChars((0x20 or (variable and 0x1f).toInt()) + 63))
            variable = variable shr 5
        }
        result.append(Character.toChars((variable + 63).toInt()))
    }

    /**
     * Reduces the number of points in a polyline while retaining its shape, giving a performance
     * boost when processing it and also reducing visual noise.
     *
     * @param points         an array of points
     * @param highestQuality excludes distance-based preprocessing step which leads to highest quality
     * simplification
     * @return an array of simplified points
     * @see [JavaScript implementation](http://mourner.github.io/simplify-js/)
     *
     * @since 1.2.0
     */
    fun simplify(points: List<Point>, highestQuality: Boolean): List<Point> {
        return simplify(points, SIMPLIFY_DEFAULT_TOLERANCE, highestQuality)
    }
    /**
     * Reduces the number of points in a polyline while retaining its shape, giving a performance
     * boost when processing it and also reducing visual noise.
     *
     * @param points         an array of points
     * @param tolerance      affects the amount of simplification (in the same metric as the point
     * coordinates)
     * @param highestQuality excludes distance-based preprocessing step which leads to highest quality
     * simplification
     * @return an array of simplified points
     * @see [JavaScript implementation](http://mourner.github.io/simplify-js/)
     *
     * @since 1.2.0
     */
    /*
   * Polyline simplification method. It's a direct port of simplify.js to Java.
   * See: https://github.com/mourner/simplify-js/blob/master/simplify.js
   */
    /**
     * Reduces the number of points in a polyline while retaining its shape, giving a performance
     * boost when processing it and also reducing visual noise.
     *
     * @param points an array of points
     * @return an array of simplified points
     * @see [JavaScript implementation](http://mourner.github.io/simplify-js/)
     *
     * @since 1.2.0
     */
    /**
     * Reduces the number of points in a polyline while retaining its shape, giving a performance
     * boost when processing it and also reducing visual noise.
     *
     * @param points    an array of points
     * @param tolerance affects the amount of simplification (in the same metric as the point
     * coordinates)
     * @return an array of simplified points
     * @see [JavaScript implementation](http://mourner.github.io/simplify-js/)
     *
     * @since 1.2.0
     */
    @JvmStatic
    @JvmOverloads
    fun simplify(
        points: List<Point>, tolerance: Double = SIMPLIFY_DEFAULT_TOLERANCE,
        highestQuality: Boolean = SIMPLIFY_DEFAULT_HIGHEST_QUALITY
    ): List<Point> {
        var points = points
        if (points.size <= 2) {
            return points
        }
        val sqTolerance = tolerance * tolerance
        points = if (highestQuality) points else simplifyRadialDist(points, sqTolerance) as List<Point>
        points = simplifyDouglasPeucker(points, sqTolerance)
        return points
    }

    /**
     * Square distance between 2 points.
     *
     * @param p1 first [Point]
     * @param p2 second Point
     * @return square of the distance between two input points
     */
    private fun getSqDist(p1: Point?, p2: Point?): Double {
        val dx = p1!!.longitude() - p2!!.longitude()
        val dy = p1.latitude() - p2.latitude()
        return dx * dx + dy * dy
    }

    /**
     * Square distance from a point to a segment.
     *
     * @param point [Point] whose distance from segment needs to be determined
     * @param p1,p2 points defining the segment
     * @return square of the distance between first input point and segment defined by
     * other two input points
     */
    private fun getSqSegDist(point: Point, p1: Point, p2: Point): Double {
        var horizontal = p1.longitude()
        var vertical = p1.latitude()
        var diffHorizontal = p2.longitude() - horizontal
        var diffVertical = p2.latitude() - vertical
        if (diffHorizontal != 0.0 || diffVertical != 0.0) {
            val total = ((point.longitude() - horizontal) * diffHorizontal + (point.latitude()
                    - vertical) * diffVertical) / (diffHorizontal * diffHorizontal + diffVertical
                    * diffVertical)
            if (total > 1) {
                horizontal = p2.longitude()
                vertical = p2.latitude()
            } else if (total > 0) {
                horizontal += diffHorizontal * total
                vertical += diffVertical * total
            }
        }
        diffHorizontal = point.longitude() - horizontal
        diffVertical = point.latitude() - vertical
        return diffHorizontal * diffHorizontal + diffVertical * diffVertical
    }

    /**
     * Basic distance-based simplification.
     *
     * @param points a list of points to be simplified
     * @param sqTolerance square of amount of simplification
     * @return a list of simplified points
     */
    private fun simplifyRadialDist(points: List<Point>, sqTolerance: Double): List<Point> {
        var prevPoint = points[0]
        val newPoints = ArrayList<Point>()
        newPoints.add(prevPoint)
        var lastPoint: Point? = null
        var i = 1
        val len = points.size
        while (i < len) {
            val point = points[i]
            if (getSqDist(point, prevPoint) > sqTolerance) {
                newPoints.add(point)
                prevPoint = point
            }
            lastPoint = point
            i++
        }
        if (lastPoint != null && !prevPoint.equals(lastPoint)) {
            newPoints.add(lastPoint)
        }
        
        return newPoints
    }

    private fun simplifyDpStep(
        points: List<Point>, first: Int, last: Int, sqTolerance: Double, simplified: List<Point>
    ): List<Point> {
        var maxSqDist = sqTolerance
        var index = 0
        val stepList = ArrayList<Point>()
        for (i in first + 1 until last) {
            val sqDist = getSqSegDist(points[i], points[first], points[last])
            if (sqDist > maxSqDist) {
                index = i
                maxSqDist = sqDist
            }
        }
        if (maxSqDist > sqTolerance) {
            if (index - first > 1) {
                stepList.addAll(simplifyDpStep(points, first, index, sqTolerance, simplified))
            }
            stepList.add(points[index])
            if (last - index > 1) {
                stepList.addAll(simplifyDpStep(points, index, last, sqTolerance, simplified))
            }
        }
        return stepList
    }

    /**
     * Simplification using Ramer-Douglas-Peucker algorithm.
     *
     * @param points a list of points to be simplified
     * @param sqTolerance square of amount of simplification
     * @return a list of simplified points
     */
    private fun simplifyDouglasPeucker(points: List<Point>, sqTolerance: Double): List<Point> {
        val last = points.size - 1
        val simplified = ArrayList<Point>()
        simplified.add(points[0])
        simplified.addAll(simplifyDpStep(points, 0, last, sqTolerance, simplified))
        simplified.add(points[last])
        return simplified
    }
}