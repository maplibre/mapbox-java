package org.maplibre.geojson

import kotlinx.serialization.SerializationException
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.maplibre.geojson.MultiPolygon
import org.maplibre.geojson.Polygon.Companion.fromOuterInner
import java.io.IOException
import org.maplibre.geojson.TestUtils.DELTA
import org.maplibre.geojson.TestUtils.compareJson

class MultiPolygonTest {

    @Test
    fun sanity() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0),
            Point(3.0, 4.0),
            Point(1.0, 2.0),
        )

        val outer = LineString(points)
        val polygons = listOf(
            fromOuterInner(outer),
            fromOuterInner(outer)
        )
        val multiPolygon = MultiPolygon.fromPolygons(polygons)
        assertNotNull(multiPolygon)
    }

    @Test
    fun bbox_nullWhenNotSet() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0),
            Point(3.0, 4.0),
            Point(1.0, 2.0),
        )

        val outer = LineString(points)
        val polygons = listOf(
            fromOuterInner(outer),
            fromOuterInner(outer)
        )
        val multiPolygon = MultiPolygon.fromPolygons(polygons)
        assertNull(multiPolygon.bbox)
    }

    @Test
    fun bbox_doesNotSerializeWhenNotPresent() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0),
            Point(3.0, 4.0),
            Point(1.0, 2.0),
        )

        val outer = LineString(points)
        val polygons = listOf(
            fromOuterInner(outer),
            fromOuterInner(outer)
        )
        val multiPolygon = MultiPolygon.fromPolygons(polygons)
        compareJson(
            multiPolygon.toJson(),
            "{\"type\":\"MultiPolygon\","
                    + "\"coordinates\":[[[[1,2],[2,3],[3,4],[1,2]]],[[[1,2],[2,3],[3,4],[1,2]]]]}"
        )
    }

    @Test
    fun bbox_returnsCorrectBbox() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0),
            Point(3.0, 4.0),
            Point(1.0, 2.0),
        )

        val outer = LineString(points)
        val polygons = listOf(
            fromOuterInner(outer),
            fromOuterInner(outer)
        )
        val bbox = BoundingBox(1.0, 2.0, 3.0, 4.0)
        val multiPolygon = MultiPolygon.fromPolygons(polygons, bbox)
        assertNotNull(multiPolygon.bbox)
        assertEquals(1.0, multiPolygon.bbox!!.west, DELTA)
        assertEquals(2.0, multiPolygon.bbox!!.south, DELTA)
        assertEquals(3.0, multiPolygon.bbox!!.east, DELTA)
        assertEquals(4.0, multiPolygon.bbox!!.north, DELTA)
    }

    @Test
    fun passingInSinglePolygon_doesHandleCorrectly() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(3.0, 4.0)
        )

        val polygon = Polygon(listOf(points))
        val multiPolygon = MultiPolygon.fromPolygon(polygon)
        assertNotNull(multiPolygon)
        assertEquals(1, multiPolygon.polygons.size)
        assertEquals(
            2.0,
            multiPolygon.polygons.first().coordinates.first().first().latitude,
            DELTA
        )
    }

    @Test
    fun bbox_doesSerializeWhenPresent() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0),
            Point(3.0, 4.0),
            Point(1.0, 2.0),
        )

        val outer = LineString(points)
        val polygons = listOf(
            fromOuterInner(outer),
            fromOuterInner(outer)
        )
        val bbox = BoundingBox(1.0, 2.0, 3.0, 4.0)
        val multiPolygon = MultiPolygon.fromPolygons(polygons, bbox)
        compareJson(
            multiPolygon.toJson(),
            "{\"type\":\"MultiPolygon\",\"bbox\":[1.0,2.0,3.0,4.0],"
                    + "\"coordinates\":[[[[1,2],[2,3],[3,4],[1,2]]],[[[1,2],[2,3],[3,4],[1,2]]]]}"
        )
    }

//    @Test
//    fun testSerializable() {
//        val points = listOf(
//            Point(1.0, 2.0),
//            Point(2.0, 3.0),
//            Point(3.0, 4.0),
//            Point(1.0, 2.0),
//        )
//
//        val outer = LineString(points)
//        val polygons = listOf(
//            fromOuterInner(outer),
//            fromOuterInner(outer)
//        )
//        val bbox = BoundingBox(1.0, 2.0, 3.0, 4.0)
//        val multiPolygon = MultiPolygon.fromPolygons(polygons, bbox)
//        val bytes = serialize(multiPolygon)
//        assertEquals(
//            multiPolygon, deserialize(
//                bytes,
//                MultiPolygon::class.java
//            )
//        )
//    }

    @Test
    fun fromJson() {
        val json = "{\"type\":\"MultiPolygon\",\"coordinates\": " +
                "    [[[[102, 2], [103, 2], [103, 3], [102, 3], [102, 2]]]," +
                "     [[[100, 0], [101, 0], [101, 1], [100, 1], [100, 0]]," +
                "      [[100.2, 0.2], [100.2, 0.8], [100.8, 0.8], [100.8, 0.2], [100.2, 0.2]]]]}"
        val geo = MultiPolygon.fromJson(json)
//        assertEquals(geo.type, "MultiPolygon")
        assertEquals(geo.coordinates.first().first().first().longitude, 102.0, DELTA)
        assertEquals(geo.coordinates.first().first().first().latitude, 2.0, DELTA)
        assertNull(geo.coordinates.first().first().first().altitude)
    }

    @Test
    fun toJson() {
        val json = "{\"type\":\"MultiPolygon\",\"coordinates\": " +
                "    [[[[102, 2], [103, 2], [103, 3], [102, 3], [102, 2]]]," +
                "     [[[100, 0], [101, 0], [101, 1], [100, 1], [100, 0]]," +
                "      [[100.2, 0.2], [100.2, 0.8], [100.8, 0.8], [100.8, 0.2], [100.2, 0.2]]]]}"

        val multiPolygon = MultiPolygon.fromJson(json)
        compareJson(json, multiPolygon.toJson())
    }

    @Test
    fun fromJson_coordinatesPresent() {
        assertThrows(SerializationException::class.java) {
            MultiPolygon.fromJson("{\"type\":\"MultiPolygon\",\"coordinates\":null}")
        }
    }
}
