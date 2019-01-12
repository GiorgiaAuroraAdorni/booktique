package it.giorgiaauroraadorni.booktique.utility;

import java.util.Set;
import java.util.stream.Collectors;

public class Assertions {

    /**
     *
     * @param expected
     * @param actual
     * @param optionalId
     * @param <T>
     * @return
     */
    public static <T extends EntityToDict> boolean associationEquals(Set<T> expected, Set<T> actual,
                                                                     boolean optionalId) {
        if (expected == actual) return true;
        if (expected == null || actual == null) return false;

        var dictsA = expected
                .stream()
                .map((e) -> e.entityToDict(optionalId))
                .collect(Collectors.toSet());

        var dictsB = actual
                .stream()
                .map((e) -> e.entityToDict(optionalId))
                .collect(Collectors.toSet());

        return dictsA.equals(dictsB);
    }

    public static <T extends EntityToDict> void assertAssociationEquals(Set<T> expected, Set<T> actual,
                                                                     boolean optionalId) {
        assertTrue(associationEquals(expected, actual, optionalId));
    }

    /**
     *
     * @param expected
     * @param actual
     * @param optionalId
     * @param <T>
     * @return
     */
    public static <T extends EntityEqualsByAttributes> boolean assertAttributesEquals(T expected, T actual, boolean optionalId) {
        if (expected == actual) return true;
        if (expected == null || actual == null) return false;

        return expected.equalsByAttributes(actual, optionalId);
    }
}
