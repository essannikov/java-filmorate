package ru.yandex.practicum.filmorate.model.enums;

public enum EventType {
    LIKE,
    REVIEW,
    FRIEND;

    public static EventType getEnumFromString(String value) {
        try {
            return EventType.valueOf(value);
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }
}
