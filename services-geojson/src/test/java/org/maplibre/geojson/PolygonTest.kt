package org.maplibre.geojson

import kotlinx.serialization.SerializationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test
import org.maplibre.geojson.TestUtils.DELTA
import org.maplibre.geojson.TestUtils.compareJson
import org.maplibre.geojson.exception.GeoJsonException

class PolygonTest {

    @Test
    fun sanity() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0),
            Point(3.0, 4.0),
            Point(1.0, 2.0)
        )

        val polygon = Polygon.fromOuterInner(LineString(points))
        assertNotNull(polygon)
    }

    //TODO: needed?
//    @Test
//    fun fromLngLats_tripleDoubleArray() {
//        val coordinates = arrayOf(
//            arrayOf(
//                doubleArrayOf(100.0, 0.0),
//                doubleArrayOf(101.0, 0.0),
//                doubleArrayOf(101.0, 1.0),
//                doubleArrayOf(100.0, 1.0),
//                doubleArrayOf(100.0, 0.0)
//            )
//        )
//        val polygon: Polygon = Polygon.fromLngLats(coordinates)
//        assertEquals(0, polygon.inner().size())
//        assertEquals(Point.fromLngLat(100.0, 0.0), polygon.coordinates().get(0).get(0))
//    }

    @Test
    fun fromOuterInner_throwsNotLinearRingException() {
        val points = listOf(
            Point(10.0, 2.0),
            Point(5.0, 2.0),
            Point(3.0, 2.0)
        )

        assertThrows(GeoJsonException::class.java) {
            Polygon.fromOuterInner(LineString(points))
        }
    }

    @Test
    fun fromOuterInner_throwsNotConnectedLinearRingException() {
        val points = listOf(
            Point(10.0, 2.0),
            Point(5.0, 2.0),
            Point(3.0, 2.0),
            Point(5.0, 2.0),
        )

        assertThrows(GeoJsonException::class.java) {
            Polygon.fromOuterInner(LineString(points))
        }
    }

    @Test
    fun fromOuterInner_handlesSingleLineStringCorrectly() {
        val points = listOf(
            Point(10.0, 2.0),
            Point(5.0, 2.0),
            Point(3.0, 2.0),
            Point(10.0, 2.0),
        )

        val polygon = Polygon.fromOuterInner(LineString(points))
        assertEquals(Point(10.0, 2.0), polygon.coordinates.first().first())
    }

    @Test
    fun fromOuterInner_handlesOuterAndInnerLineStringCorrectly() {
        val outerPoints = listOf(
            Point(10.0, 2.0),
            Point(5.0, 2.0),
            Point(3.0, 2.0),
            Point(10.0, 2.0),
        )
        val outerLineString = LineString(outerPoints)

        val innerPoints = listOf(
            Point(5.0, 2.0),
            Point(2.5, 2.0),
            Point(1.5, 2.0),
            Point(5.0, 2.0),
        )
        val innerLineString = LineString(innerPoints)

        val polygon = Polygon.fromOuterInner(outerLineString, listOf(innerLineString))
        assertEquals(Point(10.0, 2.0), polygon.coordinates.first().first())
        assertEquals(outerLineString, polygon.outerLine)
        assertEquals(1, polygon.innerLines.size)
        assertEquals(innerLineString, polygon.innerLines.first())
    }

    @Test
    fun fromOuterInner_withABoundingBox() {
        val outerPoints = listOf(
            Point(10.0, 2.0),
            Point(5.0, 2.0),
            Point(3.0, 2.0),
            Point(10.0, 2.0),
        )
        val outerLineString = LineString(outerPoints)

        val innerPoints = listOf(
            Point(5.0, 2.0),
            Point(2.5, 2.0),
            Point(1.5, 2.0),
            Point(5.0, 2.0),
        )
        val innerLineString = LineString(innerPoints)

        val bbox = BoundingBox(1.0, 2.0, 3.0, 4.0)
        val polygon = Polygon.fromOuterInner(outerLineString, listOf(innerLineString), bbox)

        assertEquals(bbox, polygon.bbox)
        assertEquals(outerLineString, polygon.outerLine)
        assertEquals(1, polygon.innerLines.size)
        assertEquals(innerLineString, polygon.innerLines.first())
    }

    @Test
    fun bbox_nullWhenNotSet() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0),
            Point(3.0, 4.0),
            Point(1.0, 2.0)
        )

        val outerLine = LineString(points)
        val innerLines = listOf(LineString(points), LineString(points))

        val polygon = Polygon.fromOuterInner(outerLine, inner = innerLines)
        assertNull(polygon.bbox)
    }

    @Test
    fun bbox_doesNotSerializeWhenNotPresent() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0),
            Point(3.0, 4.0),
            Point(1.0, 2.0),
        )

        val outerLine = LineString(points)

        val innerLines = listOf(LineString(points), LineString(points))
        val polygon = Polygon.fromOuterInner(outerLine, innerLines)
        compareJson(
            polygon.toJson(),
            "{\"type\":\"Polygon\",\"coordinates\":"
                    + "[[[1,2],[2,3],[3,4],[1,2]],[[1,2],[2,3],[3,4],[1,2]],[[1,2],[2,3],[3,4],[1,2]]]}"
        )
    }

    @Test
    fun bbox_returnsCorrectBbox() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0),
            Point(3.0, 4.0),
            Point(1.0, 2.0)
        )

        val outerLine = LineString(points)
        val innerLines = listOf(LineString(points), LineString(points))
        val bbox = BoundingBox(1.0, 2.0, 3.0, 4.0)
        val polygon = Polygon.fromOuterInner(outerLine, innerLines, bbox)

        assertNotNull(polygon.bbox)
        assertEquals(1.0, polygon.bbox!!.west, DELTA)
        assertEquals(2.0, polygon.bbox!!.south, DELTA)
        assertEquals(3.0, polygon.bbox!!.east, DELTA)
        assertEquals(4.0, polygon.bbox!!.north, DELTA)
    }

    @Test
    fun bbox_doesSerializeWhenPresent() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0),
            Point(3.0, 4.0),
            Point(1.0, 2.0)
        )

        val outerLine = LineString(points)
        val innerLines = listOf(LineString(points), LineString(points))
        val bbox = BoundingBox(1.0, 2.0, 3.0, 4.0)
        val polygon = Polygon.fromOuterInner(outerLine, innerLines, bbox)
        compareJson(
            polygon.toJson(),
            "{\"type\":\"Polygon\",\"bbox\":[1.0,2.0,3.0,4.0],\"coordinates\":"
                    + "[[[1,2],[2,3],[3,4],[1,2]],[[1,2],[2,3],[3,4],[1,2]],[[1,2],[2,3],[3,4],[1,2]]]}"
        )
    }

    //TODO fabi755
//    @Test
//    fun testSerializable() {
//        val points = listOf(
//            Point(1.0, 2.0),
//            Point(2.0, 3.0),
//            Point(3.0, 4.0),
//            Point(1.0, 2.0)
//        )
//
//        val outerLine = LineString(points)
//val innerLines = listOf(LineString(points), LineString(points))
//        val bbox: BoundingBox = BoundingBox.fromLngLats(1.0, 2.0, 3.0, 4.0)
//        val polygon = Polygon.fromOuterInner(outerLine, innerLines, bbox)
//        val bytes = serialize(polygon)
//        assertEquals(
//            polygon, deserialize(
//                bytes,
//                Polygon::class.java
//            )
//        )
//    }

    @Test
    fun fromJson() {
        val json = "{\"type\": \"Polygon\", " +
                "\"coordinates\": [[[100, 0], [101, 0], [101, 1], [100, 1],[100, 0]]]}"
        val geo = Polygon.fromJson(json)
//        assertEquals("Polygon", geo.type)
        assertEquals(100.0, geo.coordinates.first().first().longitude, DELTA)
        assertEquals(0.0, geo.coordinates.first().first().latitude, DELTA)
        assertNull(geo.coordinates.first().first().altitude)
    }

    @Test
    fun fromJsonHoles() {
        val json = "{\"type\": \"Polygon\", " +
                "\"coordinates\": [[[100, 0], [101, 0], [101, 1], [100, 1],[100, 0]], " +
                " [[100.8, 0.8],[100.8, 0.2],[100.2, 0.2],[100.2, 0.8],[100.8, 0.8]]]}"
        val geo: Polygon = Polygon.fromJson(json)
//        assertEquals("Polygon", geo.type)
        assertEquals(100.0, geo.coordinates.first().first().longitude, DELTA)
        assertEquals(0.0, geo.coordinates.first().first().latitude, DELTA)
        assertEquals(2, geo.coordinates.size)
        assertEquals(100.8, geo.coordinates[1].first().longitude, DELTA)
        assertEquals(0.8, geo.coordinates[1].first().latitude, DELTA)
        assertNull(geo.coordinates.first().first().altitude)
    }

    @Test
    fun toJson() {
        val json = "{\"type\": \"Polygon\", " +
                "\"coordinates\": [[[100, 0], [101, 0], [101, 1], [100, 1],[100, 0]]]}"
        val geo: Polygon = Polygon.fromJson(json)
        compareJson(json, geo.toJson())
    }

    @Test
    fun toJsonHoles() {
        val json = "{\"type\": \"Polygon\", " +
                "\"coordinates\": [[[100, 0], [101, 0], [101, 1], [100, 1],[100, 0]], " +
                " [[100.8, 0.8],[100.8, 0.2],[100.2, 0.2],[100.2, 0.8],[100.8, 0.8]]]}"
        val geo: Polygon = Polygon.fromJson(json)
        compareJson(json, geo.toJson())
    }

    @Test
    fun fromJson_coordinatesPresent() {
        assertThrows(SerializationException::class.java) {
            Polygon.fromJson("{\"type\":\"Polygon\",\"coordinates\":null}")
        }
    }
}
