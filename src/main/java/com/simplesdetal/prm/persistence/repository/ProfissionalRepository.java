package com.simplesdetal.prm.persistence.repository;

import com.simplesdetal.prm.persistence.model.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ProfissionalRepository extends JpaRepository<Profissional, Long>, RepositoryPersistenceContext {

    default List<Profissional> findText(final String text, final Set<String> fields) {
        return RepositoryUtil.findText(getEntityManager(),text, fields, Profissional.class);
    }

}
