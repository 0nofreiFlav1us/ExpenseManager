package com.onofreiflavius.expenseManager.controller.expenses;

import com.onofreiflavius.expenseManager.model.ExpenseModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<ExpenseModel, Integer> {

    // This is a custom query created to help me divide the days
    @Query(value = "SELECT COUNT(*) > 0 FROM expenses WHERE date = :date AND id != :id", nativeQuery = true)
    Integer dateExistence(@Param("date") String date, @Param("id") Integer id);

    // This is a custom query that returns the last expense on a certain date, helping me with reassigning(*) the final of the day
    @Query(value = "SELECT * FROM expenses WHERE date = :date LIMIT 1", nativeQuery = true)
    Optional<ExpenseModel> lastExpense(String date);

    // This is a custom query that returns a user's expenses
    @Query(value = "SELECT * FROM expenses WHERE user = :username ORDER BY year DESC, month DESC, day DESC, id DESC", nativeQuery = true)
    List<ExpenseModel> getExpenses(@Param("username") String username);

}
