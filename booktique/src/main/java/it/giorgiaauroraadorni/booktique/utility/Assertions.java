package it.giorgiaauroraadorni.booktique.utility;

import java.util.Set;
import java.util.stream.Collectors;

public class Assertions {

    public static <T extends EntityToDict> boolean assertAssociationEquals(Set<T> expected, Set<T> actual, boolean optionalId) {
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
}
