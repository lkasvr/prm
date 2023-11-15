package com.simplesdetal.prm.persistence.repository;

import com.simplesdetal.prm.persistence.model.Contato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface ContatoRepository extends JpaRepository<Contato, Long>, RepositoryPersistenceContext {

    default List<Contato> findText(final String text, final Set<String> fields) {
        return RepositoryUtil.findText(getEntityManager(),text, fields, Contato.class);
    }

}
