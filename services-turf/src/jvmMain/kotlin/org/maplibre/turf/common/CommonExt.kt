package org.maplibre.turf.common

import org.maplibre.geojson.turf.TurfUnit
import org.maplibre.turf.TurfConstants

fun String.toUnit(): TurfUnit {
    return when (this) {
        TurfConstants.UNIT_MILES -> TurfUnit.MILES
        TurfConstants.UNIT_NAUTICAL_MILES -> TurfUnit.NAUTICAL_MILES
        TurfConstants.UNIT_KILOMETERS -> TurfUnit.KILOMETERS
        TurfConstants.UNIT_RADIANS -> TurfUnit.RADIANS
        TurfConstants.UNIT_DEGREES -> TurfUnit.DEFAULT
        TurfConstants.UNIT_INCHES -> TurfUnit.INCHES
        TurfConstants.UNIT_YARDS -> TurfUnit.YARDS
        TurfConstants.UNIT_METERS -> TurfUnit.METERS
        TurfConstants.UNIT_CENTIMETERS -> TurfUnit.CENTIMETERS
        TurfConstants.UNIT_FEET -> TurfUnit.FEET
        TurfConstants.UNIT_CENTIMETRES -> TurfUnit.CENTIMETRES
        TurfConstants.UNIT_METRES -> TurfUnit.METRES
        TurfConstants.UNIT_KILOMETRES -> TurfUnit.KILOMETERS
        else -> throw IllegalArgumentException("Invalid unit")
    }
}