package br.com.gt.imagelitleapi.application.user;

import br.com.gt.imagelitleapi.domain.entity.User;
import br.com.gt.imagelitleapi.domain.exception.DuplicatadTupleException;
import br.com.gt.imagelitleapi.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity save(@RequestBody UserDTO dto){
        try {
            User user = userMapper.mapToUser(dto);
            userService.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }catch (DuplicatadTupleException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.toString()));
        }
    }

    @PostMapping("auth")
    public ResponseEntity authentication(@RequestBody UserDTO dto){
        var token = userService.authentication(dto.getEmail(), dto.getPassword());
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(token);
    }
}
