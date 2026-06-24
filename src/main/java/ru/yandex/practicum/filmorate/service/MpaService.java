package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage mpaStorage;

    public Collection<Mpa> getMpaAll() {
        return mpaStorage.getAll();
    }

    public Mpa getMpa(Long id) {
        checkMpaId(id);
        Mpa mpa = mpaStorage.get(id);
        checkMpa(mpa, id);

        return mpa;
    }

    protected void checkMpaId(Long id) {
        if (id == null) {
            throw new ValidationException("Не задан id MPA");
        }
    }

    protected void checkMpa(Mpa mpa, Long id) {
        if (mpa == null) {
            throw new NotFoundException(String.format("MPA с id = %d не найден", id));
        }
    }
}
