package it.giorgiaauroraadorni.booktique.utility;

import java.util.Map;

public interface EntityToDict {

    /**
     * @param optionalId if {@code true} the entity identifier is included in the dictionary, otherwise no.
     * @return a dictionary containing the attributes of an entity.
     */
    Map<String, Object> entityToDict(boolean optionalId);
}
