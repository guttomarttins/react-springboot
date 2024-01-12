package br.com.gt.imagelitleapi.domain.service;

import br.com.gt.imagelitleapi.domain.AccessToken;
import br.com.gt.imagelitleapi.domain.entity.User;

public interface UserService {

    User getByEmail(String email);
    User save(User user);
    AccessToken authentication(String email, String password);
}
