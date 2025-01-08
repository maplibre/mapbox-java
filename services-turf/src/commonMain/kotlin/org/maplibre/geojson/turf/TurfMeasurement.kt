package org.maplibre.geojson.turf

import kotlinx.serialization.json.JsonElement
import org.maplibre.geojson.model.BoundingBox
import org.maplibre.geojson.model.Feature
import org.maplibre.geojson.model.FeatureCollection
import org.maplibre.geojson.model.GeoJson
import org.maplibre.geojson.model.Geometry
import org.maplibre.geojson.model.GeometryCollection
import org.maplibre.geojson.model.LineString
import org.maplibre.geojson.model.MultiLineString
import org.maplibre.geojson.model.MultiPoint
import org.maplibre.geojson.model.MultiPolygon
import org.maplibre.geojson.model.Point
import org.maplibre.geojson.model.Polygon
import org.maplibre.geojson.turf.TurfConversion.degreesToRadians
import org.maplibre.geojson.turf.TurfConversion.lengthToRadians
import org.maplibre.geojson.turf.TurfConversion.radiansToDegrees
import org.maplibre.geojson.turf.TurfConversion.radiansToLength
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.PI

/**
 * Class contains an assortment of methods used to calculate measurements such as bearing,
 * destination, midpoint, etc.
 *
 * @see [Turf documentation](http://turfjs.org/docs/)
 *
 * @since 1.2.0
 */
object TurfMeasurement {

    /**
     * Earth's radius in meters.
     */
    private var EARTH_RADIUS: Double = 6378137.0

    /**
     * Takes two [Point]s and finds the geographic bearing between them.
     *
     * @param point1 first point used for calculating the bearing
     * @param point2 second point used for calculating the bearing
     * @return bearing in decimal degrees
     * @see [Turf Bearing documentation](http://turfjs.org/docs/.bearing)
     *
     * @since 1.3.0
     */
    @JvmStatic
    fun bearing(point1: Point, point2: Point): Double {
        val lon1 = degreesToRadians(point1.longitude)
        val lon2 = degreesToRadians(point2.longitude)
        val lat1 = degreesToRadians(point1.latitude)
        val lat2 = degreesToRadians(point2.latitude)
        val value1 = sin(lon2 - lon1) * cos(lat2)
        val value2 = cos(lat1) * sin(lat2) - (sin(lat1) * cos(lat2) * cos(lon2 - lon1))

        return radiansToDegrees(atan2(value1, value2))
    }

    /**
     * Takes a Point and calculates the location of a destination point given a distance in
     * degrees, radians, miles, or kilometers; and bearing in degrees. This uses the Haversine
     * formula to account for global curvature.
     *
     * @param point    starting point used for calculating the destination
     * @param distance distance from the starting point
     * @param bearing  ranging from -180 to 180 in decimal degrees
     * @param unit    one of the units found inside [TurfUnit]
     * @return destination [Point] result where you specified
     * @see [Turf Destination documetation](http://turfjs.org/docs/.destination)
     *
     * @since 1.2.0
     */
    @JvmStatic
    fun destination(
        point: Point,
        distance: Double,
        bearing: Double,
        unit: TurfUnit
    ): Point {
        val longitude1 = degreesToRadians(point.longitude)
        val latitude1 = degreesToRadians(point.latitude)
        val bearingRad = degreesToRadians(bearing)

        val radians = lengthToRadians(distance, unit)

        val latitude2 = asin(
            sin(latitude1) * cos(radians) + cos(latitude1) * sin(radians) * cos(bearingRad)
        )
        val longitude2 = longitude1 + atan2(
            sin(bearingRad) * sin(radians) * cos(latitude1),
            cos(radians) - sin(latitude1) * sin(latitude2)
        )

        return Point(
            radiansToDegrees(longitude2),
            radiansToDegrees(latitude2)
        )
    }

    /**
     * Calculates the distance between two points in degress, radians, miles, or kilometers. This
     * uses the Haversine formula to account for global curvature.
     *
     * @param point1 first point used for calculating the bearing
     * @param point2 second point used for calculating the bearing
     * @param unit  one of the units found inside [TurfUnit]
     * @return distance between the two points in kilometers
     * @see [Turf distance documentation](http://turfjs.org/docs/.distance)
     *
     * @since 1.2.0
     */
    @JvmStatic
    @JvmOverloads
    fun distance(
        point1: Point, point2: Point,
        unit: TurfUnit = TurfUnit.DEFAULT
    ): Double {
        val difLat = degreesToRadians((point2.latitude - point1.latitude))
        val difLon = degreesToRadians((point2.longitude - point1.longitude))
        val lat1 = degreesToRadians(point1.latitude)
        val lat2 = degreesToRadians(point2.latitude)

        val value = sin(difLat / 2).pow(2.0) + sin(difLon / 2).pow(2.0) * cos(lat1) * cos(lat2)

        return radiansToLength(
            2 * atan2(sqrt(value), sqrt(1 - value)), unit
        )
    }

    /**
     * Takes a [LineString] and measures its length in the specified units.
     *
     * @param lineString geometry to measure
     * @param unit       one of the units found inside [TurfUnit]
     * @return length of the input line in the units specified
     * @see [Turf Line Distance documentation](http://turfjs.org/docs/.linedistance)
     *
     * @since 1.2.0
     */
    @JvmStatic
    fun length(
        lineString: LineString,
        unit: TurfUnit
    ): Double = length(lineString.coordinates, unit)

    /**
     * Takes a [MultiLineString] and measures its length in the specified units.
     *
     * @param multiLineString geometry to measure
     * @param unit            one of the units found inside [TurfUnit]
     * @return length of the input lines combined, in the units specified
     * @see [Turf Line Distance documentation](http://turfjs.org/docs/.linedistance)
     *
     * @since 1.2.0
     */
    @JvmStatic
    fun length(
        multiLineString: MultiLineString,
        unit: TurfUnit
    ): Double {
        return multiLineString.coordinates
            .sumOf { points -> length(points, unit) }
    }

    /**
     * Takes a [Polygon] and measures its perimeter in the specified units. if the polygon
     * contains holes, the perimeter will also be included.
     *
     * @param polygon geometry to measure
     * @param unit   one of the units found inside [TurfUnit]
     * @return total perimeter of the input polygon in the units specified
     * @see [Turf Line Distance documentation](http://turfjs.org/docs/.linedistance)
     *
     * @since 1.2.0
     */
    @JvmStatic
    fun length(
        polygon: Polygon,
        unit: TurfUnit
    ): Double {
        return polygon.coordinates
            .sumOf { points -> length(points, unit) }
    }

    /**
     * Takes a [MultiPolygon] and measures each polygons perimeter in the specified units. if
     * one of the polygons contains holes, the perimeter will also be included.
     *
     * @param multiPolygon geometry to measure
     * @param unit        one of the units found inside [TurfUnit]
     * @return total perimeter of the input polygons combined, in the units specified
     * @see [Turf Line Distance documentation](http://turfjs.org/docs/.linedistance)
     *
     * @since 1.2.0
     */
    fun length(
        multiPolygon: MultiPolygon,
        unit: TurfUnit
    ): Double {
        return multiPolygon.coordinates
            .flatten()
            .sumOf { points -> length(points, unit) }
    }

    /**
     * Takes a [List] of [Point] and measures its length in the specified units.
     *
     * @param coords geometry to measure
     * @param unit  one of the units found inside [TurfUnit]
     * @return length of the input line in the units specified
     * @see [Turf Line Distance documentation](http://turfjs.org/docs/.linedistance)
     *
     * @since 5.2.0
     */
    fun length(coords: List<Point>, unit: TurfUnit): Double {
        return coords.drop(1)
            .mapIndexed { index, point ->
                // Using unmodified index for previous point is working,
                // because we drop the first point
                distance(coords[index], point, unit)
            }
            .sum()
    }

    /**
     * Takes two [Point]s and returns a point midway between them. The midpoint is calculated
     * geodesically, meaning the curvature of the earth is taken into account.
     *
     * @param from first point used for calculating the midpoint
     * @param to   second point used for calculating the midpoint
     * @return a [Point] midway between point1 and point2
     * @see [Turf Midpoint documentation](http://turfjs.org/docs/.midpoint)
     *
     * @since 1.3.0
     */
    @JvmStatic
    fun midpoint(from: Point, to: Point): Point {
        val dist = distance(from, to, TurfUnit.MILES)
        val heading = bearing(from, to)
        return destination(from, dist / 2, heading, TurfUnit.MILES)
    }

    /**
     * Takes a line and returns a point at a specified distance along the line.
     *
     * @param line     that the point should be placed upon
     * @param distance along the linestring geometry which the point should be placed on
     * @param unit    one of the units found inside [TurfUnit]
     * @return a [Point] which is on the linestring provided and at the distance from
     * the origin of that line to the end of the distance
     * @since 1.3.0
     */
    @JvmStatic
    fun along(
        line: LineString,
        distance: Double,
        unit: TurfUnit
    ): Point {
        return along(line.coordinates, distance, unit)
    }

    /**
     * Takes a list of points and returns a point at a specified distance along the line.
     *
     * @param coords   that the point should be placed upon
     * @param distance along the linestring geometry which the point should be placed on
     * @param unit     one of the units found inside [TurfUnit]
     * @return a [Point] which is on the linestring provided and at the distance from
     * the origin of that line to the end of the distance
     * @since 5.2.0
     */
    fun along(
        coords: List<Point>,
        distance: Double,
        unit: TurfUnit
    ): Point {
        var travelled = 0.0
        for ((index, point) in coords.withIndex()) {
            if (travelled >= distance) {
                val overshot = distance - travelled
                if (overshot == 0.0 || index == 0) {
                    return point
                } else {
                    val direction = bearing(point, coords[index - 1]) - 180
                    return destination(point, overshot, direction, unit)
                }
            } else if (index < coords.size - 1) {
                travelled += distance(point, coords[index + 1], unit)
            }
        }

        return coords.last()
    }

    /**
     * Takes a set of features, calculates the bbox of all input features, and returns a bounding box.
     *
     * @param point a [Point] object
     * @return A double array defining the bounding box in this order `[minX, minY, maxX, maxY]`
     * @since 2.0.0
     */
    @JvmStatic
    fun bbox(point: Point): DoubleArray {
        val resultCoords = TurfMeta.coordAll(point)
        return bboxCalculator(resultCoords)
    }

    /**
     * Takes a set of features, calculates the bbox of all input features, and returns a bounding box.
     *
     * @param lineString a [LineString] object
     * @return A double array defining the bounding box in this order `[minX, minY, maxX, maxY]`
     * @since 2.0.0
     */
    @JvmStatic
    fun bbox(lineString: LineString): DoubleArray {
        val resultCoords = TurfMeta.coordAll(lineString)
        return bboxCalculator(resultCoords)
    }

    /**
     * Takes a set of features, calculates the bbox of all input features, and returns a bounding box.
     *
     * @param multiPoint a [MultiPoint] object
     * @return a double array defining the bounding box in this order `[minX, minY, maxX, maxY]`
     * @since 2.0.0
     */
    @JvmStatic
    fun bbox(multiPoint: MultiPoint): DoubleArray {
        val resultCoords = TurfMeta.coordAll(multiPoint)
        return bboxCalculator(resultCoords)
    }

    /**
     * Takes a set of features, calculates the bbox of all input features, and returns a bounding box.
     *
     * @param polygon a [Polygon] object
     * @return a double array defining the bounding box in this order `[minX, minY, maxX, maxY]`
     * @since 2.0.0
     */
    @JvmStatic
    fun bbox(polygon: Polygon): DoubleArray {
        val resultCoords = TurfMeta.coordAll(polygon, false)
        return bboxCalculator(resultCoords)
    }

    /**
     * Takes a set of features, calculates the bbox of all input features, and returns a bounding box.
     *
     * @param multiLineString a [MultiLineString] object
     * @return a double array defining the bounding box in this order `[minX, minY, maxX, maxY]`
     * @since 2.0.0
     */
    @JvmStatic
    fun bbox(multiLineString: MultiLineString): DoubleArray {
        val resultCoords = TurfMeta.coordAll(multiLineString)
        return bboxCalculator(resultCoords)
    }

    /**
     * Takes a set of features, calculates the bbox of all input features, and returns a bounding box.
     *
     * @param multiPolygon a [MultiPolygon] object
     * @return a double array defining the bounding box in this order `[minX, minY, maxX, maxY]`
     * @since 2.0.0
     */
    @JvmStatic
    fun bbox(multiPolygon: MultiPolygon): DoubleArray {
        val resultCoords = TurfMeta.coordAll(multiPolygon, false)
        return bboxCalculator(resultCoords)
    }

    /**
     * Takes a set of features, calculates the bbox of all input features, and returns a bounding box.
     *
     * @param geoJson a [GeoJson] object
     * @return a double array defining the bounding box in this order `[minX, minY, maxX, maxY]`
     * @since 4.8.0
     */
    fun bbox(geoJson: GeoJson): DoubleArray {
        val boundingBox = geoJson.bbox
        if (boundingBox != null) {
            return doubleArrayOf(
                boundingBox.west,
                boundingBox.south,
                boundingBox.east,
                boundingBox.north
            )
        }

        return when (geoJson) {
            is Geometry -> bbox(geoJson)
            is FeatureCollection -> bbox(geoJson)
            is Feature -> bbox(geoJson)
            else -> throw UnsupportedOperationException("bbox type not supported for GeoJson instance")
        }
    }

    /**
     * Takes a set of features, calculates the bbox of all input features, and returns a bounding box.
     *
     * @param featureCollection a [FeatureCollection] object
     * @return a double array defining the bounding box in this order `[minX, minY, maxX, maxY]`
     * @since 4.8.0
     */
    fun bbox(featureCollection: FeatureCollection): DoubleArray {
        return bboxCalculator(TurfMeta.coordAll(featureCollection, false))
    }

    /**
     * Takes a set of features, calculates the bbox of all input features, and returns a bounding box.
     *
     * @param feature a [Feature] object
     * @return a double array defining the bounding box in this order `[minX, minY, maxX, maxY]`
     * @since 4.8.0
     */
    fun bbox(feature: Feature): DoubleArray {
        return bboxCalculator(TurfMeta.coordAll(feature, false))
    }

    /**
     * Takes an arbitrary [Geometry] and calculates a bounding box.
     *
     * @param geometry a [Geometry] object
     * @return a double array defining the bounding box in this order `[minX, minY, maxX, maxY]`
     * @since 2.0.0
     */
    @JvmStatic
    fun bbox(geometry: Geometry): DoubleArray {
        return when (geometry) {
            is Point -> bbox(geometry)
            is MultiPoint -> bbox(geometry)
            is LineString -> bbox(geometry)
            is MultiLineString -> bbox(geometry)
            is Polygon -> bbox(geometry)
            is MultiPolygon -> bbox(geometry)

            is GeometryCollection -> {
                val points = geometry.geometries.flatMap { geo ->
                    val bbox = bbox(geo)
                    listOf(
                        Point(bbox[0], bbox[1]),
                        Point(bbox[2], bbox[1]),
                        Point(bbox[2], bbox[3]),
                        Point(bbox[0], bbox[3])
                    )
                }
                bbox(MultiPoint(points))
            }

            else -> throw RuntimeException(("Unknown geometry class: " + geometry::class))
        }
    }

    private fun bboxCalculator(resultCoords: List<Point>): DoubleArray {
        return doubleArrayOf(
            resultCoords.minOf { c -> c.longitude },
            resultCoords.minOf { c -> c.latitude },
            resultCoords.maxOf { c -> c.longitude },
            resultCoords.maxOf { c -> c.latitude },
        )
    }

    /**
     * Takes a [BoundingBox] and uses its coordinates to create a [Polygon]
     * geometry.
     *
     * @param boundingBox a [BoundingBox] object to calculate with
     * @param properties a [JsonObject] containing the feature properties
     * @param id  common identifier of this feature
     * @return a [Feature] object
     * @see [Turf BoundingBox Polygon documentation](http://turfjs.org/docs/.bboxPolygon)
     *
     * @since 4.9.0
     */
    @JvmStatic
    @JvmOverloads
    fun bboxPolygon(
        boundingBox: BoundingBox,
        properties: Map<String, JsonElement>? = null,
        id: String? = null
    ): Feature {
        return Feature(
            Polygon(
                listOf(
                    listOf(
                        Point(boundingBox.west, boundingBox.south),
                        Point(boundingBox.east, boundingBox.south),
                        Point(boundingBox.east, boundingBox.north),
                        Point(boundingBox.west, boundingBox.north),
                        Point(boundingBox.west, boundingBox.south)
                    )
                )
            ), properties, id
        )
    }

    /**
     * Takes a bbox and uses its coordinates to create a [Polygon] geometry.
     *
     * @param bbox a double[] object to calculate with
     * @param properties a [JsonObject] containing the feature properties
     * @param id  common identifier of this feature
     * @return a [Feature] object
     * @see [Turf BoundingBox Polygon documentation](http://turfjs.org/docs/.bboxPolygon)
     *
     * @since 4.9.0
     */
    @JvmOverloads
    fun bboxPolygon(
        bbox: DoubleArray,
        properties: Map<String, JsonElement>? = null,
        id: String? = null
    ): Feature {
        return Feature(
            Polygon(
                listOf(
                    listOf(
                        Point(bbox[0], bbox[1]),
                        Point(bbox[2], bbox[1]),
                        Point(bbox[2], bbox[3]),
                        Point(bbox[0], bbox[3]),
                        Point(bbox[0], bbox[1])
                    )
                )
            ), properties, id
        )
    }

    /**
     * Takes any number of features and returns a rectangular Polygon that encompasses all vertices.
     *
     * @param geoJson input features
     * @return a rectangular Polygon feature that encompasses all vertices
     * @since 4.9.0
     */
    @JvmStatic
    fun envelope(geoJson: GeoJson): Polygon? {
        return bboxPolygon(bbox(geoJson)).geometry as Polygon?
    }

    /**
     * Takes a bounding box and calculates the minimum square bounding box
     * that would contain the input.
     *
     * @param boundingBox extent in west, south, east, north order
     * @return a square surrounding bbox
     * @since 4.9.0
     */
    @JvmStatic
    fun square(boundingBox: BoundingBox): BoundingBox {
        val horizontalDistance = distance(
            boundingBox.southwest,
            Point(boundingBox.east, boundingBox.south)
        )
        val verticalDistance = distance(
            Point(boundingBox.west, boundingBox.south),
            Point(boundingBox.west, boundingBox.north)
        )

        if (horizontalDistance >= verticalDistance) {
            val verticalMidpoint = (boundingBox.south + boundingBox.north) / 2
            return BoundingBox(
                boundingBox.west,
                verticalMidpoint - ((boundingBox.east - boundingBox.west) / 2),
                boundingBox.east,
                verticalMidpoint + ((boundingBox.east - boundingBox.west) / 2)
            )
        } else {
            val horizontalMidpoint = (boundingBox.west + boundingBox.east) / 2
            return BoundingBox(
                horizontalMidpoint - ((boundingBox.north - boundingBox.south) / 2),
                boundingBox.south,
                horizontalMidpoint + ((boundingBox.north - boundingBox.south) / 2),
                boundingBox.north
            )
        }
    }

    /**
     * Takes one [Feature] and returns it's area in square meters.
     *
     * @param feature input [Feature]
     * @return area in square meters
     * @since 4.10.0
     */
    @JvmStatic
    fun area(feature: Feature): Double {
        //TODO: make it more sense here to throw exception or return `null`?
        return feature.geometry?.let { geometry -> area(geometry) } ?: 0.0
    }

    /**
     * Takes one [FeatureCollection] and returns it's area in square meters.
     *
     * @param featureCollection input [FeatureCollection]
     * @return area in square meters
     * @since 4.10.0
     */
    @JvmStatic
    fun area(featureCollection: FeatureCollection): Double {
        return featureCollection.features
            .sumOf { feature -> area(feature) }
    }

    /**
     * Takes one [Geometry] and returns its area in square meters.
     *
     * @param geometry input [Geometry]
     * @return area in square meters
     * @since 4.10.0
     */
    @JvmStatic
    fun area(geometry: Geometry): Double {
        return calculateArea(geometry)
    }

    private fun calculateArea(geometry: Geometry): Double {
        return when (geometry) {
            is Polygon -> polygonArea(geometry.coordinates)
            is MultiPolygon ->
                geometry.coordinates.sumOf { coordinates -> polygonArea(coordinates) }

            else ->
                // Area should be 0 for case Point, MultiPoint, LineString and MultiLineString
                0.0
        }
    }

    private fun polygonArea(coordinates: List<List<Point>>): Double {
        if (coordinates.isEmpty()) {
            return 0.0
        }

        return abs(ringArea(coordinates.first())) - coordinates.drop(1)
            .sumOf { c -> abs(ringArea(c)) }
    }

    /**
     * Calculate the approximate area of the polygon were it projected onto the earth.
     * Note that this area will be positive if ring is oriented clockwise, otherwise
     * it will be negative.
     *
     *
     * Reference:
     * Robert. G. Chamberlain and William H. Duquette, "Some Algorithms for Polygons on a Sphere",
     * JPL Publication 07-03, Jet Propulsion
     * Laboratory, Pasadena, CA, June 2007 [JPL Publication 07-03](https://trs.jpl.nasa.gov/handle/2014/41271)
     *
     * @param coordinates  A list of [Point] of Ring Coordinates
     * @return The approximate signed geodesic area of the polygon in square meters.
     */
    private fun ringArea(coordinates: List<Point>): Double {
        var p1: Point
        var p2: Point
        var p3: Point
        var lowerIndex: Int
        var middleIndex: Int
        var upperIndex: Int
        var total = 0.0
        val coordsLength = coordinates.size

        if (coordsLength > 2) {
            for (i in 0 until coordsLength) {
                when (i) {
                    coordsLength - 2 -> { // i = N-2
                        lowerIndex = coordsLength - 2
                        middleIndex = coordsLength - 1
                        upperIndex = 0
                    }

                    coordsLength - 1 -> { // i = N-1
                        lowerIndex = coordsLength - 1
                        middleIndex = 0
                        upperIndex = 1
                    }

                    else -> { // i = 0 to N-3
                        lowerIndex = i
                        middleIndex = i + 1
                        upperIndex = i + 2
                    }
                }
                p1 = coordinates[lowerIndex]
                p2 = coordinates[middleIndex]
                p3 = coordinates[upperIndex]
                total += (rad(p3.longitude) - rad(p1.longitude)) * sin(rad(p2.latitude))
            }
            total = total * EARTH_RADIUS * EARTH_RADIUS / 2
        }
        return total
    }

    private fun rad(num: Double): Double {
        return num * PI / 180
    }

    /**
     * Takes a [Feature] and returns the absolute center of the [Feature].
     *
     * @param feature the single [Feature] to find the center of.
     * @param properties a optional [JsonObject] containing the properties that should be
     * placed in the returned [Feature].
     * @param id  an optional common identifier that should be placed in the returned [Feature].
     * @return a [Feature] with a [Point] geometry type.
     * @since 5.3.0
     */
    @JvmStatic
    @JvmOverloads
    fun center(
        feature: Feature,
        properties: Map<String, JsonElement>? = null,
        id: String? = null
    ): Feature {
        return center(FeatureCollection(feature), properties, id)
    }

    /**
     * Takes [FeatureCollection] and returns the absolute center
     * of the [Feature]s in the [FeatureCollection].
     *
     * @param featureCollection the single [FeatureCollection] to find the center of.
     * @param properties a optional [JsonObject] containing the properties that should be
     * placed in the returned [Feature].
     * @param id  an optional common identifier that should be placed in the returned [Feature].
     * @return a [Feature] with a [Point] geometry type.
     * @since 5.3.0
     */
    @JvmStatic
    @JvmOverloads
    fun center(
        featureCollection: FeatureCollection,
        properties: Map<String, JsonElement>? = null,
        id: String? = null
    ): Feature {
        val ext = bbox(featureCollection)
        val finalCenterLongitude = (ext[0] + ext[2]) / 2
        val finalCenterLatitude = (ext[1] + ext[3]) / 2
        return Feature(
            Point(finalCenterLongitude, finalCenterLatitude),
            properties, id
        )
    }
}
