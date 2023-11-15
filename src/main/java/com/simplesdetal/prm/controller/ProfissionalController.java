package com.simplesdetal.prm.controller;

import com.simplesdetal.prm.persistence.model.Profissional;
import com.simplesdetal.prm.persistence.repository.ProfissionalRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Controlador para operações relacionadas aos profissionais.
 */
@RestController
@RequestMapping("/profissionais")
@Tag(name = "Profissionais", description = "Endpoints para gerenciar informações sobre profissionais.")
public class ProfissionalController {

    private final ProfissionalRepository repository;

    /**
     * Construtor do controlador.
     *
     * @param repository Repositório de dados para profissionais.
     */
    public ProfissionalController(final ProfissionalRepository repository) {
        this.repository = repository;
    }

    /**
     * Lista profissionais com base no texto de pesquisa e nos campos especificados.
     *
     * @param text   Texto de pesquisa.
     * @param fields Campos a serem retornados.
     * @return Lista de profissionais correspondentes à pesquisa.
     */
    @Operation(summary = "Listar profissionais por texto e campos específicos")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Profissional>> list(
            @Parameter(description = "Texto a ser pesquisado", required = true) @RequestParam("text") final String text,
            @Parameter(description = "Campos a serem retornados", required = false) @RequestParam(name = "fields", required = false) final Set<String> fields
    ) {
        return new ResponseEntity<>(repository.findText(text, fields), HttpStatus.OK);
    }

    /**
     * Obtém um profissional pelo ID.
     *
     * @param id ID do profissional a ser obtido.
     * @return O profissional encontrado ou HttpStatus.NOT_FOUND se não encontrado.
     */
    @Operation(summary = "Obter um profissional pelo ID")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(responseCode = "200", description = "Profissional encontrado", content = @Content(schema = @Schema(implementation = Profissional.class)))
    @ApiResponse(responseCode = "404", description = "Profissional não encontrado")
    public ResponseEntity<Optional<Profissional>> get(@PathVariable("id") final Long id) {
        return repository.existsById(id)
                ? new ResponseEntity<>(repository.findById(id), HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Cria um novo profissional.
     *
     * @param profissional O profissional a ser criado.
     * @return O profissional criado e HttpStatus.CREATED.
     */
    @Operation(summary = "Criar um novo profissional")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Profissional> create(@RequestBody final Profissional profissional) {
        profissional.setId(null);
        return new ResponseEntity<>(repository.save(profissional), HttpStatus.CREATED);
    }

    /**
     * Atualiza um profissional pelo ID.
     *
     * @param id          ID do profissional a ser atualizado.
     * @param profissional O profissional com as atualizações.
     * @return O profissional atualizado ou HttpStatus.BAD_REQUEST se o ID não existir.
     */
    @Operation(summary = "Atualizar um profissional pelo ID")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(responseCode = "404", description = "Profissional não encontrado")
    public ResponseEntity<Profissional> update(@PathVariable("id") final Long id, @RequestBody final Profissional profissional) {
        profissional.setId(id);
        return repository.existsById(id)
                ? new ResponseEntity<>(repository.save(profissional), HttpStatus.OK)
                : new ResponseEntity<>(repository.save(profissional), HttpStatus.BAD_REQUEST);
    }

    /**
     * Exclui um profissional pelo ID.
     *
     * @param id ID do profissional a ser excluído.
     * @return HttpStatus.OK se o profissional foi excluído, HttpStatus.BAD_REQUEST se o ID não existir.
     */
    @Operation(summary = "Excluir um profissional pelo ID")
    @DeleteMapping(value = "/{id}")
    @ApiResponse(responseCode = "404", description = "Profissional não encontrado")
    public ResponseEntity<Void> delete(@PathVariable("id") final Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
