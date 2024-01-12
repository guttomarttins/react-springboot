package br.com.gt.imagelitleapi.application.user;

import br.com.gt.imagelitleapi.domain.AccessToken;
import br.com.gt.imagelitleapi.domain.entity.User;
import br.com.gt.imagelitleapi.domain.exception.DuplicatadTupleException;
import br.com.gt.imagelitleapi.domain.service.UserService;
import br.com.gt.imagelitleapi.infra.jwt.JwtService;
import br.com.gt.imagelitleapi.infra.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public User getByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Transactional
    @Override
    public User save(User user) {
        var possibleUser = getByEmail(user.getEmail());
        if(Objects.nonNull(possibleUser)) throw new DuplicatadTupleException("User already exists!");
        encodePassword(user);
        return repository.save(user);
    }

    @Override
    public AccessToken authentication(String email, String password) {
        var user = getByEmail(email);
        if(Objects.isNull(user))return null;
        boolean matches = passwordEncoder.matches(password, user.getPassword());

        if(matches){
            return jwtService.generateToken(user);
        }
        return null;
    }

    private void encodePassword(User user){
        String rawPassword = user.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        user.setPassword(encodedPassword);
    }
}
