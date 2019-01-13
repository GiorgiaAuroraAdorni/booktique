package it.giorgiaauroraadorni.booktique.utility;

public interface EntityEqualsByAttributes {

    /**
     * Compare two instances and return {@code true} is the entities are equal to each other and {@code false}
     * otherwise.
     * @param expectedObject expected entity to compare.
     * @param optionalId if {@code true} the entities identifier is included in the comparison, otherwise no.
     * @return {@code true} if the entity instances are equal to each other and {@code false} otherwise
     */
    boolean equalsByAttributes(Object expectedObject, boolean optionalId);
}
