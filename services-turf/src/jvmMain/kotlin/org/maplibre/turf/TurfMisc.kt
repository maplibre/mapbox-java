package org.maplibre.turf

import org.maplibre.geojson.Feature
import org.maplibre.geojson.LineString
import org.maplibre.geojson.Point
import org.maplibre.geojson.common.toJvm
import org.maplibre.turf.TurfConstants.TurfUnitCriteria
import org.maplibre.turf.common.toUnit
import org.maplibre.geojson.turf.TurfMisc as CommonTurfMisc

/**
 * Class contains all the miscellaneous methods that Turf can perform.
 *
 * @see [Turf documentation](http://turfjs.org/docs/)
 *
 * @since 1.2.0
 */
@Deprecated(
    message = "Use new common Turf utils instead.",
    replaceWith = ReplaceWith("TurfMeta", "org.maplibre.geojson.turf.TurfMisc"),
)
object TurfMisc {

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
    fun lineSlice(
        startPt: Point,
        stopPt: Point,
        line: Feature
    ): LineString {
        return CommonTurfMisc.lineSlice(startPt, stopPt, line).toJvm()
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
    fun lineSlice(
        startPt: Point,
        stopPt: Point,
        line: LineString
    ): LineString {
        return CommonTurfMisc.lineSlice(startPt, stopPt, line).toJvm()
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
        return CommonTurfMisc.lineSliceAlong(line, startDist, stopDist, units.toUnit()).toJvm()
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
        return CommonTurfMisc.lineSliceAlong(line, startDist, stopDist, units.toUnit()).toJvm()
    }

    /**
     * Takes a [Point] and a [LineString] and calculates the closest Point on the
     * LineString.
     *
     * @param pt     point to snap from
     * @param coords line to snap to
     * @return closest point on the line to point
     * @since 1.3.0
     */
    @JvmStatic
    fun nearestPointOnLine(pt: Point, coords: List<Point>): Feature {
        return CommonTurfMisc.nearestPointOnLine(pt, coords).toJvm()
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
    fun nearestPointOnLine(
        pt: Point,
        coords: List<Point>,
        @TurfUnitCriteria units: String
    ): Feature {
        return CommonTurfMisc.nearestPointOnLine(pt, coords, units.toUnit()).toJvm()
    }
}