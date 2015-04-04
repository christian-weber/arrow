package org.arrow.util

/**
 * Object utility class.
 *
 * @since 1.0.0
 * @author christian.weber
 */
final class Objects {

    /**
     * Returns the first non null argument of the given variable argument.
     *
     * @param objects the objects
     * @return Object
     */
    public static Object firstNonNull(Object...objects) {
        assert objects != null
        objects.find {it != null}
    }

}
