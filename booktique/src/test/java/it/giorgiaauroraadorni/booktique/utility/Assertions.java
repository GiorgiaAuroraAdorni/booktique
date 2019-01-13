package it.giorgiaauroraadorni.booktique.utility;

import java.util.Set;

import static it.giorgiaauroraadorni.booktique.utility.Associations.associationEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Assertions {
    /**
     *
     * @param expected
     * @param actual
     * @param optionalId
     * @param <T>
     */
    public static <T extends EntityToDict> void assertAssociationEquals(Set<T> expected, Set<T> actual, boolean optionalId) {
        assertTrue(associationEquals(expected, actual, optionalId));
    }

    /**
     *
     * @param expected
     * @param actual
     * @param optionalId
     * @param <T>
     */
    public static <T extends EntityEqualsByAttributes> void assertAttributesEquals(T expected, T actual, boolean optionalId) {
        assertTrue(expected.equalsByAttributes(actual, optionalId));
    }
}
