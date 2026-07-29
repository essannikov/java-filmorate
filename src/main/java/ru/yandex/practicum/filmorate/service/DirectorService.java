package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FilmDirectorStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;
    private final FilmDirectorStorage filmDirectorStorage;

    public Collection<Director> getDirectorAll() {
        return directorStorage.getAll();
    }

    public Director getDirector(Long id) {
        checkDirectorId(id);
        Director director = directorStorage.get(id);
        checkDirector(director, id);
        return director;
    }

    public Director addDirector(Director director) {
        return directorStorage.add(director);
    }

    public Director updateDirector(Director newDirector) {
        checkDirectorId(newDirector.getId());
        Director directorUpdate = directorStorage.update(newDirector);
        checkDirector(directorUpdate, newDirector.getId());
        return directorUpdate;
    }

    public Director deleteDirector(Long id) {
        checkDirectorId(id);
        Director director = directorStorage.get(id);
        checkDirector(director, id);

        // Удаляем связи с фильмами
        filmDirectorStorage.deleteAllByDirectorId(id);

        Director deletedDirector = directorStorage.delete(id);
        if (deletedDirector == null) {
            throw new NotFoundException(String.format("Не удалось удалить режиссера с id = %d", id));
        }
        return deletedDirector;
    }

    protected void checkDirectorId(Long id) {
        if (id == null) {
            throw new ValidationException("Не задан id режиссера");
        }
    }

    protected void checkDirector(Director director, Long id) {
        if (director == null) {
            throw new NotFoundException(String.format("Режиссер с id = %d не найден", id));
        }
    }
}