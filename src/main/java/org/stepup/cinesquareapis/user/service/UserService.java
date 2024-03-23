package org.stepup.cinesquareapis.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stepup.cinesquareapis.user.entity.User;
import org.stepup.cinesquareapis.user.model.CreateUserRequest;
import org.stepup.cinesquareapis.user.model.LoginUserRequest;
import org.stepup.cinesquareapis.user.model.UpdateUserRequest;
import org.stepup.cinesquareapis.user.model.UserResponse;
import org.stepup.cinesquareapis.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

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
        User user = userRepository.findByAccount(request.getAccount());

        if (user != null && user.getPassword().equals(request.getPassword())) {
            return true;
        }

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
