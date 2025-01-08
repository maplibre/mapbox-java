package org.maplibre.geojson.turf

/**
 * This indicates conditions that a reasonable application might want to catch.
 *
 * A [RuntimeException] specific to Turf calculation errors and is thrown whenever either an
 * unintended event occurs or the data passed into the method isn't sufficient enough to perform the
 * calculation.
 *
 * @param message the detail message (which is saved for later retrieval by the
 *  [.getMessage] method).
 *
 * @see [Turfjs documentation](http://turfjs.org/docs/)
 *
 * @since 1.2.0
 */
open class TurfException(message: String?) : RuntimeException(message)
