package org.maplibre.turf

import kotlinx.serialization.json.JsonPrimitive
import org.maplibre.geojson.Feature
import org.maplibre.geojson.LineString
import org.maplibre.geojson.Point
import org.maplibre.turf.TurfConstants.TurfUnitCriteria
import org.maplibre.turf.TurfMeasurement.bearing
import org.maplibre.turf.TurfMeasurement.destination
import org.maplibre.turf.TurfMeasurement.distance
import kotlin.math.max

/**
 * Class contains all the miscellaneous methods that Turf can perform.
 *
 * @see [Turf documentation](http://turfjs.org/docs/)
 *
 * @since 1.2.0
 */
object TurfMisc {

    private const val INDEX_KEY = "index"
    private const val DISTANCE_KEY = "dist"

    /**
     * Takes a line, a start [Point], and a stop point and returns the line in between those
     * points.
     *
     * @param startPt Starting point.
     * @param stopPt  Stopping point.
     * @param line    Line to slice.
     * @return Sliced line.
     * @throws TurfException signals that a Turf exception of some sort has occurred.
     * @see [Turf Line slice documentation](http://turfjs.org/docs/.lineslice)
     *
     * @since 1.2.0
     */
    @JvmStatic
    fun lineSlice(startPt: Point, stopPt: Point, line: Feature): LineString {
        return (line.geometry as? LineString)?.let { lineString ->
            lineSlice(startPt, stopPt, lineString)
        } ?: throw TurfException("line must be a LineString")
    }

    /**
     * Takes a line, a start [Point], and a stop point and returns the line in between those
     * points.
     *
     * @param startPt used for calculating the lineSlice
     * @param stopPt  used for calculating the lineSlice
     * @param line    geometry that should be sliced
     * @return a sliced [LineString]
     * @see [Turf Line slice documentation](http://turfjs.org/docs/.lineslice)
     *
     * @since 1.2.0
     */
    @JvmStatic
    fun lineSlice(startPt: Point, stopPt: Point, line: LineString): LineString {
        val coordinates = line.coordinates

        if (coordinates.size < 2) {
            throw TurfException("Turf lineSlice requires a LineString made up of at least 2 coordinates.")
        } else if (startPt == stopPt) {
            throw TurfException("Start and stop points in Turf lineSlice cannot equal each other.")
        }

        val ends = listOf(
            nearestPointOnLine(startPt, coordinates),
            nearestPointOnLine(stopPt, coordinates)
        )
            .sortedBy { point -> point.getIntProperty(INDEX_KEY) }
            .map { feature -> feature.geometry as Point }

        val points = listOf(ends.first()) +
                coordinates.subList(1, coordinates.size - 1) +
                listOf(ends.last())

        return LineString(points)
    }

    /**
     * Takes a [LineString], a specified distance along the line to a start [Point],
     * and a specified distance along the line to a stop point
     * and returns a subsection of the line in-between those points.
     *
     *
     * This can be useful for extracting only the part of a route between two distances.
     *
     * @param line input line
     * @param startDist distance along the line to starting point
     * @param stopDist distance along the line to ending point
     * @param units one of the units found inside [TurfConstants.TurfUnitCriteria]
     * can be degrees, radians, miles, or kilometers
     * @return sliced line
     * @throws TurfException signals that a Turf exception of some sort has occurred.
     * @see [Turf Line slice documentation](http://turfjs.org/docs/.lineslicealong)
     *
     * @since 3.1.0
     */
    @JvmStatic
    fun lineSliceAlong(
        line: Feature,
        startDist: Double,
        stopDist: Double,
        @TurfUnitCriteria units: String
    ): LineString {
        return (line.geometry as? LineString)?.let { lineString ->
            lineSliceAlong(lineString, startDist, stopDist, units)
        } ?: throw TurfException("line must be a LineString")
    }

    /**
     *
     *
     * Takes a [LineString], a specified distance along the line to a start [Point],
     * and a specified distance along the line to a stop point,
     * returns a subsection of the line in-between those points.
     *
     *
     * This can be useful for extracting only the part of a route between two distances.
     *
     * @param line input line
     * @param startDist distance along the line to starting point
     * @param stopDist distance along the line to ending point
     * @param units one of the units found inside [TurfConstants.TurfUnitCriteria]
     * can be degrees, radians, miles, or kilometers
     * @return sliced line
     * @throws TurfException signals that a Turf exception of some sort has occurred.
     * @see [Turf Line slice documentation](http://turfjs.org/docs/.lineslicealong)
     *
     * @since 3.1.0
     */
    @JvmStatic
    fun lineSliceAlong(
        line: LineString,
        startDist: Double,
        stopDist: Double,
        @TurfUnitCriteria units: String
    ): LineString {
        require(startDist >= 0) { "startDist must be greater than or equal 0" }
        require(stopDist > 0) { "stopDist must be greater than 0" }

        val coordinates = line.coordinates

        if (coordinates.size < 2) {
            throw TurfException("Turf lineSlice requires a LineString made up of at least 2 coordinates.")
        } else if (startDist == stopDist) {
            throw TurfException("Start and stop distance in Turf lineSliceAlong cannot equal each other.")
        }

        var travelled = 0.0
        val slicedLinePoints = mutableListOf<Point>()
        for ((index, point) in coordinates.withIndex()) {
            if (travelled >= startDist) {
                // Travelled distance is greater than `startDist`

                if (slicedLinePoints.size == 0) {
                    // First point after `startDist`
                    val overshot = startDist - travelled
                    if (overshot == 0.0 || index == 0) {
                        slicedLinePoints.add(point)
                    } else {
                        val direction = bearing(point, coordinates[index - 1]) - 180
                        val interpolated = destination(point, overshot, direction, units)
                        slicedLinePoints.add(interpolated)
                    }
                }

                if (travelled >= stopDist) {
                    // `stopDist` has been reached
                    val overshot = stopDist - travelled
                    if (overshot == 0.0 || index == 0) {
                        slicedLinePoints.add(point)
                    } else {
                        val direction = bearing(point, coordinates[index - 1]) - 180
                        val interpolated = destination(point, overshot, direction, units)
                        slicedLinePoints.add(interpolated)
                    }

                    break // Line slice finished
                } else {
                    // Point between `startDist` and `stopDist`
                    slicedLinePoints.add(point)
                }
            }

            coordinates.getOrNull(index + 1)?.let { upcomingPoint ->
                travelled += distance(point, upcomingPoint, units)
            }
        }

        if (travelled < startDist) {
            throw TurfException("Start position is beyond line")
        }

        return LineString(slicedLinePoints)
    }

    /**
     * Takes a [Point] and a [LineString] and calculates the closest Point on the
     * LineString.
     *
     * @param pt point to snap from
     * @param coords line to snap to
     * @param units one of the units found inside [TurfConstants.TurfUnitCriteria]
     * can be degrees, radians, miles, or kilometers
     * @return closest point on the line to point
     * @since 4.9.0
     */
    @JvmStatic
    @JvmOverloads
    fun nearestPointOnLine(
        pt: Point,
        coords: List<Point>,
        @TurfUnitCriteria units: String = TurfConstants.UNIT_KILOMETERS
    ): Feature {
        if (coords.size < 2) {
            throw TurfException("Turf nearestPointOnLine requires a List of Points made up of at least 2 coordinates.")
        }

        var closestPt = Feature(
            Point(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY),
            mapOf(DISTANCE_KEY to JsonPrimitive(Double.POSITIVE_INFINITY))
        )

        for (i in 0 until coords.size - 1) {
            val start = Feature(
                coords[i],
                mapOf(DISTANCE_KEY to JsonPrimitive(distance(pt, coords[i], units)))
            )
            val startPoint = start.geometry as Point

            val stop = Feature(
                coords[i + 1],
                mapOf(DISTANCE_KEY to JsonPrimitive(distance(pt, coords[i + 1], units)))
            )
            val stopPoint = stop.geometry as Point

            // perpendicular
            val heightDistance = max(
                start.getDoubleProperty(DISTANCE_KEY)!!,
                stop.getDoubleProperty(DISTANCE_KEY)!!
            )

            val direction = bearing(startPoint, stopPoint)

            val perpendicularPt1Feature = Feature(
                destination(pt, heightDistance, direction + 90, units)
            )
            val perpendicularPt1Point = perpendicularPt1Feature.geometry as Point

            val perpendicularPt2Feature = Feature(
                destination(pt, heightDistance, direction - 90, units)
            )
            val perpendicularPt2Point = perpendicularPt2Feature.geometry as Point

            val intersect = lineIntersects(
                perpendicularPt1Point.longitude,
                perpendicularPt1Point.latitude,
                perpendicularPt2Point.longitude,
                perpendicularPt2Point.latitude,
                startPoint.longitude,
                startPoint.latitude,
                stopPoint.longitude,
                stopPoint.latitude
            )

            val intersectPtFeature = intersect?.let { lineIntersects ->
                val intersectionPoint = Point(
                    lineIntersects.horizontalIntersection!!,
                    lineIntersects.verticalIntersection!!
                )

                Feature(
                    intersectionPoint,
                    mapOf(DISTANCE_KEY to JsonPrimitive(distance(pt, intersectionPoint, units)))
                )
            }

            var closestFeatureDistance = closestPt.getDoubleProperty(DISTANCE_KEY)!!
            val startFeatureDistance = start.getDoubleProperty(DISTANCE_KEY)!!
            if (startFeatureDistance < closestFeatureDistance) {
                closestPt = start.copy(
                    properties = mapOf(INDEX_KEY to JsonPrimitive(i)).plus(
                        start.properties ?: emptyMap()
                    )
                )
            }

            closestFeatureDistance = closestPt.getDoubleProperty(DISTANCE_KEY)!!
            val stopFeatureDistance = stop.getDoubleProperty(DISTANCE_KEY)!!
            if (stopFeatureDistance < closestFeatureDistance) {
                closestPt = stop.copy(
                    properties = mapOf(INDEX_KEY to JsonPrimitive(i)).plus(
                        stop.properties ?: emptyMap()
                    )
                )
            }

            closestFeatureDistance = closestPt.getDoubleProperty(DISTANCE_KEY)!!
            intersectPtFeature?.let { intersectPoint ->
                val intersectFeatureDistance = intersectPoint.getDoubleProperty(DISTANCE_KEY)!!
                if (intersectFeatureDistance < closestFeatureDistance) {
                    closestPt = intersectPoint.copy(
                        properties = mapOf(INDEX_KEY to JsonPrimitive(i)).plus(
                            intersectPoint.properties ?: emptyMap()
                        )
                    )
                }
            }
        }

        return closestPt
    }

    private fun lineIntersects(
        line1StartX: Double,
        line1StartY: Double,
        line1EndX: Double,
        line1EndY: Double,
        line2StartX: Double,
        line2StartY: Double,
        line2EndX: Double,
        line2EndY: Double
    ): LineIntersects? {
        // If the lines intersect, the result contains the x and y of the intersection
        // (treating the lines as infinite) and booleans for whether line segment 1 or line
        // segment 2 contain the point
        var result = LineIntersects(
            onLine1 = false,
            onLine2 = false
        )

        val denominator = (((line2EndY - line2StartY) * (line1EndX - line1StartX))
                - ((line2EndX - line2StartX) * (line1EndY - line1StartY)))
        if (denominator == 0.0) {
            return if (result.horizontalIntersection != null && result.verticalIntersection != null) {
                result
            } else {
                null
            }
        }
        var varA = line1StartY - line2StartY
        var varB = line1StartX - line2StartX
        val numerator1 = ((line2EndX - line2StartX) * varA) - ((line2EndY - line2StartY) * varB)
        val numerator2 = ((line1EndX - line1StartX) * varA) - ((line1EndY - line1StartY) * varB)
        varA = numerator1 / denominator
        varB = numerator2 / denominator

        // if we cast these lines infinitely in both directions, they intersect here:
        result = result.copy(
            horizontalIntersection = line1StartX + (varA * (line1EndX - line1StartX)),
            verticalIntersection = line1StartY + (varA * (line1EndY - line1StartY))
        )

        // if line1 is a segment and line2 is infinite, they intersect if:
        if (varA > 0 && varA < 1) {
            result = result.copy(onLine1 = true)
        }
        // if line2 is a segment and line1 is infinite, they intersect if:
        if (varB > 0 && varB < 1) {
            result = result.copy(onLine2 = true)
        }
        // if line1 and line2 are segments, they intersect if both of the above are true
        return if (result.onLine1 && result.onLine2) {
            result
        } else {
            null
        }
    }

    private data class LineIntersects(
        val horizontalIntersection: Double? = null,
        val verticalIntersection: Double? = null,
        val onLine1: Boolean,
        val onLine2: Boolean
    )
}
