package com.simplesdetal.prm.persistence.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contato implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    @Schema(description = "ID do contato.", example = "1")
    private Long id;

    @Convert(converter = ContatoType.ContatoTypeConverter.class)
    @Column(name = "nome")
    @Schema(description = "Tipo de contato.", example = "FIXO, CASA, CELULAR e ESCRITORIO")
    private ContatoType nome;

    @Column(name = "contato")
    @Schema(description = "Valor do contato.", example = "(61)98765-4321")
    private String valor;

    @CreationTimestamp
    @Column(name = "created_date")
    @Schema(description = "Data de criação do contato em UTC-0.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
    private Date created;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "profissional_id")
    @Schema(description = "Profissional associado ao contato.")
    @JsonBackReference
    private Profissional profissional;

    @Transient
    @JsonIgnore
    private Long idProfissional;

}
