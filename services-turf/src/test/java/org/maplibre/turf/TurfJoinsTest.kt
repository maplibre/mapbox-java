package org.maplibre.turf

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.MultiPolygon
import org.maplibre.geojson.Point
import org.maplibre.geojson.Polygon
import org.maplibre.turf.TestUtils.loadJsonFixture
import org.maplibre.turf.TurfJoins.inside
import org.maplibre.turf.TurfJoins.pointsWithinPolygon

class TurfJoinsTest {

    @Test
    fun testFeatureCollection() {
        // Test for a simple polygon
        val coordinates1 = listOf(
            listOf(

                Point.fromLngLat(0.0, 0.0),
                Point.fromLngLat(0.0, 100.0),
                Point.fromLngLat(100.0, 100.0),
                Point.fromLngLat(100.0, 0.0),
                Point.fromLngLat(0.0, 0.0),
            )
        )
        val poly = Polygon.fromLngLats(coordinates1)

        var ptIn = Point.fromLngLat(50.0, 50.0)
        var ptOut = Point.fromLngLat(140.0, 150.0)

        assertTrue(inside(ptIn, poly))
        assertFalse(inside(ptOut, poly))

        // Test for a concave polygon
        val coordinates2 = listOf(
            listOf(
                Point.fromLngLat(0.0, 0.0),
                Point.fromLngLat(50.0, 50.0),
                Point.fromLngLat(0.0, 100.0),
                Point.fromLngLat(100.0, 100.0),
                Point.fromLngLat(100.0, 0.0),
                Point.fromLngLat(0.0, 0.0),
            )
        )
        val concavePoly = Polygon.fromLngLats(coordinates2)

        ptIn = Point.fromLngLat(75.0, 75.0)
        ptOut = Point.fromLngLat(25.0, 50.0)

        assertTrue(inside(ptIn, concavePoly))
        assertFalse(inside(ptOut, concavePoly))
    }

    @Test
    fun testPolyWithHole() {
        val ptInHole = Point.fromLngLat(-86.69208526611328, 36.20373274711739)
        val ptInPoly = Point.fromLngLat(-86.72229766845702, 36.20258997094334)
        val ptOutsidePoly = Point.fromLngLat(-86.75079345703125, 36.18527313913089)
        val polyHole = Feature.fromJson(loadJsonFixture(POLY_WITH_HOLE_FIXTURE))

        assertFalse(
            inside(ptInHole, (polyHole.geometry() as Polygon))
        )
        assertTrue(
            inside(ptInPoly, (polyHole.geometry() as Polygon))
        )
        assertFalse(
            inside(ptOutsidePoly, (polyHole.geometry() as Polygon))
        )
    }

    @Test
    fun testMultipolygonWithHole() {
        val ptInHole = Point.fromLngLat(-86.69208526611328, 36.20373274711739)
        val ptInPoly = Point.fromLngLat(-86.72229766845702, 36.20258997094334)
        val ptInPoly2 = Point.fromLngLat(-86.75079345703125, 36.18527313913089)
        val ptOutsidePoly = Point.fromLngLat(-86.75302505493164, 36.23015046460186)

        val multiPolyHole = Feature.fromJson(
            loadJsonFixture(
                MULTIPOLY_WITH_HOLE_FIXTURE
            )
        )
        assertFalse(
            inside(ptInHole, (multiPolyHole.geometry() as MultiPolygon))
        )
        assertTrue(
            inside(ptInPoly, (multiPolyHole.geometry() as MultiPolygon))
        )
        assertTrue(
            inside(ptInPoly2, (multiPolyHole.geometry() as MultiPolygon))
        )
        assertFalse(
            inside(ptOutsidePoly, (multiPolyHole.geometry() as MultiPolygon))
        )
    }

    @Test
    fun testInputPositions() {
        val ptInPoly = Point.fromLngLat(-86.72229766845702, 36.20258997094334)
        val ptOutsidePoly = Point.fromLngLat(-86.75079345703125, 36.18527313913089)
        val polyHole = Feature.fromJson(loadJsonFixture(POLY_WITH_HOLE_FIXTURE))

        val polygon = polyHole.geometry() as Polygon

        assertTrue(inside(ptInPoly, polygon))
        assertFalse(inside(ptOutsidePoly, polygon))
    }

    @Test
    fun testWithin() {
        // Test with a single point
        val poly = Polygon.fromLngLats(
            listOf(
                listOf(
                    Point.fromLngLat(0.0, 0.0),
                    Point.fromLngLat(0.0, 100.0),
                    Point.fromLngLat(100.0, 100.0),
                    Point.fromLngLat(100.0, 0.0),
                    Point.fromLngLat(0.0, 0.0),
                )
            )
        )

        val pt = Point.fromLngLat(50.0, 50.0)

        val features1 = ArrayList<Feature>()
        features1.add(Feature.fromGeometry(poly))
        var polyFeatureCollection = FeatureCollection.fromFeatures(features1)

        val features2 = ArrayList<Feature>()
        features2.add(Feature.fromGeometry(pt))
        var ptFeatureCollection = FeatureCollection.fromFeatures(features2)

        var counted = pointsWithinPolygon(ptFeatureCollection, polyFeatureCollection)
        assertNotNull(counted)
        assertEquals(counted.features()!!.size.toLong(), 1) // 1 point in 1 polygon

        // test with multiple points and multiple polygons
        val poly1 = Polygon.fromLngLats(
            listOf(
                listOf(
                    Point.fromLngLat(0.0, 0.0),
                    Point.fromLngLat(10.0, 0.0),
                    Point.fromLngLat(10.0, 10.0),
                    Point.fromLngLat(0.0, 10.0),
                    Point.fromLngLat(0.0, 0.0)
                )
            )
        )

        val poly2 = Polygon.fromLngLats(
            listOf(
                listOf(
                    Point.fromLngLat(10.0, 0.0),
                    Point.fromLngLat(20.0, 10.0),
                    Point.fromLngLat(20.0, 20.0),
                    Point.fromLngLat(20.0, 0.0),
                    Point.fromLngLat(10.0, 0.0)
                )
            )
        )

        polyFeatureCollection = FeatureCollection.fromFeatures(
            listOf(
                Feature.fromGeometry(poly1),
                Feature.fromGeometry(poly2)
            )
        )


        ptFeatureCollection = FeatureCollection.fromFeatures(
            listOf(
                Point.fromLngLat(1.0, 1.0),
                Point.fromLngLat(1.0, 3.0),
                Point.fromLngLat(14.0, 2.0),
                Point.fromLngLat(13.0, 1.0),
                Point.fromLngLat(19.0, 7.0),
                Point.fromLngLat(100.0, 7.0)
            ).map(Feature::fromGeometry)
        )

        counted = pointsWithinPolygon(ptFeatureCollection, polyFeatureCollection)
        assertNotNull(counted)
        assertEquals(
            counted.features()!!.size.toLong(),
            5
        ) // multiple points in multiple polygons
    }

    companion object {
        private const val POLY_WITH_HOLE_FIXTURE = "turf-inside/poly-with-hole.geojson"
        private const val MULTIPOLY_WITH_HOLE_FIXTURE = "turf-inside/multipoly-with-hole.geojson"
    }
}
