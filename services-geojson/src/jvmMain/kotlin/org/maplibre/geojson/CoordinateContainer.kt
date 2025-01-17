package org.maplibre.geojson


/**
 * Each of the s geometries which make up GeoJson implement this interface and consume a varying
 * dimension of [Point] list. Since this is varying, each geometry object fulfills the
 * contract by replacing the generic with a well defined list of Points.
 *
 * @param <T> a generic allowing varying dimensions for each GeoJson geometry
 * @since 3.0.0
</T> */
@Deprecated(
    message = "Use new common models instead.",
    replaceWith = ReplaceWith("CoordinateContainer", "org.maplibre.geojson.model.CoordinateContainer"),
)
interface CoordinateContainer<T> : Geometry {

    /**
     * the coordinates which define the geometry. Typically a list of points but for some geometry
     * such as polygon this can be a list of a list of points, thus the return is generic here.
     *
     * @return the [Point]s which make up the coordinates defining the geometry
     * @since 3.0.0
     */
    fun coordinates(): T
}