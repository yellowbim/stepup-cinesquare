package org.stepup.cinesquareapis.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stepup.cinesquareapis.user.entity.User;
import org.stepup.cinesquareapis.user.model.*;
import org.stepup.cinesquareapis.user.repository.UserRepository;

import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfo(Integer id) {
        return userRepository.findById(id)
                .map(UserInfoResponse::from)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));
    }

    @Transactional
    public UserDeleteResponse deleteUser(Integer id) {
        if (!userRepository.existsById(id)) return new UserDeleteResponse(false);
        userRepository.deleteById(id);
        return new UserDeleteResponse(true);
    }

    @Transactional
    public UserUpdateResponse updateUser(Integer id, UserUpdateRequest request) {
        return userRepository.findById(id)
//                .filter(user -> user.getPassword().equals(request.password()))
                .filter(member -> encoder.matches(request.password(), member.getPassword()))	// 암호화된 비밀번호와 비교하도록 수정
                .map(user -> {
                    user.update(request, encoder);	// 새 비밀번호를 암호화하도록 수정
                    return UserUpdateResponse.of(true, user);
                })
                .orElseThrow(() -> new NoSuchElementException("아이디 또는 비밀번호가 일치하지 않습니다."));
    }

    /**
     * account 존재 확인
     *
     * @param account
     * @return result
     */
    public boolean checkAccount(String account) {
        Boolean result = userRepository.existsByAccount(account);

        return result;
    }

    /**
     * User 생성
     *
     * @param request
     * @return new UserResponse(savedUser)
     */
    public UserResponse createUser(CreateUserRequest request) {
        User savedUser = userRepository.save(request.toEntity());

        return new UserResponse(savedUser);
    }

    /**
     * User 확인 (계정&비밀번호 확인)
     *
     * @param request
     * @return new UserResponse(savedUser)
     */
    public boolean checkUser(LoginUserRequest request) {
        // account로 사용자 조회
//        User user = userRepository.findByAccount(request.getAccount());
//
//        if (user != null && user.getPassword().equals(request.getPassword())) {
//            return true;
//        }

        return false;
    }
    
    /**
     * User 수정
     *
     * @param userId, request
     * @return
     */
    @Transactional
    public UserResponse updateUser(int userId, UpdateUserRequest request) {
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // 요청에서 변경된 정보만 업데이트
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(request.getPassword());
        }
        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }
        if (request.getNickname() != null && !request.getNickname().isBlank()) {
            user.setNickname(request.getNickname());
        }
        // TODO: 모두 null 이면 오류 처리

        // 회원 정보 저장
        User updatedUser = userRepository.save(user);

        return new UserResponse(updatedUser);
    }

    /**
     * userId에 해당하는 User 조회
     *
     * @param userId
     * @return
     */
    public UserResponse getUser(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return new UserResponse(user);
    }
}
