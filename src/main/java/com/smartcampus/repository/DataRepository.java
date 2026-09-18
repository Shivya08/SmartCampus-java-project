package com.smartcampus.repository;

import java.util.List;
import java.util.Optional;

/**
 * Generic repository interface defining standard CRUD operations.
 * Demonstrates the use of Java Generics and the Repository Design Pattern.
 *
 * @param <T>  The entity type managed by this repository.
 * @param <ID> The type of the entity's identifier.
 */
public interface DataRepository<T, ID> {
    T save(T entity);

    Optional<T> findById(ID id);

    List<T> findAll();

    boolean deleteById(ID id);

    boolean existsById(ID id);

    long count();
}
