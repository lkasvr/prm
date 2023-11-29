package com.simplesdetal.prm.controller;

import com.simplesdetal.prm.persistence.model.Contato;
import com.simplesdetal.prm.persistence.repository.ContatoRepository;
import com.simplesdetal.prm.service.ContatoService;
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
 * Controlador para operações relacionadas aos contatos.
 */
@RestController
@RequestMapping("/contatos")
@Tag(name = "Contatos", description = "Endpoints para gerenciar informações sobre contatos.")
public class ContatoController {

    private final ContatoRepository repository;

    private final ContatoService contatoService;

    /**
     * Construtor do controlador.
     *
     * @param repository     Repositório de dados para contatos.
     * @param contatoService Serviço para contatos.
     */
    public ContatoController(final ContatoRepository repository, ContatoService contatoService) {
        this.repository = repository;
        this.contatoService = contatoService;
    }

    /**
     * Lista contatos com base no texto de pesquisa e nos campos especificados.
     *
     * @param text   Texto de pesquisa.
     * @param fields Campos a serem retornados.
     * @return Lista de contatos correspondentes à pesquisa.
     */
    @Operation(summary = "Listar contatos por texto e campos específicos")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Contato>> list(
            @Parameter(description = "Texto a ser pesquisado", required = true) @RequestParam("text") final String text,
            @Parameter(description = "Campos a serem retornados", required = false) @RequestParam(name = "fields", required = false) final Set<String> fields
    ) {
        return new ResponseEntity<>(repository.findText(text, fields), HttpStatus.OK);
    }

    /**
     * Obtém um contato pelo ID.
     *
     * @param id ID do contato a ser obtido.
     * @return O contato encontrado ou HttpStatus.NOT_FOUND se não encontrado.
     */
    @Operation(summary = "Obter um contato pelo ID")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(responseCode = "200", description = "Contato encontrado", content = @Content(schema = @Schema(implementation = Contato.class)))
    @ApiResponse(responseCode = "404", description = "Contato não encontrado")
    public ResponseEntity<Contato> get(@PathVariable("id") final Long id) {
        return new ResponseEntity<>(contatoService.read(id), HttpStatus.OK);
    }

    /**
     * Cria um novo contato.
     *
     * @param contato O contato a ser criado.
     * @return O contato criado e HttpStatus.CREATED.
     */
    @Operation(summary = "Criar um novo contato")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Contato> create(@RequestBody final Contato contato) {
        return new ResponseEntity<>(contatoService.create(contato), HttpStatus.CREATED);
    }

    /**
     * Atualiza um contato pelo ID.
     *
     * @param id      ID do contato a ser atualizado.
     * @param contato O contato com as atualizações.
     * @return O contato atualizado ou HttpStatus.BAD_REQUEST se o ID não existir.
     */
    @Operation(summary = "Atualizar um contato pelo ID")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(responseCode = "404", description = "Profissional não encontrado")
    public ResponseEntity<Contato> update(@PathVariable("id") final Long id, @RequestBody final Contato contato) {
        return new ResponseEntity<>(contatoService.update(id, contato), HttpStatus.OK);
    }

    /**
     * Exclui um contato pelo ID.
     *
     * @param id ID do contato a ser excluído.
     * @return HttpStatus.OK se o contato foi excluído, HttpStatus.BAD_REQUEST se o ID não existir.
     */
    @Operation(summary = "Excluir um contato pelo ID")
    @DeleteMapping(value = "/{id}")
    @ApiResponse(responseCode = "404", description = "Profissional não encorepositoryntrado")
    public ResponseEntity<Void> delete(@PathVariable("id") final Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
