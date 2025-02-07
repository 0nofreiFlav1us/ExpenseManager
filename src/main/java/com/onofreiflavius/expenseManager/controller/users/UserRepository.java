package com.onofreiflavius.expenseManager.controller.users;

import com.onofreiflavius.expenseManager.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserModel, String> { }
