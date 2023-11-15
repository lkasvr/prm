package com.simplesdetal.prm.service;

import com.simplesdetal.prm.exception.NegocioException;
import com.simplesdetal.prm.persistence.model.Contato;
import com.simplesdetal.prm.persistence.model.Profissional;
import com.simplesdetal.prm.persistence.repository.ContatoRepository;
import com.simplesdetal.prm.persistence.repository.ProfissionalRepository;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Service
public class ContatoService {

    private final ContatoRepository repository;

    private final  ProfissionalRepository profissionalRepository;

    public ContatoService(ContatoRepository repository, ProfissionalRepository profissionalRepository) {
        this.repository = repository;
        this.profissionalRepository = profissionalRepository;
    }

    public Contato create(final Contato contato) {
        contato.setId(null);
        contato.setCreated(new Date());
        contato.setProfissional(getProfissional(contato));
        return repository.save(contato);
    }

    public Contato update(final Contato contato) {
        final Contato current = getContato(contato);
        current.setValor(contato.getValor());
        current.setNome(contato.getNome());
        return repository.save(current);
    }


    private Contato getContato(final Contato contato) {
        if (Objects.isNull(contato) || Objects.isNull(contato.getId())) {
            throw new NegocioException("A informação do contato é obrigatória.");
        }

        final Optional<Contato> optional = repository.findById(contato.getId());
        return optional.orElseThrow( () -> new NegocioException("O contato informado não existe em nosso banco de dados."));
    }

    private Profissional getProfissional(final Contato contato) {
        if (Objects.isNull(contato.getIdProfissional())) {
            throw new NegocioException("A informação do profissional é obrigatória.");
        }

        final Optional<Profissional> optional = profissionalRepository.findById(contato.getIdProfissional());
        return optional.orElseThrow( () -> new NegocioException("O profissional informado não existe em nosso banco de dados."));
    }


}
