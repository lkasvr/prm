package com.simplesdetal.prm.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplesdetal.prm.persistence.model.CargoType;
import com.simplesdetal.prm.persistence.model.Profissional;
import com.simplesdetal.prm.persistence.repository.ContatoRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
class ProfissionalControllerTest {

    private static final Set<Profissional> PROFISSIONAIS = new HashSet<>();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private ContatoRepository repository;

    @InjectMocks
    private ProfissionalController profissionalController;


    Profissional bindProfissional(final ResultActions result) throws Exception {
        final String responseContent = result.andReturn().getResponse().getContentAsString();
        final Profissional profissional = objectMapper.readValue(responseContent, Profissional.class);
        PROFISSIONAIS.remove(profissional);
        PROFISSIONAIS.add(profissional);
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

    @Test
    void testCreate() throws Exception {

        final Profissional profissional = Profissional.builder()
                .nome("testing create")
                .cargo(CargoType.DESENVOLVEDOR)
                .build();

        final Profissional responseProfissional = create(profissional);
        assertProfissional(profissional, responseProfissional);

    }

    @Test
    void testGet() throws Exception {

        final Profissional profissionalCreate = Profissional.builder()
                .nome("testing get")
                .cargo(CargoType.DESIGNER)
                .build();

        final Profissional responseProfissionalCreate = create(profissionalCreate);

        assertProfissional(profissionalCreate, responseProfissionalCreate);

        final ResultActions result = mockMvc.perform(get("/profissionais/" + responseProfissionalCreate.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        final Profissional responseProfissional = bindProfissional(result);


        assertProfissional(responseProfissional, responseProfissionalCreate);
        assertThat(responseProfissional.getId()).isEqualTo(responseProfissionalCreate.getId());
    }

    @Test
    void testGetNotFound() throws Exception {

        mockMvc.perform(get("/contatos/9999")
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());


    }

    @Test
    void testUpdate() throws Exception {

        final Profissional profissionalCreate = Profissional.builder()
                .nome("testing update")
                .cargo(CargoType.TESTER)
                .build();

        final Profissional responseProfissionalCreate = create(profissionalCreate);

        assertProfissional(profissionalCreate, responseProfissionalCreate);

        responseProfissionalCreate.setNome("testing update");


        final ResultActions result = mockMvc.perform(put("/profissionais/"+ responseProfissionalCreate.getId())
                        .content(objectMapper.writeValueAsString(responseProfissionalCreate))
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        final Profissional responsProfissional = bindProfissional(result);

        assertProfissional(responsProfissional, responseProfissionalCreate);
        assertThat(responsProfissional.getId()).isEqualTo(responseProfissionalCreate.getId());
    }

    @Test
    void testUpdateBadRequest() throws Exception {

        final Profissional profissionalCreate = Profissional.builder()
                .id(999999L)
                .nome("testing update not found")
                .cargo(CargoType.TESTER)
                .build();


        mockMvc.perform(put("/profissionais/" + profissionalCreate.getId())
                .content(objectMapper.writeValueAsString(profissionalCreate))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());


    }


    @Test
    void testDelete() throws Exception {

        final Profissional profissionalCreate = Profissional.builder()
                .nome("testing update not found")
                .cargo(CargoType.SUPORTE)
                .build();


        final Profissional responseContatoCreate = create(profissionalCreate);

        assertProfissional(profissionalCreate, responseContatoCreate);


        mockMvc.perform(delete("/profissionais/" + responseContatoCreate.getId()))
                .andExpect(status().isOk());

    }

    @Test
    void testDeleteBadRequest() throws Exception {

        final Profissional profissionalCreate = Profissional.builder()
                .id(999999L)
                .nome("testing update not found")
                .cargo(CargoType.SUPORTE)
                .build();

        mockMvc.perform(delete("/profissionais/" + profissionalCreate.getId())
                .content(objectMapper.writeValueAsString(profissionalCreate))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());


    }

    @Test
    void testListResult() throws Exception {

        final Profissional profissionalCreate1 = Profissional.builder()
                .nome("testing list x")
                .cargo(CargoType.DESIGNER)
                .build();


        final Profissional responseProfissionalCreate1 = create(profissionalCreate1);

        assertProfissional(profissionalCreate1, responseProfissionalCreate1);


        final Profissional profissionalCreate2 = Profissional.builder()
                .nome("testing list 2")
                .cargo(CargoType.TESTER)
                .build();


        final Profissional responseProfissionalCreate2 = create(profissionalCreate2);

        assertProfissional(profissionalCreate2, responseProfissionalCreate2);

        final Profissional profissionalCreate3 = Profissional.builder()
                .nome("testing list 3")
                .cargo(CargoType.DESENVOLVEDOR)
                .build();


        final Profissional responseProfissionalCreate3 = create(profissionalCreate3);

        assertProfissional(profissionalCreate3, responseProfissionalCreate3);

        final Profissional profissionalCreate4 = Profissional.builder()
                .nome("asasasasasasasas 4")
                .cargo(CargoType.SUPORTE)
                .build();


        final Profissional responseProfissionalCreate4 = create(profissionalCreate4);

        assertProfissional(profissionalCreate4, responseProfissionalCreate4);


        final ResultActions result = mockMvc.perform(get("/profissionais")
                        .param("text", "asasasasasasasas")
                        .param("fields", "nome", "cargo")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        final String responseContent = result.andReturn().getResponse().getContentAsString();

        final List<Profissional> profissionais = objectMapper.readValue(responseContent,new TypeReference<>() {});
        final Profissional responseProfissional = profissionais.get(0);
        assertThat(responseProfissional.getId()).isNull();
        assertThat(responseProfissional.getNome()).isNotNull();
        assertThat(responseProfissional.getCargo()).isNotNull();
        assertThat(responseProfissional.getCreated()).isNull();
    }


}
