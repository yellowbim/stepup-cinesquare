package org.stepup.cinesquareapis.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.stepup.cinesquareapis.user.repository.UserRepository;
import org.stepup.cinesquareapis.user.entity.User;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository UserRepository;

    /**
     * User 생성
     *
     * @param User
     * @return
     */
    public User createUser(User User) {
        User savedUser = UserRepository.save(User);  // JpaRepository에서 제공하는 save() 함수
        return savedUser;
    }

    /**
     * User 수정
     * JPA Repository의 save Method를 사용하여 객체를 업데이트 할 수 있습니다.
     * Entity User에 @Id로 설정한 키 값이 존재할 경우 해당하는 데이터를 업데이트 해줍니다.
     * 만약 수정하려는 Entity User 객체에 @Id 값이 존재하지 않으면 Insert 되기 때문에
     * 아래와 같이 업데이트 하고자 하는 User가 존재하는지 체크하는 로직을 추가하였습니다.
     *
     * @param User
     * @return
     */
    public User updateUser(User User) {
        User updatedUser = null;
        try {
            User existUser = getUser(User.getUserId());
            if (!ObjectUtils.isEmpty(existUser)) {
                updatedUser = UserRepository.save(User);  // JpaRepository에서 제공하는 save() 함수
            }
        } catch (Exception e) {
            log.info("[Fail] e: " + e.toString());
        } finally {
            return updatedUser;
        }
    }

    /**
     * User List 조회
     *
     * @return
     */
    public List<User> getUsers() {
        return UserRepository.findAll();  // JpaRepository에서 제공하는 findAll() 함수
    }

    /**
     * userId에 해당하는 User 조회
     *
     * @param userId
     * @return
     */
    public User getUser(int userId) {
        return UserRepository.getById(userId);  // JpaRepository에서 제공하는 getById() 함수
    }

    /**
     * userId에 해당하는 User 삭제
     *
     * @param userId
     */
    public void deleteUser(int userId) {
        UserRepository.deleteById(userId);  // JpaRepository에서 제공하는 deleteById() 함수
    }
}
