package hexlet.code.service;

import hexlet.code.config.EncodersConfig;
import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    private UserMapper userMapper;

    public List<UserDTO> findAll() {
        var users = userRepository.findAll();
        var result = users.stream().map(userMapper::map)
                .toList();

        return result;
    }

    public UserDTO findById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        var userDTO = userMapper.map(user);

        return userDTO;
    }

    public UserDTO create(UserCreateDTO userData) {
        var user = userMapper.map(userData);
        userRepository.save(user);
        var userDTO = userMapper.map(user);

        return userDTO;
    }

    public UserDTO update(UserUpdateDTO userData, Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        userMapper.update(userData, user);
        var hashPwd = passwordEncoder.encode(user.getPassword());
        user.setPasswordDigest(hashPwd);
        userRepository.save(user);
        var userDTO = userMapper.map(user);

        return userDTO;
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
