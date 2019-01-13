/**
 * Contains repository interfaces for all models, excluding abstract classes.
 * <i>Spring Data JPA</i> create automatically a repository implementations from the repository interface.
 * Extending <b>JpaRepository</b>** every repository inherits several methods for working with entity persistence,
 * including methods that implement CRUD operations such as save and delete, but also for entity search operations.
 * <i>Spring Data JPA</i> also allows defining other custom query methods by simply declaring their method signature.
 * For all models, customized {@code findBy{...}()} methods have been implemented.
 * For example in the case of <b>BookRepository</b> the {@code findByAuthors_Name()} method.
 */
package it.giorgiaauroraadorni.booktique.repository;
