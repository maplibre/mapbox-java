package org.maplibre.turf

import com.google.gson.JsonObject
import org.maplibre.geojson.BoundingBox
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.GeoJson
import org.maplibre.geojson.Geometry
import org.maplibre.geojson.LineString
import org.maplibre.geojson.MultiLineString
import org.maplibre.geojson.MultiPoint
import org.maplibre.geojson.MultiPolygon
import org.maplibre.geojson.Point
import org.maplibre.geojson.Polygon
import org.maplibre.geojson.common.toCommon
import org.maplibre.geojson.common.toJvm
import org.maplibre.geojson.common.toKtxJsonMap
import org.maplibre.turf.TurfConstants.TurfUnitCriteria
import org.maplibre.turf.common.toUnit
import org.maplibre.geojson.model.Geometry as CommonGeometry
import org.maplibre.geojson.turf.TurfMeasurement as CommonTurfMeasurement

/**
 * Class contains an assortment of methods used to calculate measurements such as bearing,
 * destination, midpoint, etc.
 *
 * @see [Turf documentation](http://turfjs.org/docs/)
 *
 * @since 1.2.0
 */
@Deprecated(
    message = "Use new common Turf utils instead.",
    replaceWith = ReplaceWith("TurfMeasurement", "org.maplibre.geojson.turf.TurfMeasurement"),
)
object TurfMeasurement {

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
        return CommonTurfMeasurement.bearing(point1, point2)
    }

    /**
     * Takes a Point and calculates the location of a destination point given a distance in
     * degrees, radians, miles, or kilometers; and bearing in degrees. This uses the Haversine
     * formula to account for global curvature.
     *
     * @param point    starting point used for calculating the destination
     * @param distance distance from the starting point
     * @param bearing  ranging from -180 to 180 in decimal degrees
     * @param units    one of the units found inside [TurfConstants.TurfUnitCriteria]
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
        @TurfUnitCriteria units: String
    ): Point {
        return CommonTurfMeasurement.destination(point, distance, bearing, units.toUnit()).toJvm()
    }

    /**
     * Calculates the distance between two points in degress, radians, miles, or kilometers. This
     * uses the Haversine formula to account for global curvature.
     *
     * @param point1 first point used for calculating the bearing
     * @param point2 second point used for calculating the bearing
     * @param units  one of the units found inside [TurfConstants.TurfUnitCriteria]
     * @return distance between the two points in kilometers
     * @see [Turf distance documentation](http://turfjs.org/docs/.distance)
     *
     * @since 1.2.0
     */
    @JvmStatic
    @JvmOverloads
    fun distance(
        point1: Point,
        point2: Point,
        @TurfUnitCriteria units: String = TurfConstants.UNIT_DEFAULT
    ): Double {
        return CommonTurfMeasurement.distance(point1, point2, units.toUnit())
    }

    /**
     * Takes a [LineString] and measures its length in the specified units.
     *
     * @param lineString geometry to measure
     * @param units      one of the units found inside [TurfConstants.TurfUnitCriteria]
     * @return length of the input line in the units specified
     * @see [Turf Line Distance documentation](http://turfjs.org/docs/.linedistance)
     *
     * @since 1.2.0
     */
    @JvmStatic
    fun length(
        lineString: LineString,
        @TurfUnitCriteria units: String
    ): Double {
        return CommonTurfMeasurement.length(lineString, units.toUnit())
    }

    /**
     * Takes a [MultiLineString] and measures its length in the specified units.
     *
     * @param multiLineString geometry to measure
     * @param units           one of the units found inside [TurfConstants.TurfUnitCriteria]
     * @return length of the input lines combined, in the units specified
     * @see [Turf Line Distance documentation](http://turfjs.org/docs/.linedistance)
     *
     * @since 1.2.0
     */
    @JvmStatic
    fun length(
        multiLineString: MultiLineString,
        @TurfUnitCriteria units: String
    ): Double {
        return CommonTurfMeasurement.length(multiLineString, units.toUnit())
    }

    /**
     * Takes a [Polygon] and measures its perimeter in the specified units. if the polygon
     * contains holes, the perimeter will also be included.
     *
     * @param polygon geometry to measure
     * @param units   one of the units found inside [TurfConstants.TurfUnitCriteria]
     * @return total perimeter of the input polygon in the units specified
     * @see [Turf Line Distance documentation](http://turfjs.org/docs/.linedistance)
     *
     * @since 1.2.0
     */
    @JvmStatic
    fun length(
        polygon: Polygon,
        @TurfUnitCriteria units: String
    ): Double {
        return CommonTurfMeasurement.length(polygon, units.toUnit())
    }

    /**
     * Takes a [MultiPolygon] and measures each polygons perimeter in the specified units. if
     * one of the polygons contains holes, the perimeter will also be included.
     *
     * @param multiPolygon geometry to measure
     * @param units        one of the units found inside [TurfConstants.TurfUnitCriteria]
     * @return total perimeter of the input polygons combined, in the units specified
     * @see [Turf Line Distance documentation](http://turfjs.org/docs/.linedistance)
     *
     * @since 1.2.0
     */
    @JvmStatic
    fun length(
        multiPolygon: MultiPolygon,
        @TurfUnitCriteria units: String
    ): Double {
        return CommonTurfMeasurement.length(multiPolygon, units.toUnit())
    }

    /**
     * Takes a [List] of [Point] and measures its length in the specified units.
     *
     * @param coords geometry to measure
     * @param units  one of the units found inside [TurfConstants.TurfUnitCriteria]
     * @return length of the input line in the units specified
     * @see [Turf Line Distance documentation](http://turfjs.org/docs/.linedistance)
     *
     * @since 5.2.0
     */
    @JvmStatic
    fun length(coords: List<Point>, units: String): Double {
        return CommonTurfMeasurement.length(coords, units.toUnit())
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
        return CommonTurfMeasurement.midpoint(from, to).toJvm()
    }

    /**
     * Takes a line and returns a point at a specified distance along the line.
     *
     * @param line     that the point should be placed upon
     * @param distance along the linestring geometry which the point should be placed on
     * @param units    one of the units found inside [TurfConstants.TurfUnitCriteria]
     * @return a [Point] which is on the linestring provided and at the distance from
     * the origin of that line to the end of the distance
     * @since 1.3.0
     */
    @JvmStatic
    fun along(
        line: LineString,
        distance: Double,
        @TurfUnitCriteria units: String
    ): Point {
        return CommonTurfMeasurement.along(line, distance, units.toUnit()).toJvm()
    }

    /**
     * Takes a list of points and returns a point at a specified distance along the line.
     *
     * @param coords   that the point should be placed upon
     * @param distance along the linestring geometry which the point should be placed on
     * @param units    one of the units found inside [TurfConstants.TurfUnitCriteria]
     * @return a [Point] which is on the linestring provided and at the distance from
     * the origin of that line to the end of the distance
     * @since 5.2.0
     */
    @JvmStatic
    fun along(
        coords: List<Point>,
        distance: Double,
        @TurfUnitCriteria units: String
    ): Point {
        return CommonTurfMeasurement.along(coords, distance, units.toUnit()).toJvm()
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
        return CommonTurfMeasurement.bbox(point)
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
        return CommonTurfMeasurement.bbox(lineString)
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
        return CommonTurfMeasurement.bbox(multiPoint)
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
        return CommonTurfMeasurement.bbox(polygon)
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
        return CommonTurfMeasurement.bbox(multiLineString)
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
        return CommonTurfMeasurement.bbox(multiPolygon)
    }

    /**
     * Takes a set of features, calculates the bbox of all input features, and returns a bounding box.
     *
     * @param geoJson a [GeoJson] object
     * @return a double array defining the bounding box in this order `[minX, minY, maxX, maxY]`
     * @since 4.8.0
     */
    @JvmStatic
    fun bbox(geoJson: GeoJson): DoubleArray {
        return CommonTurfMeasurement.bbox(CommonGeometry.fromJson(geoJson.toJson()))
    }

    /**
     * Takes a set of features, calculates the bbox of all input features, and returns a bounding box.
     *
     * @param featureCollection a [FeatureCollection] object
     * @return a double array defining the bounding box in this order `[minX, minY, maxX, maxY]`
     * @since 4.8.0
     */
    @JvmStatic
    fun bbox(featureCollection: FeatureCollection): DoubleArray {
        return CommonTurfMeasurement.bbox(featureCollection)
    }

    /**
     * Takes a set of features, calculates the bbox of all input features, and returns a bounding box.
     *
     * @param feature a [Feature] object
     * @return a double array defining the bounding box in this order `[minX, minY, maxX, maxY]`
     * @since 4.8.0
     */
    @JvmStatic
    fun bbox(feature: Feature): DoubleArray {
        return CommonTurfMeasurement.bbox(feature)
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
        return CommonTurfMeasurement.bbox(geometry.toCommon())
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
        properties: JsonObject? = null,
        id: String? = null
    ): Feature {
        return CommonTurfMeasurement.bboxPolygon(boundingBox, properties?.toKtxJsonMap(), id)
            .toJvm()
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
    @JvmStatic
    @JvmOverloads
    fun bboxPolygon(
        bbox: DoubleArray,
        properties: JsonObject? = null,
        id: String? = null
    ): Feature {
        return CommonTurfMeasurement.bboxPolygon(bbox, properties?.toKtxJsonMap(), id).toJvm()
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
        return CommonTurfMeasurement.envelope(CommonGeometry.fromJson(geoJson.toJson()))?.toJvm()
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
        return CommonTurfMeasurement.square(boundingBox).toJvm()
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
        return CommonTurfMeasurement.area(feature)
    }

    /**
     * Takes one [FeatureCollection] and returns it's area in square meters.
     *
     * @param featureCollection input [FeatureCollection]
     * @return area in square meters
     * @since 4.10.0
     */
    fun area(featureCollection: FeatureCollection): Double {
        return CommonTurfMeasurement.area(featureCollection)
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
        return CommonTurfMeasurement.area(geometry.toCommon())
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
    fun center(
        feature: Feature,
        properties: JsonObject?,
        id: String?
    ): Feature {
        return CommonTurfMeasurement.center(feature, properties?.toKtxJsonMap(), id).toJvm()
    }

    /**
     * Takes a [Feature] and returns the absolute center of the [Feature].
     *
     * @param feature the single [Feature] to find the center of.
     * @return a [Feature] with a [Point] geometry type.
     * @since 5.3.0
     */
    @JvmStatic
    fun center(feature: Feature): Feature {
        return CommonTurfMeasurement.center(feature).toJvm()
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
        properties: JsonObject? = null,
        id: String? = null
    ): Feature {
        return CommonTurfMeasurement.center(featureCollection, properties?.toKtxJsonMap(), id)
            .toJvm()
    }
}