package it.giorgiaauroraadorni.booktique.utility;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;


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

        var dictsExpected = expected
                .stream()
                .map((e) -> e.entityToDict(optionalId))
                .collect(Collectors.toSet());

        var dictsActual = actual
                .stream()
                .map((e) -> e.entityToDict(optionalId))
                .collect(Collectors.toSet());

        return dictsExpected.equals(dictsActual);
    }

    public static <T extends EntityToDict> void assertAssociationEquals(Set<T> expected, Set<T> actual, boolean optionalId) {
        assertTrue(associationEquals(expected, actual, optionalId));
    }

    public static <T extends EntityEqualsByAttributes> void assertAttributesEquals(T expected, T actual, boolean optionalId) {
        assertTrue(expected.equalsByAttributes(actual, optionalId));
    }
}
