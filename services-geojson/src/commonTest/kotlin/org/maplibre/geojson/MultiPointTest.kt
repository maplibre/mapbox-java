package org.maplibre.geojson

import kotlinx.serialization.SerializationException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.Test
import org.maplibre.geojson.TestUtils.DELTA
import org.maplibre.geojson.TestUtils.compareJson
import org.maplibre.geojson.model.BoundingBox
import org.maplibre.geojson.model.MultiPoint
import org.maplibre.geojson.model.Point
import kotlin.test.assertFailsWith

class MultiPointTest {

    @Test
    fun sanity() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )

        val multiPoint = MultiPoint(points)
        assertNotNull(multiPoint)
    }

    @Test
    fun bbox_nullWhenNotSet() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )

        val multiPoint = MultiPoint(points)
        assertNull(multiPoint.bbox)
    }

    @Test
    fun bbox_doesNotSerializeWhenNotPresent() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )

        val multiPoint = MultiPoint(points)
        compareJson(
            multiPoint.toJson(),
            "{\"coordinates\":[[1.0,2.0],[2.0,3.0]],\"type\":\"MultiPoint\"}"
        )
    }

    @Test
    fun bbox_returnsCorrectBbox() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )

        val bbox = BoundingBox(1.0, 2.0, 3.0, 4.0)
        val multiPoint = MultiPoint(points, bbox)
        assertNotNull(multiPoint.bbox)
        assertEquals(1.0, multiPoint.bbox!!.west, DELTA)
        assertEquals(2.0, multiPoint.bbox!!.south, DELTA)
        assertEquals(3.0, multiPoint.bbox!!.east, DELTA)
        assertEquals(4.0, multiPoint.bbox!!.north, DELTA)
    }

    @Test
    fun bbox_doesSerializeWhenPresent() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )

        val bbox = BoundingBox(1.0, 2.0, 3.0, 4.0)
        val multiPoint = MultiPoint(points, bbox)
        compareJson(
            multiPoint.toJson(),
            "{\"coordinates\":[[1.0,2.0],[2.0,3.0]],\"type\":\"MultiPoint\",\"bbox\":[1.0,2.0,3.0,4.0]}"
        )
    }

    @Test
    fun fromJson() {
        val json = ("{ \"type\": \"MultiPoint\","
                + "\"coordinates\": [ [100, 0], [101, 1] ] } ")
        val geo: MultiPoint = MultiPoint.fromJson(json)
        assertEquals(geo.coordinates.first().longitude, 100.0, DELTA)
        assertEquals(geo.coordinates.first().latitude, 0.0, DELTA)
        assertEquals(geo.coordinates[1].longitude, 101.0, DELTA)
        assertEquals(geo.coordinates[1].latitude, 1.0, DELTA)
        assertNull(geo.coordinates.first().altitude)
    }

    @Test
    fun toJson() {
        val json = ("{ \"type\": \"MultiPoint\","
                + "\"coordinates\": [ [100.0, 0.0], [101.0, 1.0] ] } ")
        val geo: MultiPoint = MultiPoint.fromJson(json)
        compareJson(json, geo.toJson())
    }

    @Test
    fun fromJson_coordinatesPresent() {
        assertFailsWith(SerializationException::class) {
            MultiPoint.fromJson("{\"type\":\"MultiPoint\",\"coordinates\":null}")
        }
    }
}
