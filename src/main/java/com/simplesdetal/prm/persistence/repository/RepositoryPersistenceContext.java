package com.simplesdetal.prm.persistence.repository;

import jakarta.persistence.EntityManager;

public interface RepositoryPersistenceContext {

    EntityManager getEntityManager();


}
