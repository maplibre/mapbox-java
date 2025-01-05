package org.maplibre.geojson

import kotlinx.serialization.SerializationException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.Test
import org.maplibre.geojson.TestUtils.DELTA
import org.maplibre.geojson.TestUtils.compareJson
import kotlin.test.assertFailsWith

class LineStringTest {

    @Test
    fun sanity() {
        val points = listOf(
            Point(1.0, 1.0),
            Point(2.0, 2.0),
            Point(3.0, 3.0),
        )

        val lineString = LineString(points)
        assertNotNull(lineString)
    }

    @Test
    fun fromLngLats_generatedFromMultipoint() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(4.0, 8.0)
        )

        val multiPoint = MultiPoint(points)
        val lineString = LineString(multiPoint)
        assertEquals("_gayB_c`|@_wemJ_kbvD", lineString.toPolyline(PRECISION_6))
    }

    @Test
    fun bbox_nullWhenNotSet() {
        val points = listOf(
            Point(1.0, 1.0),
            Point(2.0, 2.0),
            Point(3.0, 3.0)
        )

        val lineString = LineString(points)
        assertNull(lineString.bbox)
    }

    @Test
    fun bbox_doesNotSerializeWhenNotPresent() {
        val points = listOf(
            Point(1.0, 1.0),
            Point(2.0, 2.0),
            Point(3.0, 3.0)
        )

        val lineString = LineString(points)
        compareJson(
            lineString.toJson(),
            "{\"coordinates\":[[1.0,1.0],[2.0,2.0],[3.0,3.0]],\"type\":\"LineString\"}"
        )
    }

    @Test
    fun bbox_returnsCorrectBbox() {
        val points = listOf(
            Point(1.0, 1.0),
            Point(2.0, 2.0),
            Point(3.0, 3.0)
        )

        val bbox = BoundingBox(1.0, 2.0, 3.0, 4.0)
        val lineString = LineString(points, bbox)

        assertNotNull(lineString.bbox)
        assertEquals(1.0, lineString.bbox!!.west, DELTA)
        assertEquals(2.0, lineString.bbox!!.south, DELTA)
        assertEquals(3.0, lineString.bbox!!.east, DELTA)
        assertEquals(4.0, lineString.bbox!!.north, DELTA)
    }

    @Test
    fun bbox_doesSerializeWhenPresent() {
        val points = listOf(
            Point(1.0, 1.0),
            Point(2.0, 2.0),
            Point(3.0, 3.0)
        )

        val bbox = BoundingBox(1.0, 2.0, 3.0, 4.0)
        val lineString = LineString(points, bbox)
        val lineStringJson = lineString.toJson()
        compareJson(
            "{\"coordinates\":[[1.0,1.0],[2.0,2.0],[3.0,3.0]],"
                    + "\"type\":\"LineString\",\"bbox\":[1.0,2.0,3.0,4.0]}",
            lineStringJson
        )
    }

    @Test
    fun bbox_doesDeserializeWhenPresent() {
        val lineString = LineString.fromJson(
            "{\"coordinates\":[[1,2],[2,3],[3,4]],"
                    + "\"type\":\"LineString\",\"bbox\":[1.0,2.0,3.0,4.0]}"
        )

        assertNotNull(lineString)
        assertNotNull(lineString.bbox)
        assertEquals(1.0, lineString.bbox!!.southwest.longitude, DELTA)
        assertEquals(2.0, lineString.bbox!!.southwest.latitude, DELTA)
        assertEquals(3.0, lineString.bbox!!.northeast.longitude, DELTA)
        assertEquals(4.0, lineString.bbox!!.northeast.latitude, DELTA)
        assertEquals(1.0, lineString.coordinates[0].longitude, DELTA)
        assertEquals(2.0, lineString.coordinates[0].latitude, DELTA)
        assertEquals(2.0, lineString.coordinates[1].longitude, DELTA)
        assertEquals(3.0, lineString.coordinates[1].latitude, DELTA)
        assertEquals(3.0, lineString.coordinates[2].longitude, DELTA)
        assertEquals(4.0, lineString.coordinates[2].latitude, DELTA)
    }

    @Test
    fun fromJson() {
        val json = "{\"type\": \"LineString\"," +
                "  \"coordinates\": [[ 100, 0], [101, 1]]} "
        val geo: LineString = LineString.fromJson(json)
        assertEquals(geo.coordinates.first().longitude, 100.0, 0.0)
        assertEquals(geo.coordinates.first().latitude, 0.0, 0.0)
        assertNull(geo.coordinates.first().altitude)
    }

    @Test
    fun toJson() {
        val json = "{\"type\": \"LineString\"," +
                "  \"coordinates\": [[ 100.0, 0.0], [101.0, 1.0]]} "
        val geo: LineString = LineString.fromJson(json)
        val geoJsonString = geo.toJson()
        compareJson(geoJsonString, json)
    }

    @Test
    fun fromJson_coordinatesNotPresent() {
        assertFailsWith(SerializationException::class) {
            LineString.fromJson("{\"type\":\"LineString\",\"coordinates\":null}")
        }
    }

    companion object {
        private const val PRECISION_6 = 6
    }
}
