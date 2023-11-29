package com.simplesdetal.prm.persistence.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.Getter;

@Getter
public enum ContatoType {

    FIXO("fixo"),
    CASA("casa"),
    CELULAR("celular"),
    ESCRITORIO("escritório");

    private final String nome;

    ContatoType(final String nome) {
        this.nome = nome;
    }

    @Converter(autoApply = true)
    public static class ContatoTypeConverter implements AttributeConverter<ContatoType, String> {

        @Override
        public String convertToDatabaseColumn(ContatoType contatoType) {
            return contatoType == null ? null : contatoType.getNome();
        }

        @Override
        public ContatoType convertToEntityAttribute(String value) {
            if (value == null) return null;

            for (ContatoType contatoType : ContatoType.values()) {
                if (contatoType.getNome().equalsIgnoreCase(value)) {
                    return contatoType;
                }
            }

            throw new IllegalArgumentException("Unknown database value: " + value);
        }
    }
}
