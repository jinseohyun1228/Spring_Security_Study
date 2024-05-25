package com.jsh.securitystudy.repository;

import com.jsh.securitystudy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

//인터페이스인데 실수로 class로 선언해버렸다ㅜㅜ
public interface UserRepository extends JpaRepository<User,Integer> {
}
