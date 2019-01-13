package it.giorgiaauroraadorni.booktique.utility;

import java.util.Set;

import static it.giorgiaauroraadorni.booktique.utility.Associations.associationEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Assertions {
    /**
     * <em>Asserts</em> that the {@code expected} set of entities and {@code actual} ones are equal.
     * Each of the entity sets is the attribute that identifies the association between entities.
     * @param expected set of entities.
     * @param actual set of entities.
     * @param optionalId if {@code true} the entities identifier is included in the comparison, otherwise no.
     * @param <T> type of the sets of entities.
     */
    public static <T extends EntityToDict> void assertAssociationEquals(Set<T> expected, Set<T> actual, boolean optionalId) {
        assertTrue(associationEquals(expected, actual, optionalId));
    }

    /**
     * <em>Asserts</em> that the {@code expected} attributes of an entity and {@code actual} one are equal.
     * @param expected entity.
     * @param actual entity.
     * @param optionalId if {@code true} the entities identifier is included in the comparison, otherwise no.
     * @param <T> type of the entities.
     */
    public static <T extends EntityEqualsByAttributes> void assertAttributesEquals(T expected, T actual, boolean optionalId) {
        assertTrue(expected.equalsByAttributes(actual, optionalId));
    }
}
