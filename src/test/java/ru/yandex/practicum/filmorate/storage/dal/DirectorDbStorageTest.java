package ru.yandex.practicum.filmorate.storage.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DirectorDbStorageTest {
    private final DirectorDbStorage directorStorage;
    private Director director;

    @BeforeEach
    public void beforeEach() {
        // Очищаем таблицу перед каждым тестом
        directorStorage.deleteAll();

        // Создаем тестового режиссера
        director = new Director();
        director.setName("Тестовый Режиссер");
    }

    @Test
    public void testCreateDirector() {
        // Проверяем создание режиссера
        director = directorStorage.add(director);
        assertNotNull(director.getId(), "Режиссер не создан");

        Director directorDb = directorStorage.get(director.getId());
        assertNotNull(directorDb, "Режиссер не найден");
        assertEquals(director, directorDb, "Данные режиссера не совпадают");
    }

    @Test
    public void testUpdateDirector() {
        // Проверяем обновление режиссера
        director = directorStorage.add(director);
        assertNotNull(director.getId(), "Режиссер не создан");

        director.setName("Обновленный Режиссер");
        directorStorage.update(director);

        Director directorDb = directorStorage.get(director.getId());
        assertNotNull(directorDb, "Режиссер не найден");
        assertEquals(director, directorDb, "Данные режиссера не обновились");
    }

    @Test
    public void testDeleteDirector() {
        // Проверяем удаление режиссера
        director = directorStorage.add(director);
        assertNotNull(director.getId(), "Режиссер не создан");

        directorStorage.delete(director.getId());

        Director directorDb = directorStorage.get(director.getId());
        assertNull(directorDb, "Режиссер не удален");
    }

    @Test
    public void testFindDirectorById() {
        // Проверяем поиск режиссера по id
        director = directorStorage.add(director);
        assertNotNull(director.getId(), "Режиссер не создан");

        Director directorDb = directorStorage.get(director.getId());
        assertNotNull(directorDb, "Режиссер не найден");
        assertEquals(director, directorDb, "Данные режиссера не совпадают");
    }

    @Test
    public void testFindDirectors() {
        // Проверяем получение всех режиссеров
        director = directorStorage.add(director);
        assertNotNull(director.getId(), "Режиссер не создан");

        List<Director> directorList = new ArrayList<>();
        directorList.add(director);

        List<Director> directorListDb = directorStorage.getAll().stream().toList();

        assertNotNull(directorListDb, "Список режиссеров пуст");
        assertThat(directorListDb).containsExactlyElementsOf(directorList);
    }
}