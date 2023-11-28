package com.simplesdetal.prm.persistence.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.simplesdetal.prm.persistence.entityListeners.ProfissionalListener;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import lombok.*;
import org.hibernate.annotations.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.ReadOnlyProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
@Data
@SQLDelete(sql = "update profissional set active = 0 where id = ?")
@Where(clause = "active = 1")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(ProfissionalListener.class)
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
    @Column(name = "nascimento", nullable = false)
    @Schema(description = "Data de nascimento do profissional.", example = "YYYY-MM-DDTHH:MM:SS.SSSZ")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
    private Date nascimento;

    @CreationTimestamp
    @Column(name = "created_date", nullable = false, updatable = false)
    @Schema(description = "Data de criação do profissional em UTC-0.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
    private Date created_at;

    @OneToMany(mappedBy = "profissional", cascade = CascadeType.DETACH)
    @Schema(description = "Conjunto de contatos associados ao profissional.")
    private Set<Contato> contatos;

    @ColumnDefault("1")
    @Column(name = "active", nullable = false, updatable = false)
    @JsonIgnore
    private Integer active;

}
