/**
 * Contains a factory interface <b>EntityFactory</b> used by the test suite to creates instances.
 * In particular implement the {@code createValidEntity()} method that creates a default instance, and the {@code
 * createValidEntities()}
 * method the creates a list of valid instances.
 * For each domain class, there’s a factory that implements the <b>EntityFactory</b> methods.
 * All the factories are instantiated using default values for all the fields.
 * In order to respect the unique constraint in case of the creation of multiple instances is used an index as param for the methods.
 */
package it.giorgiaauroraadorni.booktique.model;
