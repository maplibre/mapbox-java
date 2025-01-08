package org.maplibre.geojson

import org.maplibre.geojson.TestUtils.DELTA
import org.maplibre.geojson.model.BoundingBox
import org.maplibre.geojson.model.Point
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class BoundingBoxTest {

    @Test
    @Throws(Exception::class)
    fun sanity() {
        val southwest = Point(2.0, 2.0)
        val northeast = Point(4.0, 4.0)
        val boundingBox = BoundingBox(southwest, northeast)
        assertNotNull(boundingBox)
    }

    @Test
    @Throws(Exception::class)
    fun southWest_doesReturnMostSouthwestCoordinate() {
        val southwest = Point(1.0, 2.0)
        val northeast = Point(3.0, 4.0)
        val boundingBox = BoundingBox(southwest, northeast)
        assertEquals(southwest, boundingBox.southwest)
    }

    @Test
    @Throws(Exception::class)
    fun northEast_doesReturnMostNortheastCoordinate() {
        val southwest = Point(1.0, 2.0)
        val northeast = Point(3.0, 4.0)
        val boundingBox = BoundingBox(southwest, northeast)
        assertEquals(northeast, boundingBox.northeast)
    }

    @Test
    @Throws(Exception::class)
    fun west_doesReturnMostWestCoordinate() {
        val southwest = Point(1.0, 2.0)
        val northeast = Point(3.0, 4.0)
        val boundingBox = BoundingBox(southwest, northeast)
        assertEquals(1.0, boundingBox.west, DELTA)
    }

    @Test
    @Throws(Exception::class)
    fun south_doesReturnMostSouthCoordinate() {
        val southwest = Point(1.0, 2.0)
        val northeast = Point(3.0, 4.0)
        val boundingBox = BoundingBox(southwest, northeast)
        assertEquals(2.0, boundingBox.south, DELTA)
    }

    @Test
    @Throws(Exception::class)
    fun east_doesReturnMostEastCoordinate() {
        val southwest = Point(1.0, 2.0)
        val northeast = Point(3.0, 4.0)
        val boundingBox = BoundingBox(southwest, northeast)
        assertEquals(3.0, boundingBox.east, DELTA)
    }

    @Test
    @Throws(Exception::class)
    fun north_doesReturnMostNorthCoordinate() {
        val southwest = Point(1.0, 2.0)
        val northeast = Point(3.0, 4.0)
        val boundingBox = BoundingBox(southwest, northeast)
        assertEquals(4.0, boundingBox.north, DELTA)
    }
}
