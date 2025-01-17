package org.maplibre.geojson.turf

import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.Test
import org.maplibre.geojson.model.Feature
import org.maplibre.geojson.model.LineString
import org.maplibre.geojson.model.Point
import org.maplibre.geojson.turf.TestUtils.loadJsonFixture
import org.maplibre.geojson.turf.TurfMeasurement.along
import org.maplibre.geojson.turf.TurfMeasurement.distance
import org.maplibre.geojson.turf.TurfMeasurement.length
import org.maplibre.geojson.turf.TurfMisc.lineSlice
import org.maplibre.geojson.turf.TurfMisc.lineSliceAlong
import org.maplibre.geojson.turf.TurfMisc.nearestPointOnLine
import kotlin.test.assertFailsWith

class TurfMiscTest {

    @Test
    fun lineSlice_throwsStartStopPointException() {
        val point = Point(1.0, 1.0)
        val point2 = Point(2.0, 2.0)
        val coords = listOf(point)

        val lineString = LineString(coords)
        assertFailsWith(TurfException::class) {
            lineSlice(point, point2, lineString)
        }
    }

    @Test
    fun lineSlice_throwLineMustContainTwoOrMorePoints() {
        val point = Point(1.0, 1.0)
        val coords = listOf(
            point,
            Point(2.0, 2.0)
        )

        val lineString = LineString(coords)

        assertFailsWith(TurfException::class) {
            lineSlice(point, point, lineString)
        }
    }

    @Test
    fun lineSlice_returnsEmptyLineStringRatherThanNull() {
        val coords = listOf(
            Point(1.0, 1.0),
            Point(2.0, 2.0)
        )

        val lineString = LineString(coords)
        assertNotNull(lineSlice(coords[0], coords[1], lineString))
    }

    @Test
    fun testTurfLineSliceLine1() {
        val start = Point(-97.79617309570312, 22.254624939561698)
        val stop = Point(-97.72750854492188, 22.057641623615734)

        val line1 = Feature.fromJson(loadJsonFixture(LINE_SLICE_ONE))

        val sliced = lineSlice(start, stop, line1)
        assertNotNull(sliced)
    }

    @Test
    fun testTurfLineSliceRawGeometry() {
        val start = Point(-97.79617309570312, 22.254624939561698)
        val stop = Point(-97.72750854492188, 22.057641623615734)

        val line1 = Feature.fromJson(loadJsonFixture(LINE_SLICE_ONE))

        val sliced = lineSlice(
            start,
            stop,
            line1.geometry as LineString
        )

        assertNotNull(sliced)
    }

    @Test
    fun testTurfLineSliceLine2() {
        val start = Point(0.0, 0.1)
        val stop = Point(.9, .8)

        val coordinates = listOf(
            Point(0.0, 0.0),
            Point(1.0, 1.0)
        )
        val line2 = LineString(coordinates)

        val sliced = lineSlice(start, stop, line2)
        assertNotNull(sliced)
    }

    @Test
    fun testTurfLineSliceRoute1() {
        val start = Point(-79.0850830078125, 37.60117623656667)
        val stop = Point(-77.7667236328125, 38.65119833229951)

        val route1 = Feature.fromJson(loadJsonFixture(LINE_SLICE_ROUTE_ONE))

        val sliced = lineSlice(start, stop, route1)
        assertNotNull(sliced)
    }

    @Test
    fun testTurfLineSliceRoute2() {
        val start = Point(-112.60660171508789, 45.96021963947196)
        val stop = Point(-111.97265625, 48.84302835299516)

        val route2 = Feature.fromJson(loadJsonFixture(LINE_SLICE_ROUTE_TWO))

        val sliced = lineSlice(start, stop, route2)
        assertNotNull(sliced)
    }

    @Test
    fun testTurfLineSliceVertical() {
        val start = Point(-121.25447809696198, 38.70582415504791)
        val stop = Point(-121.25447809696198, 38.70634324369764)

        val vertical = Feature.fromJson(loadJsonFixture(LINE_SLICE_VERTICAL))

        val sliced = lineSlice(start, stop, vertical)
        assertNotNull(sliced)

        // No duplicated coords
        assertEquals(2, sliced.coordinates.size.toLong())

        // Vertical slice does not collapse to 1st coord
        assertNotEquals(sliced.coordinates[0], sliced.coordinates[1])
    }

    /*
   * Point on line test
   */
    @Test
    fun pointOnLine_throwLineMustContainTwoOrMorePoints() {
        val line = listOf(
            Point(-122.45717525482178, 37.72003306385638)
        )

        assertFailsWith(TurfException::class) {
            nearestPointOnLine(line[0], line)
        }
    }

    @Test
    fun testTurfPointOnLineFirstPoint() {
        val pt = Point(-122.45717525482178, 37.72003306385638)
        val line = listOf(
            pt,
            Point(-122.45717525482178, 37.718242366859215)

        )

        val snappedFeature = nearestPointOnLine(pt, line)
        val snapped = snappedFeature.geometry as Point
        // pt on start does not move
        assertEquals(pt, snapped)
    }

    @Test
    fun testTurfPointOnLinePointsBehindFirstPoint() {
        val line = listOf(
            Point(-122.45717525482178, 37.72003306385638),
            Point(-122.45717525482178, 37.718242366859215)
        )

        val pts = listOf(
            Point(-122.45717525482178, 37.72009306385638),
            Point(-122.45717525482178, 37.82009306385638),
            Point(-122.45716525482177, 37.72009306385638),
            Point(-122.45516525482178, 37.72009306385638),
        )

        for (pt in pts) {
            val snappedFeature = nearestPointOnLine(pt, line)
            val snapped = snappedFeature.geometry as Point
            // pt behind start moves to first vertex
            assertEquals(line.first(), snapped)
        }
    }

    @Test
    fun testTurfPointOnLinePointsInFrontOfLastPoint() {
        val line = listOf(
            Point(-122.45616137981413, 37.72125936929241),
            Point(-122.45717525482178, 37.72003306385638),
            Point(-122.45717525482178, 37.718242366859215),
        )

        val pts = listOf(
            Point(-122.45696067810057, 37.7181405249708),
            Point(-122.4573630094528, 37.71813203814049),
            Point(-122.45730936527252, 37.71797927502795),
            Point(-122.45718061923981, 37.71704571582896)
        )

        for (pt in pts) {
            val snappedFeature = nearestPointOnLine(pt, line)
            val snapped = snappedFeature.geometry as Point
            // pt behind start moves to last vertex
            assertEquals(line.last(), snapped)
        }
    }

    @Test
    fun testTurfPointOnLinePointsOnJoints() {
        val line1 = listOf(
            Point(-122.45616137981413, 37.72125936929241),
            Point(-122.45717525482178, 37.72003306385638),
            Point(-122.45717525482178, 37.718242366859215)
        )

        val line2 = listOf(
            Point(26.279296875, 31.728167146023935),
            Point(21.796875, 32.69486597787505),
            Point(18.80859375, 29.99300228455108),
            Point(12.919921874999998, 33.137551192346145),
            Point(10.1953125, 35.60371874069731),
            Point(4.921875, 36.527294814546245),
            Point(-1.669921875, 36.527294814546245),
            Point(-5.44921875, 34.74161249883172),
            Point(-8.7890625, 32.99023555965106)
        )

        val line3 = listOf(
            Point(-0.10919809341430663, 51.52204224896724),
            Point(-0.10923027992248535, 51.521942114455435),
            Point(-0.10916590690612793, 51.52186200668747),
            Point(-0.10904788970947266, 51.52177522311313),
            Point(-0.10886549949645996, 51.521601655468345),
            Point(-0.10874748229980469, 51.52138135712038),
            Point(-0.10855436325073242, 51.5206870765674),
            Point(-0.10843634605407713, 51.52027984939518),
            Point(-0.10839343070983887, 51.519952729849024),
            Point(-0.10817885398864746, 51.51957887606202),
            Point(-0.10814666748046874, 51.51928513164789),
            Point(-0.10789990425109863, 51.518624199789016),
            Point(-0.10759949684143065, 51.51778299991493)
        )

        val lines = listOf(line1, line2, line3)

        for (line in lines) {
            val linePoint = line.map { pt -> Point(pt.longitude, pt.latitude) }

            for (pt in line) {
                val snappedFeature = nearestPointOnLine(pt, linePoint)
                val snapped = snappedFeature.geometry as Point
                // pt on joint stayed in place
                assertEquals(pt, snapped)
            }
        }
    }

    @Test
    fun testTurfPointOnLinePointsOnTopOfLine() {
        val line = listOf(
            Point(-0.10919809341430663, 51.52204224896724),
            Point(-0.10923027992248535, 51.521942114455435),
            Point(-0.10916590690612793, 51.52186200668747),
            Point(-0.10904788970947266, 51.52177522311313),
            Point(-0.10886549949645996, 51.521601655468345),
            Point(-0.10874748229980469, 51.52138135712038),
            Point(-0.10855436325073242, 51.5206870765674),
            Point(-0.10843634605407713, 51.52027984939518),
            Point(-0.10839343070983887, 51.519952729849024),
            Point(-0.10817885398864746, 51.51957887606202),
            Point(-0.10814666748046874, 51.51928513164789),
            Point(-0.10789990425109863, 51.518624199789016),
            Point(-0.10759949684143065, 51.51778299991493)
        )

        val dist = length(LineString(line), TurfUnit.MILES)
        val increment = dist / 10

        for (i in 0..9) {
            val pt = along(
                LineString(line), increment * i, TurfUnit.MILES
            )
            val snappedFeature = nearestPointOnLine(pt, line)
            val snapped = snappedFeature.geometry as Point?

            val shift = distance(pt, snapped!!, TurfUnit.MILES)

            // pt did not shift far
            assertTrue(shift < 0.000001)
        }
    }

    @Test
    fun testTurfPointOnLinePointAlongLine() {
        val line = listOf(
            Point(-122.45717525482178, 37.7200330638563),
            Point(-122.45717525482178, 37.718242366859215),
        )

        val pt = along(
            LineString(line), 0.019, TurfUnit.MILES
        )
        val snappedFeature = nearestPointOnLine(pt, line)
        val snapped = snappedFeature.geometry as Point?
        val shift = distance(pt, snapped!!, TurfUnit.MILES)

        // pt did not shift far
        assertTrue(shift < 0.00001)
    }

    @Test
    fun testTurfPointOnLinePointsOnSidesOfLines() {
        val line = listOf(
            Point(-122.45616137981413, 37.72125936929241),
            Point(-122.45717525482178, 37.718242366859215)
        )

        val pts = listOf(
            Point(-122.45702505111694, 37.71881098149625),
            Point(-122.45733618736267, 37.719235317933844),
            Point(-122.45686411857605, 37.72027068864082),
            Point(-122.45652079582213, 37.72063561093274)
        )

        for (pt in pts) {
            val snappedFeature = nearestPointOnLine(pt, line)
            val snapped = snappedFeature.geometry as Point?
            // pt did not snap to first vertex
            assertNotEquals(snapped, line.first())
            // pt did not snap to last vertex
            assertNotEquals(snapped, line.last())
        }
    }

    @Test
    fun testTurfPointOnLinePointsOnSidesOfLinesCustomUnit() {
        val line = listOf(
            Point(-122.45616137981413, 37.72125936929241),
            Point(-122.45717525482178, 37.718242366859215)
        )

        val pts = listOf(
            Point(-122.45702505111694, 37.71881098149625),
            Point(-122.45733618736267, 37.719235317933844),
            Point(-122.45686411857605, 37.72027068864082),
            Point(-122.45652079582213, 37.72063561093274)
        )

        for (pt in pts) {
            val snappedFeature = nearestPointOnLine(pt, line, TurfUnit.MILES)
            val snapped = snappedFeature.geometry as Point?
            // pt did not snap to first vertex
            assertNotEquals(snapped, line.first())
            // pt did not snap to last vertex
            assertNotEquals(snapped, line.last())
        }
    }

    @Test
    fun testLineSliceAlongLine1() {
        val line1 = Feature.fromJson(loadJsonFixture(LINE_SLICE_ALONG_LINE_ONE))
        val lineStringLine1 = line1.geometry as LineString?

        val start = 500.0
        val stop = 750.0

        val startPoint = along(lineStringLine1!!, start, TurfUnit.MILES)
        val endPoint = along(lineStringLine1, stop, TurfUnit.MILES)
        val sliced = lineSliceAlong(line1, start, stop, TurfUnit.MILES)

        assertEquals(
            sliced.coordinates.first().coordinates,
            startPoint.coordinates
        )
        assertEquals(
            sliced.coordinates.last().coordinates,
            endPoint.coordinates
        )
    }

    @Test
    fun testLineSliceAlongOvershootLine1() {
        val line1 = Feature.fromJson(loadJsonFixture(LINE_SLICE_ALONG_LINE_ONE))
        val lineStringLine1 = line1.geometry as LineString

        val start = 500.0
        val stop = 1500.0

        val startPoint = along(lineStringLine1, start, TurfUnit.MILES)
        val endPoint = along(lineStringLine1, stop, TurfUnit.MILES)
        val sliced = lineSliceAlong(line1, start, stop, TurfUnit.MILES)

        assertEquals(
            sliced.coordinates.first().coordinates,
            startPoint.coordinates
        )
        assertEquals(
            sliced.coordinates.last().coordinates,
            endPoint.coordinates
        )
    }

    @Test
    fun testLineSliceAlongRoute1() {
        val route1 = Feature.fromJson(loadJsonFixture(LINE_SLICE_ALONG_ROUTE_ONE))
        val lineStringRoute1 = route1.geometry as LineString

        val start = 500.0
        val stop = 750.0

        val startPoint = along(lineStringRoute1, start, TurfUnit.MILES)
        val endPoint = along(lineStringRoute1, stop, TurfUnit.MILES)

        val sliced = lineSliceAlong(route1, start, stop, TurfUnit.MILES)

        assertEquals(
            sliced.coordinates.first().coordinates,
            startPoint.coordinates
        )
        assertEquals(
            sliced.coordinates[sliced.coordinates.size - 1].coordinates,
            endPoint.coordinates
        )
    }

    @Test
    fun testLineSliceAlongRoute2() {
        val route2 = Feature.fromJson(loadJsonFixture(LINE_SLICE_ALONG_ROUTE_TWO))
        val lineStringRoute2 = route2.geometry as LineString?
        val start = 25.0
        val stop = 50.0

        val startPoint = along(lineStringRoute2!!, start, TurfUnit.MILES)
        val endPoint = along(lineStringRoute2, stop, TurfUnit.MILES)
        val sliced = lineSliceAlong(route2, start, stop, TurfUnit.MILES)

        assertEquals(
            sliced.coordinates[0].coordinates,
            startPoint.coordinates
        )
        assertEquals(
            sliced.coordinates[sliced.coordinates.size - 1].coordinates,
            endPoint.coordinates
        )
    }

    @Test
    fun testLineAlongStartLongerThanLength() {
        val line1 = Feature.fromJson(loadJsonFixture(LINE_SLICE_ALONG_LINE_ONE))

        val start = 500000.0
        val stop = 800000.0

        assertFailsWith(TurfException::class) {
            lineSliceAlong(line1, start, stop, TurfUnit.MILES)
        }
    }

    @Test
    fun testLineAlongStopLongerThanLength() {
        val line1 = Feature.fromJson(loadJsonFixture(LINE_SLICE_ALONG_LINE_ONE))
        val lineStringLine1 = line1.geometry as LineString

        val start = 500.0
        val stop = 800000.0
        val startPoint = along(lineStringLine1, start, TurfUnit.MILES)
        val lineCoordinates = lineStringLine1.coordinates
        val sliced = lineSliceAlong(line1, start, stop, TurfUnit.MILES)
        assertEquals(
            sliced.coordinates[0].coordinates,
            startPoint.coordinates
        )
        assertEquals(
            sliced.coordinates[sliced.coordinates.size - 1].coordinates,
            lineCoordinates[lineCoordinates.size - 1].coordinates
        )
    }

    @Test
    fun testShortLine() {
        // Distance between points is about 186 miles

        val lineStringLine1 = LineString(
            listOf(
                Point(113.99414062499999, 22.350075806124867),
                Point(116.76269531249999, 23.241346102386135)
            )
        )

        val start = 50.0
        val stop = 100.0

        val startPoint = along(lineStringLine1, start, TurfUnit.MILES)
        val endPoint = along(lineStringLine1, stop, TurfUnit.MILES)
        val sliced = lineSliceAlong(lineStringLine1, start, stop, TurfUnit.MILES)

        assertEquals(
            sliced.coordinates[0].coordinates,
            startPoint.coordinates
        )
        assertEquals(
            sliced.coordinates.last().coordinates,
            endPoint.coordinates
        )
    }

    companion object {
        private const val LINE_SLICE_ONE = "turf-line-slice/line1.geojson"
        private const val LINE_SLICE_ROUTE_ONE = "turf-line-slice/route1.geojson"
        private const val LINE_SLICE_ROUTE_TWO = "turf-line-slice/route2.geojson"
        private const val LINE_SLICE_VERTICAL = "turf-line-slice/vertical.geojson"

        private const val LINE_SLICE_ALONG_LINE_ONE = "turf-line-slice-along/line1.geojson"
        private const val LINE_SLICE_ALONG_ROUTE_ONE = "turf-line-slice-along/route1.geojson"
        private const val LINE_SLICE_ALONG_ROUTE_TWO = "turf-line-slice-along/route2.geojson"
    }
}
