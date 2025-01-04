package org.maplibre.geojson

import kotlinx.serialization.SerializationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test
import org.maplibre.geojson.TestUtils.DELTA
import org.maplibre.geojson.TestUtils.compareJson

class MultiLineStringTest {

    @Test
    fun sanity() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )

        val lineStrings = listOf(
            LineString(points),
            LineString(points)
        )

        val multiLineString = MultiLineString.fromLineStrings(lineStrings)
        assertNotNull(multiLineString)
    }

    @Test
    fun bbox_nullWhenNotSet() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )

        val lineStrings = listOf(
            LineString(points),
            LineString(points)
        )

        val multiLineString = MultiLineString.fromLineStrings(lineStrings)
        assertNull(multiLineString.bbox)
    }

    @Test
    fun bbox_doesNotSerializeWhenNotPresent() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )

        val lineStrings = listOf(
            LineString(points),
            LineString(points)
        )

        val multiLineString: MultiLineString = MultiLineString.fromLineStrings(lineStrings)
        compareJson(
            multiLineString.toJson(),
            "{\"type\":\"MultiLineString\",\"coordinates\":[[[1,2],[2,3]],[[1,2],[2,3]]]}"
        )
    }

    @Test
    fun bbox_returnsCorrectBbox() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )

        val bbox = BoundingBox(1.0, 2.0, 3.0, 4.0)

        val lineStrings = listOf(
            LineString(points),
            LineString(points)
        )

        val multiLineString: MultiLineString = MultiLineString.fromLineStrings(lineStrings, bbox)
        assertNotNull(multiLineString.bbox)
        assertEquals(1.0, multiLineString.bbox!!.west, DELTA)
        assertEquals(2.0, multiLineString.bbox!!.south, DELTA)
        assertEquals(3.0, multiLineString.bbox!!.east, DELTA)
        assertEquals(4.0, multiLineString.bbox!!.north, DELTA)
    }

    @Test
    fun passingInSingleLineString_doesHandleCorrectly() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(3.0, 4.0)
        )

        val geometry = LineString(points)
        val multiLineString: MultiLineString = MultiLineString.fromLineString(geometry)

        assertNotNull(multiLineString)
        assertEquals(1, multiLineString.lineStrings.size)
        assertEquals(
            2.0,
            multiLineString.lineStrings[0].coordinates[0].latitude,
            DELTA
        )
    }

    @Test
    fun bbox_doesSerializeWhenPresent() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )
        val bbox = BoundingBox(1.0, 2.0, 3.0, 4.0)

        val lineStrings = listOf(
            LineString(points),
            LineString(points)
        )

        val multiLineString: MultiLineString = MultiLineString.fromLineStrings(lineStrings, bbox)
        compareJson(
            multiLineString.toJson(),
            "{\"type\":\"MultiLineString\",\"bbox\":[1.0,2.0,3.0,4.0],"
                    + "\"coordinates\":[[[1,2],[2,3]],[[1,2],[2,3]]]}"
        )
    }

    @Test
    fun fromJson() {
        val json = "{\"type\": \"MultiLineString\", " +
                "\"coordinates\": [[[100, 0],[101, 1]],[[102, 2],[103, 3]]] }"

        val geo: MultiLineString = MultiLineString.fromJson(json)
        assertEquals(geo.coordinates[0][0].longitude, 100.0, DELTA)
        assertEquals(geo.coordinates[0][0].latitude, 0.0, DELTA)
        assertNull(geo.coordinates[0][0].altitude)
    }

    @Test
    fun toJson() {
        val json = "{\"type\": \"MultiLineString\", " +
                "\"coordinates\": [[[100, 0],[101, 1]],[[102, 2],[103, 3]]] }"
        val geo = MultiLineString.fromJson(json)
        compareJson(json, geo.toJson())
    }

    @Test
    fun fromJson_coordinatesPresent() {
        assertThrows(SerializationException::class.java) {
            MultiLineString.fromJson("{\"type\":\"MultiLineString\",\"coordinates\":null}")
        }
    }
}
