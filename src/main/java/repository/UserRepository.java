package repository;

import domain.User;

public interface UserRepository {
    void save(User user);
    User findByUsername(String username);
}