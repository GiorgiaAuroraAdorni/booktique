package it.giorgiaauroraadorni.booktique.utility;

public interface EntityEqualsByAttributes {

    /**
     *
     * @param expectedObject
     * @param optionalId
     * @return
     */
    boolean equalsByAttributes(Object expectedObject, boolean optionalId);
}
