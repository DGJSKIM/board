package com.study.board.repository;

import com.study.board.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {



    public List<User> findByUserid(String userid);



}
