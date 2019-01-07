package it.giorgiaauroraadorni.booktique.model;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface EntityTestFactory<T> {

    /**
     *
     * @param idx
     * @return
     */
    T createValidEntity(int idx);

    /**
     *
     * @param entity
     */
    void updateValidEntity(T entity);

    default T createValidEntity() {
        return createValidEntity(0);
    }

    default List<T> createValidEntities(int count) {
        return IntStream
                .range(0, count)
                .mapToObj(this::createValidEntity)
                .collect(Collectors.toList());
    }
}
