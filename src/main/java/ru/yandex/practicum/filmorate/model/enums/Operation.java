package ru.yandex.practicum.filmorate.model.enums;

public enum Operation {
    REMOVE,
    ADD,
    UPDATE;

    public static Operation getEnumFromString(String value) {
        try {
            return Operation.valueOf(value);
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }
}
