package org.maplibre.geojson.turf

/**
 * This class holds units prior to executing the Turf function. For example, if I intend to get the distance
 * between two GeoJson Points using [TurfMeasurement.distance] the third
 * optional parameter can define the output units.
 *
 *
 * Note that [TurfConversion.convertLength] can be used to transform
 * one unit to another, such as miles to feet.
 *
 *
 * @see [Turfjs documentation](http://turfjs.org/docs/)
 *
 * @since 1.2.0
 */
@Suppress("unused")
enum class TurfUnit(val factor: Double) {

    /**
     * The mile is an English unit of length of linear measure equal to 5,280 feet, or 1,760 yards,
     * and standardised as exactly 1,609.344 meters by international agreement in 1959.
     *
     * @since 1.2.0
     */
    MILES(factor = 3960.0),

    /**
     * The nautical mile per hour is known as the knot. Nautical miles and knots are almost
     * universally used for aeronautical and maritime navigation, because of their relationship with
     * degrees and minutes of latitude and the convenience of using the latitude scale on a map for
     * distance measuring.
     *
     * @since 1.2.0
     */
    NAUTICAL_MILES(factor = 3441.145),

    /**
     * The kilometer (American spelling) is a unit of length in the metric system, equal to one
     * thousand meters. It is now the measurement unit used officially for expressing distances
     * between geographical places on land in most of the world; notable exceptions are the United
     * States and the road network of the United Kingdom where the statute mile is the official unit
     * used.
     *
     *
     * In many Turf calculations, if a unit is not provided, the output value will fallback onto using
     * this unit. See [.UNIT_DEFAULT] for more information.
     *
     *
     * @since 1.2.0
     */
    KILOMETERS(factor = 6373.0),

    /**
     * The radian is the standard unit of angular measure, used in many areas of mathematics.
     *
     * @since 1.2.0
     */
    RADIANS(factor = 1.0),

    /**
     * A degree, is a measurement of a plane angle, defined so that a full rotation is 360 degrees.
     *
     * @since 1.2.0
     */
    DEGREES(factor = 57.2957795),

    /**
     * The inch (abbreviation: in or &quot;) is a unit of length in the (British) imperial and United
     * States customary systems of measurement now formally equal to 1/36th yard but usually
     * understood as 1/12th of a foot.
     *
     * @since 1.2.0
     */
    INCHES(factor = 250905600.0),

    /**
     * The yard (abbreviation: yd) is an English unit of length, in both the British imperial and US
     * customary systems of measurement, that comprises 3 feet or 36 inches.
     *
     * @since 1.2.0
     */
    YARDS(factor = 6969600.0),

    /**
     * The metre (international spelling) or meter (American spelling) is the base unit of length in
     * the International System of Units (SI).
     *
     * @since 1.2.0
     */
    METERS(factor = 6373000.0),

    /**
     * A centimeter (American spelling) is a unit of length in the metric system, equal to one
     * hundredth of a meter.
     *
     * @since 1.2.0
     */
    CENTIMETERS(factor = 6.373e+8),

    /**
     * The foot is a unit of length in the imperial and US customary systems of measurement.
     *
     * @since 1.2.0
     */
    FEET(factor = 20908792.65),

    /**
     * A centimetre (international spelling) is a unit of length in the metric system, equal to one
     * hundredth of a meter.
     *
     * @since 3.0.0
     */
    CENTIMETRES(factor = 6.373e+8),

    /**
     * The metre (international spelling) is the base unit of length in
     * the International System of Units (SI).
     *
     * @since 3.0.0
     */
    METRES(factor = 6373000.0),

    /**
     * The kilometre (international spelling) is a unit of length in the metric system, equal to one
     * thousand metres. It is now the measurement unit used officially for expressing distances
     * between geographical places on land in most of the world; notable exceptions are the United
     * States and the road network of the United Kingdom where the statute mile is the official unit
     * used.
     *
     * @since 3.0.0
     */
    KILOMETRES(factor = 6373.0);

    companion object {
        val DEFAULT = KILOMETERS
    }
}