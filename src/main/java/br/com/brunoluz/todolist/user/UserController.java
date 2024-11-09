package br.com.brunoluz.todolist.user;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping(path = "/users")
public class UserController {

  @Autowired
  private IUserRepository userRepository;

  @PostMapping(path = "/create")
  public ResponseEntity<Object> create(@RequestBody UserModel userModel) {
    var user = this.userRepository.findByUsername(userModel.getUsername());

    if (user != null) {
      ErrorMessageDTO response = new ErrorMessageDTO(400, "User already exists");
      return ResponseEntity.badRequest().body(response);
    }

    var passwordHashed = BCrypt.withDefaults()
        .hashToString(12, userModel.getPassword().toCharArray());

    userModel.setPassword(passwordHashed);

    var userCreated = this.userRepository.save(userModel);
    return ResponseEntity.ok().body(userCreated);
  }
}
