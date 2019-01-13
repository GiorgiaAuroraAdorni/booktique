package it.giorgiaauroraadorni.booktique.utility;

import java.util.Set;
import java.util.stream.Collectors;

public class Associations {
    /**
     * Compare two set of entity instances that represent the associations.
     * Return {@code true} is the associations are equal to each other and {@code false} otherwise.
     * During the comparison is considered all the attributes of the entities. To do that is use a dictionary
     * containing the fields of the reference entities.
     * @param expected set of entity instances.
     * @param actual set of entity instances.
     * @param optionalId if {@code true} the entities identifier is included in the comparison, otherwise no.
     * @param <T> the type of entities to compare.
     * @return {@code true} if the associations are equal to each other and {@code false} otherwise
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
}
