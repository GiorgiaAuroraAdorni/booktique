package it.giorgiaauroraadorni.booktique.model;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface EntityFactory<T> {

    /**
     * Create a valid entity that will be used in the test.
     * @param idx will be used to satisfy uniqueness constraints.
     * @return the created entity.
     */
    T createValidEntity(int idx);

    default T createValidEntity() {
        return createValidEntity(0);
    }

    /**
     * Create a list of valid entities that will be used in the test.
     * @param count corresponds to the number of entities that will be generated.
     *              It will also be used to satisfy the uniqueness constraints between the entities created.
     * @return the list of created entities.
     */
    default List<T> createValidEntities(int count) {
        return IntStream
                .range(0, count)
                .mapToObj(this::createValidEntity)
                .collect(Collectors.toList());
    }
}
