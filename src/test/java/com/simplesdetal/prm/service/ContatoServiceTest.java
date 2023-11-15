package com.simplesdetal.prm.service;

import com.simplesdetal.prm.exception.NegocioException;
import com.simplesdetal.prm.persistence.model.Contato;
import com.simplesdetal.prm.persistence.model.ContatoType;
import com.simplesdetal.prm.persistence.model.Profissional;
import com.simplesdetal.prm.persistence.repository.ContatoRepository;
import com.simplesdetal.prm.persistence.repository.ProfissionalRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ContatoServiceTest {

    @Mock
    private ContatoRepository contatoRepository;

    @Mock
    private ProfissionalRepository profissionalRepository;

    @InjectMocks
    private ContatoService contatoService;

    @Test
    void testCreate() {

        Contato contato = new Contato();
        contato.setIdProfissional(1L);
        contato.setValor("123456789");
        contato.setNome(ContatoType.FIXO);


        Profissional profissional = new Profissional();
        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissional));


        when(contatoRepository.save(any(Contato.class))).thenAnswer(invocation -> {
            Contato savedContato = invocation.getArgument(0);
            //assertNotNull(savedContato.getId());
            assertNotNull(savedContato.getCreated());
            assertEquals(profissional, savedContato.getProfissional());
            return savedContato;
        });


        Contato createdContato = contatoService.create(contato);


        assertNotNull(createdContato);
        assertNotNull(createdContato.getCreated());
        assertEquals(profissional, createdContato.getProfissional());

        verify(contatoRepository, times(1)).save(any(Contato.class));
    }

    @Test
    void testUpdate() {

        Contato existingContato = new Contato();
        existingContato.setId(1L);
        existingContato.setIdProfissional(1L);
        existingContato.setValor("123456789");
        existingContato.setNome(ContatoType.FIXO);

        when(contatoRepository.findById(1L)).thenReturn(Optional.of(existingContato));
        when(contatoRepository.save(existingContato)).thenReturn(existingContato);

        Contato updatedContato = contatoService.update(existingContato);


        assertNotNull(updatedContato);
        assertEquals(existingContato.getId(), updatedContato.getId());
        assertEquals(existingContato.getIdProfissional(), updatedContato.getIdProfissional());

        verify(contatoRepository, times(1)).save(existingContato);
    }

    @Test
    void testUpdateWithNullId() {

        Contato contatoWithoutId = new Contato();
        contatoWithoutId.setIdProfissional(1L);
        contatoWithoutId.setValor("123456789");
        contatoWithoutId.setNome(ContatoType.FIXO);


        assertThrows(NegocioException.class, () -> contatoService.update(contatoWithoutId));
    }

    @Test
    void testUpdateWithIdProfissionalNull() {

        Contato contatoWithoutId = new Contato();
        contatoWithoutId.setValor("123456789");
        contatoWithoutId.setNome(ContatoType.FIXO);

        assertThrows(NegocioException.class, () -> contatoService.create(contatoWithoutId));
    }

    @Test
    void testUpdateWithNonexistentContato() {

        Contato nonexistentContato = new Contato();
        nonexistentContato.setId(1L);
        nonexistentContato.setIdProfissional(1L);
        nonexistentContato.setValor("123456789");
        nonexistentContato.setNome(ContatoType.FIXO);


        when(contatoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NegocioException.class, () -> contatoService.update(nonexistentContato));
    }
}
