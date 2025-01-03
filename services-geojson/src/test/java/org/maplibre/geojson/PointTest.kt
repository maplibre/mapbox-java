package org.maplibre.geojson

import kotlinx.serialization.SerializationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test
import org.maplibre.geojson.TestUtils.DELTA
import org.maplibre.geojson.TestUtils.compareJson

class PointTest {

    @Test
    fun sanity() {
        val point = Point(1.0, 2.0)
        assertNotNull(point)
    }

    @Test
    fun altitude_returnsIsOptional() {
        val point = Point(1.0, 2.0)
        assertNull(point.altitude)
    }

    //TODO fabi755, add when array constructor exists, or remove unused tests
//    @Test
//    fun altitude_doesReturnCorrectValueFromDoubleArray() {
//        val coords = doubleArrayOf(1.0, 2.0, 5.0)
//        val point = Point(1.0, 2.0, 5.0)
//        assertEquals(5, point.altitude, DELTA)
//    }
//
//    @Test
//    fun point_isNullWithWrongLengthDoubleArray() {
//        val coords = doubleArrayOf(1.0)
//        val point = Point(coords)
//        assertNull(point)
//    }

    @Test
    fun longitude_doesReturnCorrectValue() {
        val point = Point(1.0, 2.0, 5.0)
        assertEquals(1.0, point.longitude, DELTA)
    }

    @Test
    fun latitude_doesReturnCorrectValue() {
        val point = Point(1.0, 2.0, 5.0)
        assertEquals(2.0, point.latitude, DELTA)
    }

    @Test
    fun bbox_nullWhenNotSet() {
        val point = Point(1.0, 2.0)
        assertNull(point.bbox)
    }

    @Test
    fun bbox_doesSerializeWhenNotPresent() {
        val point = Point(1.0, 2.0)
        compareJson(
            point.toJson(),
            "{\"type\":\"Point\",\"coordinates\":[1.0, 2.0]}"
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
        val bbox = BoundingBox(1.0, 2.0, 3.0, 4.0)
        val point = Point(2.0, 2.0, bbox = bbox)
        compareJson(
            point.toJson(),
            "{\"coordinates\": [2,2],"
                    + "\"type\":\"Point\",\"bbox\":[1.0,2.0,3.0,4.0]}"
        )
    }

    @Test
    fun bbox_doesDeserializeWhenPresent() {
        val point: Point = Point.fromJson(
            "{\"coordinates\": [2,3],"
                    + "\"type\":\"Point\",\"bbox\":[1.0,2.0,3.0,4.0]}"
        )

        assertNotNull(point)
        assertNotNull(point.bbox)
        assertEquals(1.0, point.bbox!!.southwest.longitude, DELTA)
        assertEquals(2.0, point.bbox!!.southwest.latitude, DELTA)
        assertEquals(3.0, point.bbox!!.northeast.longitude, DELTA)
        assertEquals(4.0, point.bbox!!.northeast.latitude, DELTA)
        assertNotNull(point.coordinates)
        assertEquals(2.0, point.longitude, DELTA)
        assertEquals(3.0, point.latitude, DELTA)
    }

    //TODO
//    @Test
//    fun testSerializable() {
//        val points: MutableList<Point> = ArrayList()
//        points.add(Point(1.0, 1.0))
//        points.add(Point(2.0, 2.0))
//        points.add(Point(3.0, 3.0))
//        val bbox: BoundingBox = BoundingBox(1.0, 2.0, 3.0, 4.0)
//        val lineString: LineString = LineString(points, bbox)
//        val bytes = serialize(lineString)
//        assertEquals(
//            lineString, deserialize(
//                bytes,
//                LineString::class.java
//            )
//        )
//    }

    @Test
    fun fromJson() {
        val json =
            "{ \"type\": \"Point\", \"coordinates\": [ 100, 0] }"
        val geo: Point = Point.fromJson(json)
//        assertEquals(geo.type, "Point")
        assertEquals(geo.longitude, 100.0, DELTA)
        assertEquals(geo.latitude, 0.0, DELTA)
        assertNull(geo.altitude)
        assertEquals(geo.coordinates.first(), 100.0, DELTA)
        assertEquals(geo.coordinates[1], 0.0, DELTA)
        assertEquals(geo.coordinates.size, 2)
    }

    @Test
    fun toJson() {
        val json =
            "{ \"type\": \"Point\", \"coordinates\": [ 100, 0] }"
        val geo: Point = Point.fromJson(json)
        compareJson(json, geo.toJson())
    }

    @Test
    @Throws(Exception::class)
    fun fromJson_coordinatesPresent() {
        assertThrows(SerializationException::class.java) {
            Point.fromJson("{\"type\":\"Point\",\"coordinates\":null}")
        }
    }
}
