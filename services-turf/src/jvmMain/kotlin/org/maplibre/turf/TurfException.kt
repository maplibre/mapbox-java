package org.maplibre.turf

/**
 * This indicates conditions that a reasonable application might want to catch.
 *
 *
 * A [RuntimeException] specific to Turf calculation errors and is thrown whenever either an
 * unintended event occurs or the data passed into the method isn't sufficient enough to perform the
 * calculation.
 *
 *
 * @see [Turfjs documentation](http://turfjs.org/docs/)
 *
 * @since 1.2.0
 */
@Deprecated(
    message = "Use new common Turf utils instead.",
    replaceWith = ReplaceWith("TurfException", "org.maplibre.geojson.turf.TurfException"),
)
class TurfException
/**
 * A form of [RuntimeException] that indicates conditions that a reasonable application
 * might want to catch.
 *
 * @param message the detail message (which is saved for later retrieval by the
 * [.getMessage] method).
 * @since 1.2.0
 */
    (message: String?) : RuntimeException(message)