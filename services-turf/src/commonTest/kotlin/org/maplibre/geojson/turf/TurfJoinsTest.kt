package org.maplibre.geojson.turf

import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.Test
import org.maplibre.geojson.model.Feature
import org.maplibre.geojson.model.FeatureCollection
import org.maplibre.geojson.model.MultiPolygon
import org.maplibre.geojson.model.Point
import org.maplibre.geojson.model.Polygon
import org.maplibre.geojson.turf.TestUtils.loadJsonFixture
import org.maplibre.geojson.turf.TurfJoins.inside
import org.maplibre.geojson.turf.TurfJoins.pointsWithinPolygon

class TurfJoinsTest {

    @Test
    fun testFeatureCollection() {
        // Test for a simple polygon
        val coordinates1 = listOf(
            listOf(

                Point(0.0, 0.0),
                Point(0.0, 100.0),
                Point(100.0, 100.0),
                Point(100.0, 0.0),
                Point(0.0, 0.0),
            )
        )
        val poly = Polygon(coordinates1)

        var ptIn = Point(50.0, 50.0)
        var ptOut = Point(140.0, 150.0)

        assertTrue(inside(ptIn, poly))
        assertFalse(inside(ptOut, poly))

        // Test for a concave polygon
        val coordinates2 = listOf(
            listOf(
                Point(0.0, 0.0),
                Point(50.0, 50.0),
                Point(0.0, 100.0),
                Point(100.0, 100.0),
                Point(100.0, 0.0),
                Point(0.0, 0.0),
            )
        )
        val concavePoly = Polygon(coordinates2)

        ptIn = Point(75.0, 75.0)
        ptOut = Point(25.0, 50.0)

        assertTrue(inside(ptIn, concavePoly))
        assertFalse(inside(ptOut, concavePoly))
    }

    @Test
    fun testPolyWithHole() {
        val ptInHole = Point(-86.69208526611328, 36.20373274711739)
        val ptInPoly = Point(-86.72229766845702, 36.20258997094334)
        val ptOutsidePoly = Point(-86.75079345703125, 36.18527313913089)
        val polyHole = Feature.fromJson(loadJsonFixture(POLY_WITH_HOLE_FIXTURE))

        assertFalse(
            inside(ptInHole, (polyHole.geometry as Polygon))
        )
        assertTrue(
            inside(ptInPoly, (polyHole.geometry as Polygon))
        )
        assertFalse(
            inside(ptOutsidePoly, (polyHole.geometry as Polygon))
        )
    }

    @Test
    fun testMultipolygonWithHole() {
        val ptInHole = Point(-86.69208526611328, 36.20373274711739)
        val ptInPoly = Point(-86.72229766845702, 36.20258997094334)
        val ptInPoly2 = Point(-86.75079345703125, 36.18527313913089)
        val ptOutsidePoly = Point(-86.75302505493164, 36.23015046460186)

        val multiPolyHole = Feature.fromJson(
            loadJsonFixture(
                MULTIPOLY_WITH_HOLE_FIXTURE
            )
        )
        assertFalse(
            inside(ptInHole, (multiPolyHole.geometry as MultiPolygon))
        )
        assertTrue(
            inside(ptInPoly, (multiPolyHole.geometry as MultiPolygon))
        )
        assertTrue(
            inside(ptInPoly2, (multiPolyHole.geometry as MultiPolygon))
        )
        assertFalse(
            inside(ptOutsidePoly, (multiPolyHole.geometry as MultiPolygon))
        )
    }

    @Test
    fun testInputPositions() {
        val ptInPoly = Point(-86.72229766845702, 36.20258997094334)
        val ptOutsidePoly = Point(-86.75079345703125, 36.18527313913089)
        val polyHole = Feature.fromJson(loadJsonFixture(POLY_WITH_HOLE_FIXTURE))

        val polygon = polyHole.geometry as Polygon

        assertTrue(inside(ptInPoly, polygon))
        assertFalse(inside(ptOutsidePoly, polygon))
    }

    @Test
    fun testWithin() {
        // Test with a single point
        val poly = Polygon(
            listOf(
                listOf(
                    Point(0.0, 0.0),
                    Point(0.0, 100.0),
                    Point(100.0, 100.0),
                    Point(100.0, 0.0),
                    Point(0.0, 0.0),
                )
            )
        )

        val pt = Point(50.0, 50.0)

        val features1 = ArrayList<Feature>()
        features1.add(Feature(poly))
        var polyFeatureCollection = FeatureCollection(features1)

        val features2 = ArrayList<Feature>()
        features2.add(Feature(pt))
        var ptFeatureCollection = FeatureCollection(features2)

        var counted = pointsWithinPolygon(ptFeatureCollection, polyFeatureCollection)
        assertNotNull(counted)
        assertEquals(counted.features.size.toLong(), 1) // 1 point in 1 polygon

        // test with multiple points and multiple polygons
        val poly1 = Polygon(
            listOf(
                listOf(
                    Point(0.0, 0.0),
                    Point(10.0, 0.0),
                    Point(10.0, 10.0),
                    Point(0.0, 10.0),
                    Point(0.0, 0.0)
                )
            )
        )

        val poly2 = Polygon(
            listOf(
                listOf(
                    Point(10.0, 0.0),
                    Point(20.0, 10.0),
                    Point(20.0, 20.0),
                    Point(20.0, 0.0),
                    Point(10.0, 0.0)
                )
            )
        )

        polyFeatureCollection = FeatureCollection(
            listOf(
                Feature(poly1),
                Feature(poly2)
            )
        )


        ptFeatureCollection = FeatureCollection(
            listOf(
                Point(1.0, 1.0),
                Point(1.0, 3.0),
                Point(14.0, 2.0),
                Point(13.0, 1.0),
                Point(19.0, 7.0),
                Point(100.0, 7.0)
            ).map(::Feature)
        )

        counted = pointsWithinPolygon(ptFeatureCollection, polyFeatureCollection)
        assertNotNull(counted)
        assertEquals(
            counted.features.size.toLong(),
            5
        ) // multiple points in multiple polygons
    }

    companion object {
        private const val POLY_WITH_HOLE_FIXTURE = "turf-inside/poly-with-hole.geojson"
        private const val MULTIPOLY_WITH_HOLE_FIXTURE = "turf-inside/multipoly-with-hole.geojson"
    }
}
