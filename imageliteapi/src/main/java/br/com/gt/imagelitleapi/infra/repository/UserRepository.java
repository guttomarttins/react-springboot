package br.com.gt.imagelitleapi.infra.repository;

import br.com.gt.imagelitleapi.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    User findByEmail(String email);
}
