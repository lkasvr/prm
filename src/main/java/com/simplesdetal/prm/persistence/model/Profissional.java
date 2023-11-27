package com.simplesdetal.prm.persistence.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profissional implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "id")
    @EqualsAndHashCode.Include
    @Schema(description = "ID do profissional.", example = "1")
    private Long id;

    @Column(name = "nome")
    @Schema(description = "Nome do profissional.", example = "João Silva")
    private String nome;

    @Convert(converter = CargoType.CargoTypeConverter.class)
    @Column(name = "cargo")
    @Schema(description = "Cargo do profissional.", example = "DESENVOLVEDOR, DESIGNER, SUPORTE e TESTER")
    private CargoType cargo;


    @Temporal(TemporalType.DATE)
    @Column(name = "nascimento")
    @Schema(description = "Data de nascimento do profissional.", example = "YYYY-MM-DDTHH:MM:SS.SSSZ")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
    private Date nascimento;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    @Schema(description = "Data de criação do profissional em UTC-0.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
    private Date created_at;

    @PrePersist
    protected void onCreate() {
        this.created_at = new Date();
    }

    @OneToMany(mappedBy = "profissional", cascade = CascadeType.DETACH)
    @Schema(description = "Conjunto de contatos associados ao profissional.")
    private Set<Contato> contatos;
}
