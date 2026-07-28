package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.*;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.storage.dal.GenreDbStorage;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;
    private final FeedStorage feedStorage;
    private final LikeStorage likeStorage;
    private final FilmStorage filmStorage;
    private final GenreDbStorage genreStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final DirectorStorage directorStorage;
    private final FilmDirectorStorage filmDirectorStorage;

    public Collection<User> getUserAll() {
        return userStorage.getAll();
    }

    public User getUser(Long id) {
        checkUserId(id);
        User user = userStorage.get(id);
        checkUser(user, id);

        return user;
    }

    public User addUser(User user) {
        fillDefaultValues(user);
        return userStorage.add(user);
    }

    public User updateUser(User newUser) {
        checkUserId(newUser.getId());
        User userUpdate = userStorage.update(newUser);
        checkUser(userUpdate, newUser.getId());

        return userUpdate;
    }

    @Transactional
    public User deleteUser(Long id) {
        checkUserId(id);
        User user = userStorage.get(id);
        checkUser(user, id);

        friendStorage.deleteFriends(id);

        User deletedUser = userStorage.delete(id);
        if (deletedUser == null) {
            throw new NotFoundException(String.format("Не удалось удалить пользователя с id = %d", id));
        }

        return deletedUser;
    }

    public boolean addFriend(Long id, Long friendId) {
        checkUserId(id);
        checkUserId(friendId);
        User user = userStorage.get(id);
        User friendUser = userStorage.get(friendId);
        checkUser(user, id);
        checkUser(friendUser, friendId);

        Friend friend = new Friend();
        friend.setUserId(id);
        friend.setFriendId(friendId);
        if (friendStorage.add(friend) == null) {
            return false;
        }

        addFeed(id, Operation.ADD, friendId);
        return true;
    }

    public boolean deleteFriend(Long id, Long friendId) {
        checkUserId(id);
        checkUserId(friendId);
        User user = userStorage.get(id);
        User friendUser = userStorage.get(friendId);
        checkUser(user, id);
        checkUser(friendUser, friendId);

        if (friendStorage.delete(id, friendId) == null) {
            return false;
        }

        addFeed(id, Operation.REMOVE, friendId);
        return true;
    }

    public Collection<User> getFriends(Long id) {
        checkUserId(id);
        User user = userStorage.get(id);
        checkUser(user, id);

        return userStorage.getAllInRange(
                friendStorage.getAll(id).stream().map(Friend::getFriendId).collect(Collectors.toSet())
        );
    }

    public Collection<User> getCommonFriends(Long id, Long otherId) {
        checkUserId(id);
        checkUserId(otherId);

        User user = userStorage.get(id);
        User otherUser = userStorage.get(otherId);

        checkUser(user, id);
        checkUser(otherUser, otherId);

        Set<User> userFriends = new HashSet<>(userStorage.getAllInRange(
                friendStorage.getAll(id).stream().map(Friend::getFriendId).collect(Collectors.toSet())));
        Set<User> otherUserFriends = new HashSet<>(userStorage.getAllInRange(
                friendStorage.getAll(otherId).stream().map(Friend::getFriendId).collect(Collectors.toSet())));

        userFriends.retainAll(otherUserFriends);

        return userFriends;
    }

    public Collection<Feed> getFeeds(Long userId) {
        checkUserId(userId);
        User user = userStorage.get(userId);
        checkUser(user, userId);

        return feedStorage.getAllByUserId(userId);
    }

    public Collection<Film> getRecommendations(Long userId) {
        checkUserId(userId);
        User user = userStorage.get(userId);
        checkUser(user, userId);

        Set<Long> userFilms = likeStorage.getAllByUserId(userId).stream().map(Like::getFilmId).collect(Collectors.toSet());
        if (userFilms.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> similarUsers = likeStorage.getAllInRangeFilmId(userFilms).stream().map(Like::getUserId).collect(Collectors.toSet());

        Map<Long, Set<Long>> likes = likeStorage.getAllInRangeUserId(similarUsers).stream()
                .collect(Collectors.groupingBy(Like::getUserId,
                        Collectors.mapping(Like::getFilmId, Collectors.toSet())));

        Optional<Map.Entry<Long, Integer>> similar = likes.entrySet().stream()
                .filter(e -> !e.getKey().equals(userId))
                .map(e -> {
                    Set<Long> intersection = new HashSet<>(e.getValue());
                    intersection.retainAll(userFilms);
                    return Map.entry(e.getKey(), intersection.size()); })
                .filter(e -> e.getValue() > 0)
                .max(Comparator.comparingInt(Map.Entry::getValue));
        if (similar.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> similarFilms = likes.get(similar.get().getKey());

        Set<Long> filmIdRecommendations = similarFilms.stream()
                .filter(f -> !userFilms.contains(f)).collect(Collectors.toSet());
        if (filmIdRecommendations.isEmpty()) {
            return Collections.emptyList();
        }

        Collection<Film> films = filmStorage.getAllInRange(filmIdRecommendations);
        readGenres(films);
        readDirectors(films);

        return films;
    }

    protected void checkUserId(Long id) {
        if (id == null) {
            throw new ValidationException("Не задан id пользователя");
        }
    }

    protected void checkUser(User user, Long id) {
        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", id));
        }
    }

    protected void addFeed(Long userId, Operation operation, Long entityId) {
        Feed feed = new Feed();
        feed.setTimestamp(Timestamp.from(Instant.now()).getTime());
        feed.setUserId(userId);
        feed.setEventType(EventType.FRIEND);
        feed.setOperation(operation);
        feed.setEntityId(entityId);
        feedStorage.add(feed);
    }

    protected void fillDefaultValues(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    protected void readGenres(Collection<Film> films) {
        Map<Long, Set<Long>> filmGenreMap = filmGenreStorage.getAllInRange(
                        films.stream().map(Film::getId).collect(Collectors.toSet()))
                .stream().collect(
                        Collectors.groupingBy(FilmGenre::getFilmId,
                                Collectors.mapping(FilmGenre::getGenreId, Collectors.toSet())));

        Map<Long, Genre> genreMap = genreStorage.getAllInRange(
                        filmGenreMap.values().stream().flatMap(Set::stream).collect(Collectors.toSet()))
                .stream().collect(
                        Collectors.toMap(Genre::getId, Function.identity()));

        films.forEach(film -> {
            Set<Long> genreIdSet = filmGenreMap.get(film.getId());
            if (genreIdSet != null) {
                film.setGenres(
                        genreIdSet.stream().map(genreMap::get).filter(Objects::nonNull)
                                .collect(Collectors.toSet())
                );
            }
        });
    }

    protected void readDirectors(Collection<Film> films) {
        Map<Long, Set<Long>> filmDirectorMap = filmDirectorStorage.getAllInRange(
                        films.stream().map(Film::getId).collect(Collectors.toSet()))
                .stream().collect(
                        Collectors.groupingBy(FilmDirector::getFilmId,
                                Collectors.mapping(FilmDirector::getDirectorId, Collectors.toSet())));

        Map<Long, Director> directorMap = directorStorage.getAllInRange(
                        filmDirectorMap.values().stream().flatMap(Set::stream).collect(Collectors.toSet()))
                .stream().collect(
                        Collectors.toMap(Director::getId, Function.identity()));

        films.forEach(film -> {
            Set<Long> directorIdSet = filmDirectorMap.get(film.getId());
            if (directorIdSet != null) {
                film.setDirectors(
                        directorIdSet.stream().map(directorMap::get).filter(Objects::nonNull)
                                .collect(Collectors.toSet())
                );
            }
        });
    }
}