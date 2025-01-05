package org.maplibre.turf

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Test
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.GeometryCollection
import org.maplibre.geojson.LineString
import org.maplibre.geojson.MultiLineString
import org.maplibre.geojson.MultiPoint
import org.maplibre.geojson.MultiPolygon
import org.maplibre.geojson.Point
import org.maplibre.geojson.Polygon
import org.maplibre.turf.TestUtils.DELTA
import org.maplibre.turf.TestUtils.compareJson
import org.maplibre.turf.TestUtils.loadJsonFixture
import org.maplibre.turf.TurfConversion.combine
import org.maplibre.turf.TurfConversion.convertLength
import org.maplibre.turf.TurfConversion.explode
import org.maplibre.turf.TurfConversion.lengthToDegrees
import org.maplibre.turf.TurfConversion.lengthToRadians
import org.maplibre.turf.TurfConversion.multiPolygonToLine
import org.maplibre.turf.TurfConversion.polygonToLine
import org.maplibre.turf.TurfConversion.radiansToLength

class TurfConversionTest {

    @Test
    fun radiansToDistance() {
        assertEquals(
            1.0, radiansToLength(1.0, TurfUnit.RADIANS), DELTA
        )
        assertEquals(
            6373.0, radiansToLength(1.0, TurfUnit.KILOMETERS), DELTA
        )
        assertEquals(
            3960.0, radiansToLength(1.0, TurfUnit.MILES), DELTA
        )
    }

    @Test
    fun distanceToRadians() {
        assertEquals(
            1.0, lengthToRadians(1.0, TurfUnit.RADIANS), DELTA
        )
        assertEquals(
            1.0, lengthToRadians(6373.0, TurfUnit.KILOMETERS), DELTA
        )
        assertEquals(
            1.0, lengthToRadians(3960.0, TurfUnit.MILES), DELTA
        )
    }

    @Test
    fun distanceToDegrees() {
        assertEquals(
            57.29577951308232, lengthToDegrees(1.0, TurfUnit.RADIANS), DELTA
        )
        assertEquals(
            0.8990393772647469, lengthToDegrees(
                100.0,
                TurfUnit.KILOMETERS
            ), DELTA
        )
        assertEquals(
            0.14468631190172304, lengthToDegrees(10.0, TurfUnit.MILES), DELTA
        )
    }

    @Test
    fun convertDistance() {
        assertEquals(
            1.0,
            convertLength(1000.0, TurfUnit.METERS), DELTA
        )
        assertEquals(
            0.6213714106386318,
            convertLength(
                1.0, TurfUnit.KILOMETERS,
                TurfUnit.MILES
            ), DELTA
        )
        assertEquals(
            1.6093434343434343,
            convertLength(
                1.0, TurfUnit.MILES,
                TurfUnit.KILOMETERS
            ), DELTA
        )
        assertEquals(
            1.851999843075488,
            convertLength(1.0, TurfUnit.NAUTICAL_MILES), DELTA
        )
        assertEquals(
            100.0,
            convertLength(
                1.0, TurfUnit.METERS,
                TurfUnit.CENTIMETERS
            ), DELTA
        )
    }


    @Test
    fun combinePointsToMultiPoint() {
        val pointFeatureCollection =
            FeatureCollection(
                listOf(
                    Feature(Point(-2.46, 27.6835)),
                    Feature(Point(41.83, 7.3624))
                )
            )

        val featureCollectionWithNewMultiPointObject = combine(pointFeatureCollection)
        assertNotNull(featureCollectionWithNewMultiPointObject)

        val multiPoint =
            featureCollectionWithNewMultiPointObject.features[0].geometry as MultiPoint?
        assertNotNull(multiPoint)

        assertEquals(-2.46, multiPoint!!.coordinates[0].longitude, DELTA)
        assertEquals(27.6835, multiPoint.coordinates[0].latitude, DELTA)
        assertEquals(41.83, multiPoint.coordinates[1].longitude, DELTA)
        assertEquals(7.3624, multiPoint.coordinates[1].latitude, DELTA)
    }

    @Test
    fun combinePointAndMultiPointToMultiPoint() {
        val pointAndMultiPointFeatureCollection =
            FeatureCollection(
                listOf(
                    Feature(Point(-2.46, 27.6835)),
                    Feature(
                        MultiPoint(
                            listOf(
                                Point(41.83, 7.3624),
                                Point(100.0, 101.0)
                            )
                        )
                    )
                )
            )

        val combinedFeatureCollection =
            combine(pointAndMultiPointFeatureCollection)

        assertNotNull(combinedFeatureCollection)

        val multiPoint = combinedFeatureCollection.features[0].geometry as MultiPoint?
        assertNotNull(multiPoint)

        assertEquals(-2.46, multiPoint!!.coordinates[0].longitude, DELTA)
        assertEquals(27.6835, multiPoint.coordinates[0].latitude, DELTA)
        assertEquals(41.83, multiPoint.coordinates[1].longitude, DELTA)
        assertEquals(7.3624, multiPoint.coordinates[1].latitude, DELTA)
        assertEquals(100.0, multiPoint.coordinates[2].longitude, DELTA)
        assertEquals(101.0, multiPoint.coordinates[2].latitude, DELTA)
    }

    @Test
    fun combineTwoLineStringsToMultiLineString() {
        val lineStringFeatureCollection =
            FeatureCollection(
                listOf(
                    Feature(
                        LineString(
                            listOf(
                                Point(-11.25, 55.7765),
                                Point(41.1328, 22.91792)
                            )
                        )
                    ),
                    Feature(
                        LineString(
                            listOf(
                                Point(3.8671, 19.3111),
                                Point(20.742, -20.3034)
                            )
                        )
                    )
                )
            )

        val featureCollectionWithNewMultiLineStringObject = combine(lineStringFeatureCollection)
        assertNotNull(featureCollectionWithNewMultiLineStringObject)

        val multiLineString =
            featureCollectionWithNewMultiLineStringObject.features[0].geometry as MultiLineString?
        assertNotNull(multiLineString)

        // Checking the first LineString in the MultiLineString
        assertEquals(-11.25, multiLineString!!.coordinates[0][0].longitude, DELTA)
        assertEquals(55.7765, multiLineString.coordinates[0][0].latitude, DELTA)

        // Checking the second LineString in the MultiLineString
        assertEquals(41.1328, multiLineString.coordinates[0][1].longitude, DELTA)
        assertEquals(22.91792, multiLineString.coordinates[0][1].latitude, DELTA)
    }

    @Test
    fun combineLineStringAndMultiLineStringToMultiLineString() {
        val lineStringFeatureCollection =
            FeatureCollection(
                listOf(
                    Feature(
                        LineString(
                            listOf(
                                Point(-11.25, 55.7765),
                                Point(41.1328, 22.91792)
                            )
                        )
                    ),
                    Feature(
                        MultiLineString.fromLineStrings(
                            listOf(
                                LineString(
                                    listOf(
                                        Point(102.0, -10.0),
                                        Point(130.0, 4.0)
                                    )
                                ),
                                LineString(
                                    listOf(
                                        Point(40.0, -20.0),
                                        Point(150.0, 18.0)
                                    )
                                )
                            )
                        )
                    )
                )
            )

        val featureCollectionWithNewMultiLineStringObject =
            combine(lineStringFeatureCollection)
        assertNotNull(featureCollectionWithNewMultiLineStringObject)

        val multiLineString = featureCollectionWithNewMultiLineStringObject
            .features[0].geometry as MultiLineString?
        assertNotNull(multiLineString)

        // Checking the first LineString in the MultiLineString
        assertEquals(-11.25, multiLineString!!.coordinates[0][0].longitude, DELTA)
        assertEquals(55.7765, multiLineString.coordinates[0][0].latitude, DELTA)

        assertEquals(41.1328, multiLineString.coordinates[0][1].longitude, DELTA)
        assertEquals(22.91792, multiLineString.coordinates[0][1].latitude, DELTA)

        // Checking the second LineString in the MultiLineString
        assertEquals(102.0, multiLineString.coordinates[1][0].longitude, DELTA)
        assertEquals(-10.0, multiLineString.coordinates[1][0].latitude, DELTA)

        assertEquals(130.0, multiLineString.coordinates[1][1].longitude, DELTA)
        assertEquals(4.0, multiLineString.coordinates[1][1].latitude, DELTA)

        // Checking the third LineString in the MultiLineString
        assertEquals(40.0, multiLineString.coordinates[2][0].longitude, DELTA)
        assertEquals(-20.0, multiLineString.coordinates[2][0].latitude, DELTA)

        assertEquals(150.0, multiLineString.coordinates[2][1].longitude, DELTA)
        assertEquals(18.0, multiLineString.coordinates[2][1].latitude, DELTA)
    }

    @Test
    fun combinePolygonToMultiPolygon() {
        val polygonFeatureCollection =
            FeatureCollection(
                listOf(
                    Feature(
                        Polygon(
                            listOf(
                                listOf(
                                    Point(61.938950426660604, 5.9765625),
                                    Point(52.696361078274485, 33.046875),
                                    Point(69.90011762668541, 28.828124999999996),
                                    Point(61.938950426660604, 5.9765625)
                                )
                            )
                        )
                    ),
                    Feature(
                        Polygon(
                            listOf(
                                listOf(
                                    Point(11.42578125, 16.636191878397664),
                                    Point(7.91015625, -9.102096738726443),
                                    Point(31.113281249999996, 17.644022027872726),
                                    Point(11.42578125, 16.636191878397664)
                                )
                            )
                        )
                    )
                )
            )

        val featureCollectionWithNewMultiPolygonObject = combine(polygonFeatureCollection)
        assertNotNull(featureCollectionWithNewMultiPolygonObject)

        val multiPolygon =
            featureCollectionWithNewMultiPolygonObject.features[0].geometry as MultiPolygon?
        assertNotNull(multiPolygon)

        // Checking the first Polygon in the MultiPolygon

        // Checking the first Point
        assertEquals(
            61.938950426660604,
            multiPolygon!!.coordinates[0][0][0].longitude,
            DELTA
        )
        assertEquals(5.9765625, multiPolygon.coordinates[0][0][0].latitude, DELTA)

        // Checking the second Point
        assertEquals(
            52.696361078274485,
            multiPolygon.coordinates[0][0][1].longitude,
            DELTA
        )
        assertEquals(33.046875, multiPolygon.coordinates[0][0][1].latitude, DELTA)

        // Checking the second Polygon in the MultiPolygon

        // Checking the first Point
        assertEquals(11.42578125, multiPolygon.coordinates[1][0][0].longitude, DELTA)
        assertEquals(
            16.636191878397664,
            multiPolygon.coordinates[1][0][0].latitude,
            DELTA
        )

        // Checking the second Point
        assertEquals(7.91015625, multiPolygon.coordinates[1][0][1].longitude, DELTA)
        assertEquals(
            -9.102096738726443,
            multiPolygon.coordinates[1][0][1].latitude,
            DELTA
        )
    }

    @Test
    fun combinePolygonAndMultiPolygonToMultiPolygon() {
        val polygonFeatureCollection =
            FeatureCollection(
                listOf(
                    Feature(
                        Polygon(
                            listOf(
                                listOf(
                                    Point(61.938950426660604, 5.9765625),
                                    Point(52.696361078274485, 33.046875),
                                    Point(69.90011762668541, 28.828124999999996),
                                    Point(61.938950426660604, 5.9765625)
                                )
                            )
                        )
                    ),
                    Feature(
                        MultiPolygon.fromPolygons(
                            listOf(
                                Polygon(
                                    listOf(
                                        listOf(
                                            Point(11.42578125, 16.636191878397664),
                                            Point(7.91015625, -9.102096738726443),
                                            Point(
                                                31.113281249999996,
                                                17.644022027872726
                                            ),
                                            Point(11.42578125, 16.636191878397664)
                                        )
                                    )
                                ),
                                Polygon(
                                    listOf(
                                        listOf(
                                            Point(30.0, 0.0),
                                            Point(102.0, 0.0),
                                            Point(103.0, 1.0),
                                            Point(30.0, 0.0)
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )

        val combinedFeatureCollection = combine(polygonFeatureCollection)
        assertNotNull(combinedFeatureCollection)

        val multiPolygon = combinedFeatureCollection.features[0].geometry as MultiPolygon?
        assertNotNull(multiPolygon)

        // Checking the first Polygon in the MultiPolygon

        // Checking the first Point
        assertEquals(
            61.938950426660604,
            multiPolygon!!.coordinates[0][0][0].longitude,
            DELTA
        )
        assertEquals(5.9765625, multiPolygon.coordinates[0][0][0].latitude, DELTA)

        // Checking the second Point
        assertEquals(
            52.696361078274485,
            multiPolygon.coordinates[0][0][1].longitude,
            DELTA
        )
        assertEquals(33.046875, multiPolygon.coordinates[0][0][1].latitude, DELTA)

        // Checking the second Polygon in the MultiPolygon

        // Checking the first Point
        assertEquals(11.42578125, multiPolygon.coordinates[1][0][0].longitude, DELTA)
        assertEquals(
            16.636191878397664,
            multiPolygon.coordinates[1][0][0].latitude,
            DELTA
        )

        // Checking the second Point
        assertEquals(7.91015625, multiPolygon.coordinates[1][0][1].longitude, DELTA)
        assertEquals(
            -9.102096738726443,
            multiPolygon.coordinates[1][0][1].latitude,
            DELTA
        )

        // Checking the third Polygon in the MultiPolygon

        // Checking the first Point
        assertEquals(30.0, multiPolygon.coordinates[2][0][0].longitude, DELTA)
        assertEquals(0.0, multiPolygon.coordinates[2][0][0].latitude, DELTA)

        // Checking the second Point
        assertEquals(102.0, multiPolygon.coordinates[2][0][1].longitude, DELTA)
        assertEquals(0.0, multiPolygon.coordinates[2][0][1].latitude, DELTA)
    }

    @Test
    fun combinePolygonAndMultiPolygonAndPointToMultiPolygon() {
        val featureCollectionWithPointPolygonAndMultiPolygon =
            FeatureCollection(
                listOf(
                    Feature(
                        Point(-2.46, 27.6835)
                    ),
                    Feature(
                        Polygon(
                            listOf(
                                listOf(
                                    Point(61.938950426660604, 5.9765625),
                                    Point(52.696361078274485, 33.046875),
                                    Point(69.90011762668541, 28.828124999999996),
                                    Point(61.938950426660604, 5.9765625)
                                )
                            )
                        )
                    ),
                    Feature(
                        MultiPolygon.fromPolygons(
                            listOf(
                                Polygon(
                                    listOf(
                                        listOf(
                                            Point(11.42578125, 16.636191878397664),
                                            Point(7.91015625, -9.102096738726443),
                                            Point(
                                                31.113281249999996,
                                                17.644022027872726
                                            ),
                                            Point(11.42578125, 16.636191878397664)
                                        )
                                    )
                                ),
                                Polygon(
                                    listOf(
                                        listOf(
                                            Point(30.0, 0.0),
                                            Point(102.0, 0.0),
                                            Point(103.0, 1.0),
                                            Point(30.0, 0.0)
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )

        val combinedFeatureCollection = combine(featureCollectionWithPointPolygonAndMultiPolygon)
        assertNotNull(combinedFeatureCollection)
        var multiPolygon: MultiPolygon? = null
        var multiPoint: MultiPoint? = null
        for (x in combinedFeatureCollection.features.indices) {
            val singleFeature = combinedFeatureCollection.features[x]
            if (singleFeature.geometry is MultiPolygon) {
                multiPolygon = combinedFeatureCollection.features[x].geometry as MultiPolygon?
            }
            if (singleFeature.geometry is MultiPoint) {
                multiPoint = combinedFeatureCollection.features[x].geometry as MultiPoint?
            }
        }
        assertNotNull(multiPolygon)
        assertNotNull(multiPoint)

        // Checking the first Polygon in the MultiPolygon

        // Checking the first Point
        assertEquals(
            61.938950426660604,
            multiPolygon!!.coordinates[0][0][0].longitude,
            DELTA
        )
        assertEquals(5.9765625, multiPolygon.coordinates[0][0][0].latitude, DELTA)

        // Checking the second Point
        assertEquals(
            52.696361078274485,
            multiPolygon.coordinates[0][0][1].longitude,
            DELTA
        )
        assertEquals(33.046875, multiPolygon.coordinates[0][0][1].latitude, DELTA)

        // Checking the second Polygon in the MultiPolygon

        // Checking the first Point
        assertEquals(11.42578125, multiPolygon.coordinates[1][0][0].longitude, DELTA)
        assertEquals(
            16.636191878397664,
            multiPolygon.coordinates[1][0][0].latitude,
            DELTA
        )

        // Checking the second Point
        assertEquals(7.91015625, multiPolygon.coordinates[1][0][1].longitude, DELTA)
        assertEquals(
            -9.102096738726443,
            multiPolygon.coordinates[1][0][1].latitude,
            DELTA
        )

        // Checking the third Polygon in the MultiPolygon

        // Checking the first Point
        assertEquals(30.0, multiPolygon.coordinates[2][0][0].longitude, DELTA)
        assertEquals(0.0, multiPolygon.coordinates[2][0][0].latitude, DELTA)

        // Checking the second Point
        assertEquals(102.0, multiPolygon.coordinates[2][0][1].longitude, DELTA)
        assertEquals(0.0, multiPolygon.coordinates[2][0][1].latitude, DELTA)
    }

    @Test
    fun combinePointAndLineStringGeometry() {
        val pointAndLineStringFeatureCollection =
            FeatureCollection(
                listOf(
                    Feature(Point(-2.46, 27.6835)),
                    Feature(
                        LineString(
                            listOf(
                                Point(-11.25, 55.7765),
                                Point(41.1328, 22.91792)
                            )
                        )
                    )
                )
            )

        val combinedFeatureCollection = combine(pointAndLineStringFeatureCollection)
        assertNotNull(combinedFeatureCollection)
        var multiPoint: MultiPoint? = null
        var multiLineString: MultiLineString? = null
        for (x in combinedFeatureCollection.features.indices) {
            val singleFeature = combinedFeatureCollection.features[x]
            if (singleFeature.geometry is MultiPoint) {
                multiPoint = combinedFeatureCollection.features[x].geometry as MultiPoint?
            }
            if (singleFeature.geometry is MultiLineString) {
                multiLineString =
                    combinedFeatureCollection.features[x].geometry as MultiLineString?
            }
        }
        assertNotNull(multiPoint)
        assertNotNull(multiLineString)

        // Checking the LineString in the MultiLineString

        // Checking the first LineString location
        assertEquals(-11.25, multiLineString!!.coordinates[0][0].longitude, DELTA)
        assertEquals(55.7765, multiLineString.coordinates[0][0].latitude, DELTA)

        // Checking the second LineString location
        assertEquals(41.1328, multiLineString.coordinates[0][1].longitude, DELTA)
        assertEquals(22.91792, multiLineString.coordinates[0][1].latitude, DELTA)

        // Checking the Point in the MultiPoint

        // Checking the first and only Point
        assertEquals(-2.46, multiPoint!!.coordinates[0].longitude, DELTA)
        assertEquals(27.6835, multiPoint.coordinates[0].latitude, DELTA)
    }

    @Test
    fun combinePointAndMultiPolygonAndLineStringGeometry() {
        val pointMultiPolygonAndLineStringFeatureCollection =
            FeatureCollection(
                listOf(
                    Feature(Point(-2.46, 27.6835)),
                    Feature(
                        MultiPolygon.fromPolygons(
                            listOf(
                                Polygon(
                                    listOf(
                                        listOf(
                                            Point(11.42578125, 16.636191878397664),
                                            Point(7.91015625, -9.102096738726443),
                                            Point(
                                                31.113281249999996,
                                                17.644022027872726
                                            ),
                                            Point(11.42578125, 16.636191878397664)
                                        )
                                    )
                                )
                            )
                        )
                    ),
                    Feature(
                        LineString(
                            listOf(
                                Point(-11.25, 55.7765),
                                Point(41.1328, 22.91792)
                            )
                        )
                    )
                )
            )

        val combinedFeatureCollection = combine(pointMultiPolygonAndLineStringFeatureCollection)
        assertNotNull(combinedFeatureCollection)
        var multiPoint: MultiPoint? = null
        var multiLineString: MultiLineString? = null
        var multiPolygon: MultiPolygon? = null
        for (x in combinedFeatureCollection.features.indices) {
            val singleFeature = combinedFeatureCollection.features[x]
            if (singleFeature.geometry is MultiPoint) {
                multiPoint = combinedFeatureCollection.features[x].geometry as MultiPoint?
            }
            if (singleFeature.geometry is MultiLineString) {
                multiLineString =
                    combinedFeatureCollection.features[x].geometry as MultiLineString?
            }
            if (singleFeature.geometry is MultiPolygon) {
                multiPolygon = combinedFeatureCollection.features[x].geometry as MultiPolygon?
            }
        }
        assertNotNull(multiPoint)
        assertNotNull(multiLineString)
        assertNotNull(multiPolygon)

        // Checking the Polygon in the MultiPolygon

        // Checking the first Point
        assertEquals(11.42578125, multiPolygon!!.coordinates[0][0][0].longitude, DELTA)
        assertEquals(
            16.636191878397664,
            multiPolygon.coordinates[0][0][0].latitude,
            DELTA
        )

        // Checking the second Point
        assertEquals(7.91015625, multiPolygon.coordinates[0][0][1].longitude, DELTA)
        assertEquals(
            -9.102096738726443,
            multiPolygon.coordinates[0][0][1].latitude,
            DELTA
        )

        // Checking the LineString in the MultiLineString

        // Checking the first LineString location
        assertEquals(-11.25, multiLineString!!.coordinates[0][0].longitude, DELTA)
        assertEquals(55.7765, multiLineString.coordinates[0][0].latitude, DELTA)

        // Checking the second LineString location
        assertEquals(41.1328, multiLineString.coordinates[0][1].longitude, DELTA)
        assertEquals(22.91792, multiLineString.coordinates[0][1].latitude, DELTA)

        // Checking the Point in the MultiPoint

        // Checking the first and only Point
        assertEquals(-2.46, multiPoint!!.coordinates[0].longitude, DELTA)
        assertEquals(27.6835, multiPoint.coordinates[0].latitude, DELTA)
    }

    @Test
    fun combine_featureCollectionSizeCheck() {
        val pointMultiPolygonAndLineStringFeatureCollection =
            FeatureCollection(
                listOf(
                    Feature(Point(-2.46, 27.6835)),
                    Feature(
                        MultiPolygon.fromPolygons(
                            listOf(
                                Polygon(
                                    listOf(
                                        listOf(
                                            Point(11.42578125, 16.636191878397664),
                                            Point(7.91015625, -9.102096738726443),
                                            Point(
                                                31.113281249999996,
                                                17.644022027872726
                                            ),
                                            Point(11.42578125, 16.636191878397664)
                                        )
                                    )
                                )
                            )
                        )
                    ),
                    Feature(
                        LineString(
                            listOf(
                                Point(-11.25, 55.7765),
                                Point(41.1328, 22.91792)
                            )
                        )
                    )
                )
            )

        val combinedFeatureCollection = combine(pointMultiPolygonAndLineStringFeatureCollection)
        assertNotNull(combinedFeatureCollection)
        assertEquals(3, combinedFeatureCollection.features.size.toLong())
    }

    @Test
    fun combineEmptyFeatureCollectionThrowsException() {
        assertThrows(TurfException::class.java) {
            combine(
                FeatureCollection.fromJson(
                    """{
                          "type": "FeatureCollection",
                          "features": []
                        }"""
                )
            )
        }
    }

    @Test
    fun explodePointSingleFeature() {
        val point = Point(102.0, 0.5)
        assertEquals(
            1, explode(
                Feature(
                    point
                )
            ).features.size.toLong()
        )
    }

    @Test
    fun explodeMultiPointSingleFeature() {
        val multiPoint = MultiPoint.fromJson(loadJsonFixture(TURF_EXPLODE_MULTI_POINT))
        assertEquals(
            4, explode(
                Feature(
                    multiPoint
                )
            ).features.size.toLong()
        )
    }

    @Test
    fun explodeLineStringSingleFeature() {
        val lineString = LineString.fromJson(loadJsonFixture(TURF_EXPLODE_LINESTRING))
        assertEquals(
            4, explode(
                Feature(
                    lineString
                )
            ).features.size.toLong()
        )
    }

    @Test
    fun explodePolygonSingleFeature() {
        val polygon = Polygon(
            listOf(
                listOf(
                    Point(0.0, 101.0),
                    Point(1.0, 101.0),
                    Point(1.0, 100.0),
                    Point(0.0, 100.0)
                )
            )
        )
        assertEquals(
            3, explode(
                Feature(
                    polygon
                )
            ).features.size.toLong()
        )
    }

    @Test
    fun explodeMultiLineStringSingleFeature() {
        val multiLineString =
            MultiLineString.fromJson(loadJsonFixture(TURF_EXPLODE_MULTILINESTRING))
        assertEquals(
            4, explode(
                Feature(
                    multiLineString
                )
            ).features.size.toLong()
        )
    }

    @Test
    fun explodeMultiPolygonSingleFeature() {
        val multiPolygon = MultiPolygon.fromJson(loadJsonFixture(TURF_EXPLODE_MULTIPOLYGON))
        assertEquals(
            12, explode(
                Feature(
                    multiPolygon
                )
            ).features.size.toLong()
        )
    }

    @Test
    fun explodeGeometryCollectionSingleFeature() {
        val geometryCollection = GeometryCollection.fromJson(
            loadJsonFixture(
                TURF_EXPLODE_GEOMETRY_COLLECTION
            )
        )
        assertEquals(
            3, explode(
                Feature(
                    geometryCollection
                )
            ).features.size.toLong()
        )
    }

    @Test
    fun explodeFeatureCollection() {
        val featureCollection = FeatureCollection(
            listOf(
                Feature(
                    MultiLineString.fromJson(
                        loadJsonFixture(
                            TURF_EXPLODE_MULTILINESTRING
                        )
                    )
                ),
                Feature(
                    MultiPolygon.fromJson(
                        loadJsonFixture(
                            TURF_EXPLODE_MULTIPOLYGON
                        )
                    )
                )
            )
        )
        assertEquals(16, explode(featureCollection).features.size.toLong())
    }

    @Test
    fun polygonToLine_GeometryPolygon() {
        val polygon = Polygon.fromJson(
            loadJsonFixture(
                TURF_POLYGON_TO_LINE_PATH_IN + TURF_POLYGON_TO_LINE_FILENAME_GEOMETRY_POLYGON
            )
        )
        val expected = Feature.fromJson(
            loadJsonFixture(
                TURF_POLYGON_TO_LINE_PATH_OUT + TURF_POLYGON_TO_LINE_FILENAME_GEOMETRY_POLYGON
            )
        )
        compareJson(expected.toJson(), polygonToLine(polygon)!!.toJson())
    }

    @Test
    fun polygonToLine_Polygon() {
        val polygon = Feature.fromJson(
            loadJsonFixture(
                TURF_POLYGON_TO_LINE_PATH_IN + TURF_POLYGON_TO_LINE_FILENAME_POLYGON
            )
        )
        val expected = Feature.fromJson(
            loadJsonFixture(
                TURF_POLYGON_TO_LINE_PATH_OUT + TURF_POLYGON_TO_LINE_FILENAME_POLYGON
            )
        )
        compareJson(expected.toJson(), polygonToLine(polygon).toJson())
    }

    @Test
    fun polygonToLine_PolygonWithHole() {
        val polygon = Feature.fromJson(
            loadJsonFixture(
                TURF_POLYGON_TO_LINE_PATH_IN + TURF_POLYGON_TO_LINE_FILENAME_POLYGON_WITH_HOLE
            )
        )
        val expected = Feature.fromJson(
            loadJsonFixture(
                TURF_POLYGON_TO_LINE_PATH_OUT + TURF_POLYGON_TO_LINE_FILENAME_POLYGON_WITH_HOLE
            )
        )
        compareJson(expected.toJson(), polygonToLine(polygon).toJson())
    }

    @Test
    fun polygonToLine_MultiPolygon() {
        val multiPolygon = Feature.fromJson(
            loadJsonFixture(
                TURF_POLYGON_TO_LINE_PATH_IN + TURF_POLYGON_TO_LINE_FILENAME_MULTIPOLYGON
            )
        )
        val expected =
            FeatureCollection.fromJson(loadJsonFixture(TURF_POLYGON_TO_LINE_PATH_OUT + TURF_POLYGON_TO_LINE_FILENAME_MULTIPOLYGON))
        compareJson(expected.toJson(), multiPolygonToLine(multiPolygon).toJson())
    }

    @Test
    fun polygonToLine_MultiPolygonWithHoles() {
        val multiPolygon = Feature.fromJson(
            loadJsonFixture(
                TURF_POLYGON_TO_LINE_PATH_IN + TURF_POLYGON_TO_LINE_FILENAME_MULTIPOLYGON_WITH_HOLES
            )
        )
        val expected =
            FeatureCollection.fromJson(loadJsonFixture(TURF_POLYGON_TO_LINE_PATH_OUT + TURF_POLYGON_TO_LINE_FILENAME_MULTIPOLYGON_WITH_HOLES))
        compareJson(expected.toJson(), multiPolygonToLine(multiPolygon).toJson())
    }

    @Test
    fun polygonToLine_MultiPolygonWithOuterDoughnut() {
        val multiPolygon = Feature.fromJson(
            loadJsonFixture(
                TURF_POLYGON_TO_LINE_PATH_IN + TURF_POLYGON_TO_LINE_FILENAME_MULTIPOLYGON_OUTER_DOUGHNUT
            )
        )
        val expected =
            FeatureCollection.fromJson(loadJsonFixture(TURF_POLYGON_TO_LINE_PATH_OUT + TURF_POLYGON_TO_LINE_FILENAME_MULTIPOLYGON_OUTER_DOUGHNUT))
        compareJson(expected.toJson(), multiPolygonToLine(multiPolygon).toJson())
    }

    companion object {
        private const val TURF_EXPLODE_MULTI_POINT = "turf-explode/multipoint.geojson"
        private const val TURF_EXPLODE_LINESTRING = "turf-explode/linestring.geojson"
        private const val TURF_EXPLODE_MULTILINESTRING = "turf-explode/multilinestring.geojson"
        private const val TURF_EXPLODE_MULTIPOLYGON = "turf-explode/multipolygon.geojson"
        private const val TURF_EXPLODE_GEOMETRY_COLLECTION =
            "turf-explode/geometrycollection.geojson"

        private const val TURF_POLYGON_TO_LINE_PATH_IN = "turf-polygon-to-line/in/"
        private const val TURF_POLYGON_TO_LINE_PATH_OUT = "turf-polygon-to-line/expected/"

        private const val TURF_POLYGON_TO_LINE_FILENAME_POLYGON = "polygon.geojson"
        private const val TURF_POLYGON_TO_LINE_FILENAME_GEOMETRY_POLYGON =
            "geometry-polygon.geojson"
        private const val TURF_POLYGON_TO_LINE_FILENAME_POLYGON_WITH_HOLE =
            "polygon-with-hole.geojson"

        private const val TURF_POLYGON_TO_LINE_FILENAME_MULTIPOLYGON = "multi-polygon.geojson"
        private const val TURF_POLYGON_TO_LINE_FILENAME_MULTIPOLYGON_OUTER_DOUGHNUT =
            "multi-polygon-outer-doughnut.geojson"
        private const val TURF_POLYGON_TO_LINE_FILENAME_MULTIPOLYGON_WITH_HOLES =
            "multi-polygon-with-holes.geojson"
    }
}
