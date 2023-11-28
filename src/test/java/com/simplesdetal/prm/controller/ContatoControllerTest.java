package com.simplesdetal.prm.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplesdetal.prm.persistence.model.CargoType;
import com.simplesdetal.prm.persistence.model.Contato;
import com.simplesdetal.prm.persistence.model.ContatoType;
import com.simplesdetal.prm.persistence.model.Profissional;
import com.simplesdetal.prm.persistence.repository.ContatoRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class ContatoControllerTest {

    private static final Set<Contato> CONTATOS = new HashSet<>();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private ContatoRepository repository;

    @InjectMocks
    private ContatoController contatoController;

    @InjectMocks
    private ProfissionalController profissionalController;


    Contato bindContato(final ResultActions result) throws Exception {
        final String responseContent = result.andReturn().getResponse().getContentAsString();
        final Contato contato = objectMapper.readValue(responseContent, Contato.class);
        CONTATOS.remove(contato);
        CONTATOS.add(contato);
        return objectMapper.readValue(responseContent, Contato.class);

    }
    Contato create(final Contato contato) throws Exception {
        final ResultActions result = mockMvc.perform(post("/contatos")
                        .content(objectMapper.writeValueAsString(contato))
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        return bindContato(result);

    }

    void assertContato(final Contato contato, final Contato responseContato) throws Exception {
        assertThat(responseContato.getId()).isNotNull();
        assertThat(responseContato.getValor()).isEqualTo(contato.getValor());
        assertThat(responseContato.getNome()).isEqualTo(contato.getNome());

    }

    Profissional bindProfissional(final ResultActions result) throws Exception {
        final String responseContent = result.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(responseContent, Profissional.class);

    }
    Profissional create(final Profissional profissional) throws Exception {
        final ResultActions result = mockMvc.perform(post("/profissionais")
                        .content(objectMapper.writeValueAsString(profissional))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        return bindProfissional(result);

    }

    void assertProfissional(final Profissional profissional, final Profissional responseProfissional) throws Exception {
        assertThat(responseProfissional.getId()).isNotNull();
        assertThat(responseProfissional.getCargo()).isEqualTo(profissional.getCargo());
        assertThat(responseProfissional.getNome()).isEqualTo(profissional.getNome());
    }

    Profissional createProfissional() throws Exception {

        final Profissional profissional = Profissional.builder()
                .nome("testing create")
                .cargo(CargoType.DESENVOLVEDOR)
                .active(1)
                .build();

        final Profissional responseProfissional = create(profissional);
        assertProfissional(profissional, responseProfissional);
        return responseProfissional;
    }

    @Test
    void testCreate() throws Exception {

        final Profissional profissional = createProfissional();

        final Contato contato = Contato.builder()
                .valor("testing create")
                .nome(ContatoType.FIXO)
                .idProfissional(profissional.getId())
                .build();

        final Contato responseContato = create(contato);
        assertContato(contato, responseContato);

    }

    @Test
    void testGet() throws Exception {

        final Profissional profissional = createProfissional();

        final Contato contatoCreate = Contato.builder()
                .valor("testing get")
                .nome(ContatoType.CASA)
                .idProfissional(profissional.getId())
                .build();

        final Contato responseContatoCreate = create(contatoCreate);

        assertContato(contatoCreate, responseContatoCreate);

        final ResultActions result = mockMvc.perform(get("/contatos/" + responseContatoCreate.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        final Contato responseContato = bindContato(result);


        assertContato(responseContato, responseContatoCreate);
        assertThat(responseContato.getId()).isEqualTo(responseContatoCreate.getId());
    }

    @Test
    void testGetNotFound() throws Exception {

        mockMvc.perform(get("/contatos/3")
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());


    }

    @Test
    void testUpdate() throws Exception {

        final Profissional profissional = createProfissional();

        final Contato contatoCreate = Contato.builder()
                .valor("testing update")
                .nome(ContatoType.CASA)
                .idProfissional(profissional.getId())
                .build();

        final Contato responseContatoCreate = create(contatoCreate);

        assertContato(contatoCreate, responseContatoCreate);

        responseContatoCreate.setValor("testing update");


        final ResultActions result = mockMvc.perform(put("/contatos/"+ responseContatoCreate.getId())
                        .content(objectMapper.writeValueAsString(responseContatoCreate))
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        final Contato responseContato = bindContato(result);

        assertContato(responseContato, responseContatoCreate);
        assertThat(responseContato.getId()).isEqualTo(responseContatoCreate.getId());
    }

    @Test
    void testUpdateBadRequest() throws Exception {

        final Contato contatoCreate = Contato.builder()
                .id(999999L)
                .valor("testing update not found")
                .nome(ContatoType.CASA)
                .build();

        mockMvc.perform(put("/contatos/" + contatoCreate.getId())
                .content(objectMapper.writeValueAsString(contatoCreate))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());


    }


    @Test
    void testDelete() throws Exception {

        final Profissional profissional = createProfissional();

        final Contato contatoCreate = Contato.builder()
                .valor("testing delete")
                .nome(ContatoType.CELULAR)
                .idProfissional(profissional.getId())
                .build();

        final Contato responseContatoCreate = create(contatoCreate);

        assertContato(contatoCreate, responseContatoCreate);


        mockMvc.perform(delete("/contatos/" + responseContatoCreate.getId()))
                .andExpect(status().isOk());

    }

    @Test
    void testDeleteBadRequest() throws Exception {

        final Contato contatoCreate = Contato.builder()
                .id(999999L)
                .valor("testing update not found")
                .nome(ContatoType.CASA)
                .build();

        mockMvc.perform(delete("/contatos/" + contatoCreate.getId())
                .content(objectMapper.writeValueAsString(contatoCreate))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());


    }

    @Test
    void testListResult() throws Exception {

        final Profissional profissional = createProfissional();

        final Contato contatoCreate1 = Contato.builder()
                .valor("testing list x")
                .nome(ContatoType.CELULAR)
                .created(new Date())
                .idProfissional(profissional.getId())
                .build();

        final Contato responseContatoCreate1 = create(contatoCreate1);

        assertContato(contatoCreate1, responseContatoCreate1);


        final Contato contatoCreate2 = Contato.builder()
                .valor("testing list 2")
                .nome(ContatoType.CASA)
                .created(new Date())
                .idProfissional(profissional.getId())
                .build();

        final Contato responseContatoCreate2 = create(contatoCreate2);

        assertContato(contatoCreate2, responseContatoCreate2);

        final Contato contatoCreate3 = Contato.builder()
                .valor("testing list 3")
                .nome(ContatoType.FIXO)
                .created(new Date())
                .idProfissional(profissional.getId())
                .build();

        final Contato responseContatoCreate3 = create(contatoCreate3);

        assertContato(contatoCreate3, responseContatoCreate3);


        final Contato contatoCreate4 = Contato.builder()
                .valor("asasasasasasasas 4")
                .nome(ContatoType.ESCRITORIO)
                .created(new Date())
                .idProfissional(profissional.getId())
                .build();

        final Contato responseContatoCreate4 = create(contatoCreate4);

        assertContato(contatoCreate4, responseContatoCreate4);


        final ResultActions result = mockMvc.perform(get("/contatos")
                        .param("text", "asasasasasasasas")
                        .param("fields", "nome", "valor")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        final String responseContent = result.andReturn().getResponse().getContentAsString();

        final List<Contato> contatos = objectMapper.readValue(responseContent,new TypeReference<List<Contato>>() {});
        final Contato responseContato = contatos.get(0);
        assertThat(responseContato.getId()).isNull();
        assertThat(responseContato.getValor()).isNotNull();
        assertThat(responseContato.getNome()).isNotNull();
        assertThat(responseContato.getCreated()).isNull();
    }


}
