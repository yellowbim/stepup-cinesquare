package org.stepup.cinesquareapis.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.stepup.cinesquareapis.user.entity.User;
import org.stepup.cinesquareapis.user.service.UserService;

import java.text.ParseException;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("api/user")
@RestController
public class UserController {

    private final UserService UserService;

    /**
     * User 생성
     *
     * @return
     * @throws ParseException
     */
    @PostMapping("")
    public ResponseEntity<User> createUser(@RequestBody User user) throws ParseException {
        User savedUser = UserService.createUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.OK);
    }

    /**
     * User 수정
     *
     * @return
     * @throws ParseException
     */
    @PutMapping("")
    public ResponseEntity<User> updateUser(@RequestBody User user) throws ParseException {
        User updatedUser = UserService.updateUser(user);
        if (!ObjectUtils.isEmpty(updatedUser)) {
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(user, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * User List 조회
     *
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<User>> getUsers() {
        List<User> Users = UserService.getUsers();
        return new ResponseEntity<>(Users, HttpStatus.OK);
    }

    /**
     * user_id에 해당하는 User 조회
     *
     * @param userId
     * @return
     */
    @GetMapping("{user_id}")
    public ResponseEntity<User> getUser(@PathVariable("user_id") int userId) {
        User User = UserService.getUser(userId);
        return new ResponseEntity<>(User, HttpStatus.OK);
    }

    /**
     * user_id에 해당하는 User 삭제
     *
     * @param userId
     * @return
     */
    @DeleteMapping("{user_id}")
    public ResponseEntity<Long> deleteUser(@PathVariable("user_id") int userId) {
        UserService.deleteUser(userId);
        return new ResponseEntity<>((long)userId, HttpStatus.OK);
    }
}