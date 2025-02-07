package com.onofreiflavius.expenseManager.controller.sessions;

import com.onofreiflavius.expenseManager.model.SessionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<SessionModel, String> {

    // This is a custom query created to help me find a session by username
    @Query(value = "SELECT * FROM sessions WHERE username = :username", nativeQuery = true)
    Optional<SessionModel> sessionExistenceByUsername(@Param("username") String username);

}
