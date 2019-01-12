package it.giorgiaauroraadorni.booktique.utility;

import java.util.Map;

public interface EntityToDict {

    /**
     *
     * @param optionalId
     * @return
     */
    Map<String, Object> entityToDict(boolean optionalId);
}
