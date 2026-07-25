package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Feed;

import java.util.Collection;

public interface FeedStorage {
    public Collection<Feed> getAll();

    public Collection<Feed> getAllByUserId(Long userId);

    public Feed get(Long id);

    public Feed add(Feed feed);

    public Feed delete(Long id);

    public boolean deleteAll();
}
