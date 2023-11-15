package com.simplesdetal.prm.persistence.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.Getter;

@Getter
public enum CargoType {

    DESENVOLVEDOR("Desenvolvedor"),
    DESIGNER("Designer"),
    SUPORTE("Suporte"),
    TESTER("Tester");

    private final String nome;

    private

    CargoType(final String nome) {
        this.nome = nome;
    }

    @Converter(autoApply = true)
    public static class CargoTypeConverter implements AttributeConverter<CargoType, String> {

        @Override
        public String convertToDatabaseColumn(CargoType cargoType) {
            return cargoType == null ? null : cargoType.getNome();
        }

        @Override
        public CargoType convertToEntityAttribute(String value) {
            if (value == null) {
                return null;
            }

            for (CargoType cargoType : CargoType.values()) {
                if (cargoType.getNome().equals(value)) {
                    return cargoType;
                }
            }

            throw new IllegalArgumentException("Unknown database value: " + value);
        }
    }

}
