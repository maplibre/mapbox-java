package org.maplibre.geojson.utils

import org.maplibre.geojson.LineString
import org.maplibre.geojson.Point
import org.maplibre.geojson.TestUtils.expectNearNumber
import org.maplibre.geojson.TestUtils.loadJsonFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertSame

class PolylineUtilsTest {

    @Test
    fun testDecodePath() {
        val latLngs = PolylineUtils.decode(TEST_LINE, PRECISION_5)

        val expectedLength = 21
        assertEquals(expectedLength.toLong(), latLngs.size.toLong(), "Wrong length.")

        val lastPoint = latLngs[expectedLength - 1]
        expectNearNumber(37.76953, lastPoint.latitude, 1e-6)
        expectNearNumber(-122.41488, lastPoint.longitude, 1e-6)
    }

    @Test
    fun testEncodePath5() {
        val path = PolylineUtils.decode(TEST_LINE, PRECISION_5)
        val encoded = PolylineUtils.encode(path, PRECISION_5)
        assertEquals(TEST_LINE, encoded)
    }

    @Test
    fun testDecodeEncodePath6() {
        val path = PolylineUtils.decode(TEST_LINE6, PRECISION_6)
        val encoded = PolylineUtils.encode(path, PRECISION_6)
        assertEquals(TEST_LINE6, encoded)
    }

    @Test
    fun testFromPolyline6() {
        val originalPath = listOf(
            Point(2.2862036, 48.8267868),
            Point(2.4, 48.9)
        )
        val encoded = PolylineUtils.encode(originalPath, PRECISION_6)
        val path = LineString(encoded, PRECISION_6).coordinates

        assertEquals(originalPath.size.toLong(), path.size.toLong())
        for (i in originalPath.indices) {
            assertEquals(originalPath[i].latitude, path[i].latitude, DELTA)
            assertEquals(originalPath[i].longitude, path[i].longitude, DELTA)
        }
    }

    @Test
    fun testFromPolylineAndDecode() {
        val path1 = LineString(TEST_LINE6, PRECISION_6).coordinates
        val path2 = PolylineUtils.decode(TEST_LINE6, PRECISION_6)

        assertEquals(path1.size, path2.size)
        for (i in path1.indices) {
            assertEquals(path1[i], path2[i])
        }
    }

    @Test
    fun testEncodeDecodePath6() {
        val originalPath = listOf(
            Point(2.2862036, 48.8267868),
            Point(2.4, 48.9)
        )

        val encoded = PolylineUtils.encode(originalPath, PRECISION_6)
        val path = PolylineUtils.decode(encoded, PRECISION_6)
        assertEquals(originalPath.size, path.size)

        for (i in originalPath.indices) {
            assertEquals(originalPath[i].latitude, path[i].latitude, DELTA)
            assertEquals(originalPath[i].longitude, path[i].longitude, DELTA)
        }
    }

    @Test
    fun decode_neverReturnsNullButRatherAnEmptyList() {
        val path = PolylineUtils.decode("", PRECISION_5)
        assertNotNull(path)
        assertEquals(0, path.size.toLong())
    }

    @Test
    fun encode_neverReturnsNull() {
        val encodedString = PolylineUtils.encode(ArrayList(), PRECISION_6)
        assertNotNull(encodedString)
    }

    @Test
    fun simplify_neverReturnsNullButRatherAnEmptyList() {
        val simplifiedPath = PolylineUtils.simplify(ArrayList(), PRECISION_6.toDouble())
        assertNotNull(simplifiedPath)
    }

    @Test
    fun simplify_returnSameListWhenListSizeIsLessThanOrEqualToTwo() {
        val path = listOf(
            Point(0.0, 0.0),
            Point(10.0, 0.0)
        )
        val simplifiedPath = PolylineUtils.simplify(path, PRECISION_6.toDouble(), true)
        assertSame(path, simplifiedPath, "Returned list is different from input list")
    }

    @Test
    fun simplify_withHighestQuality() {
        val path = createPointListFromResourceFile(SIMPLIFICATION_INPUT)
        val simplifiedPath = PolylineUtils.simplify(path, PRECISION_5.toDouble(), true)
        val expectedSimplifiedPath = createPointListFromResourceFile(SIMPLIFICATION_EXPECTED_OUTPUT)

        assertEquals(simplifiedPath.size, expectedSimplifiedPath.size, "Wrong number of points retained")

        for ((counter, retainedPoint) in simplifiedPath.withIndex()) {
            val expectedPoint = expectedSimplifiedPath[counter]
            assertEquals(retainedPoint, expectedPoint, "Wrong point retained by simplification algorithm")
        }
    }

    private fun createPointListFromResourceFile(fileName: String): List<Point> {
        val inputPoints = loadJsonFixture(fileName)
        val coords = inputPoints.split(",".toRegex()).toTypedArray()

        val pointList: MutableList<Point> = ArrayList()
        var idx = 0
        while (idx <= coords.size - 2) {
            val xCoord = coords[idx].toDouble()
            val yCoord = coords[idx + 1].toDouble()
            pointList.add(Point(xCoord, yCoord))
            idx += 2
        }

        return pointList
    }

    companion object {
        private const val PRECISION_6 = 6
        private const val PRECISION_5 = 5

        // Delta for Coordinates comparison
        private const val DELTA = 0.000001

        private const val SIMPLIFICATION_INPUT = "simplification-input"
        private const val SIMPLIFICATION_EXPECTED_OUTPUT = "simplification-expected-output"

        private const val TEST_LINE =
            "_cqeFf~cjVf@p@fA}AtAoB`ArAx@hA`GbIvDiFv@gAh@t@X\\|@z@`@Z\\Xf@Vf@VpA\\tATJ@NBBkC"

        private const val TEST_LINE6 =
            "qn_iHgp}LzCy@xCsAsC}PoEeD_@{A@uD_@Sg@Je@a@I_@FcAoFyGcCqFgQ{L{CmD"
    }
}
