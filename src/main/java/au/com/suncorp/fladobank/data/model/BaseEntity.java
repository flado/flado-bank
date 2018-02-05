package au.com.suncorp.fladobank.data.model;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Base entity class to be extended by all other entities to inherit Id generation through default constructor
 */
public abstract class BaseEntity {

    protected final ThreadLocalRandom IdsGenerator = ThreadLocalRandom.current();

    /**
     * Immutable key for this entity
     */
    private final Long id;

    /**
     * Default constructor initializes the id with a positive long random value
     */
    BaseEntity() {
        this.id = IdsGenerator.nextLong(1, Long.MAX_VALUE);
    }

    public Long getId() {
        return id;
    }

}
