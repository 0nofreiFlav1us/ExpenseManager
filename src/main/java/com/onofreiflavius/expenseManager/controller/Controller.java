package com.onofreiflavius.expenseManager.controller;

import com.onofreiflavius.expenseManager.controller.expenses.ExpenseServices;
import com.onofreiflavius.expenseManager.controller.users.UserServices;
import com.onofreiflavius.expenseManager.model.ExpenseModel;
import com.onofreiflavius.expenseManager.model.UserModel;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;

@org.springframework.stereotype.Controller
@RequestMapping("")
public class Controller {

    private final ExpenseServices expenseServices;
    private final UserServices userServices;

    public Controller(ExpenseServices expensesServices, UserServices userServices) {
        this.expenseServices = expensesServices;
        this.userServices = userServices;
    }

    //               ||
    // API Endpoints ||
    //               \/

    @GetMapping("/account/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie sessionCookie = new Cookie("session-id", null);
        sessionCookie.setMaxAge(0);
        sessionCookie.setPath("/");
        response.addCookie(sessionCookie);

        return "redirect:/account/login";
    }

    @GetMapping("/account/signup")
    public String signup(HttpServletRequest request) {
        return userServices.activeSession(request, "redirect:/expenses/manager", "signup");
    }

    @PostMapping("/account/signup/action")
    public String signupAction(@RequestParam String username, @RequestParam String password, Model model) {
        if (userServices.addUser(new UserModel(username, password))) {
            return "redirect:/account/login";
        }

        return "redirect:/account/signup?error=true";
    }

    @GetMapping("/account/login")
    public String login(HttpServletRequest request) {
        return userServices.activeSession(request, "redirect:/expenses/manager", "login");

    }

    @PostMapping("/account/login/action")
    public String loginAction(HttpServletResponse response, @RequestParam String username, @RequestParam String password) {
        UserModel user = new UserModel(username, password);
        if (userServices.userExists(user)) {
            userServices.setCookie(response, userServices.addSessionToken(username));
            return "redirect:/expenses/manager";
        } else {
            return "redirect:/account/login?error=true";
        }
    }

    @GetMapping("/expenses/manager")
    public String expenses(HttpServletRequest request, Model model) {

        // Verifying the 'session-id' cookie
        boolean sessionActive = false;
        String sessionId = userServices.getCookie(request);
        if (sessionId != null) {
            String username = userServices.getSession(sessionId);
            List<ExpenseModel> expenses;
            if (username != null) {
                expenseServices.setUsername(username);
                expenses = expenseServices.getExpenses(username);
                model.addAttribute("username", username);
                model.addAttribute("expenses", expenses); // This sends the expenses to the interface (Thymeleaf)
                sessionActive = true;
            }
        }

        if ( ! sessionActive) {
            return "redirect:/account/login";
        }

        // Getting the date
        SimpleDateFormat sdf = new SimpleDateFormat("d/M/yy");
        Date date = new Date();

        String currentDate = sdf.format(date);
        String[] dateSplit = currentDate.split("/");

        // This sends the current date to input forms
        model.addAttribute("day", dateSplit[0]);
        model.addAttribute("month", dateSplit[1]);
        model.addAttribute("year", dateSplit[2]);

        return "expenses";
    }


    @PostMapping("/expenses/update/{id}")
    public String update(@PathVariable Integer id, @RequestParam(defaultValue = "false") String wasteful, @RequestParam String product, @RequestParam Double price,
                         @RequestParam Double quantity, @RequestParam String store, @RequestParam String day, @RequestParam String month, @RequestParam String username) {

        Integer dayI = Integer.parseInt(day);
        Integer monthI = Integer.parseInt(month);
        String date = expenseServices.dateToString(dayI, monthI);

        int wastefulI = (Objects.equals(wasteful, "true")) ? 1 : 0;

        ExpenseModel updatedExpense = new ExpenseModel(product, price, quantity, store, date, dayI, monthI, wastefulI, username);
        expenseServices.update(id, updatedExpense);

        return "redirect:/expenses/manager";
    }


    @PostMapping("/expenses/receipt")
    public String getReceipt(@RequestParam MultipartFile receipt, @RequestParam String store, @RequestParam String day, @RequestParam String month, @RequestParam String username) {

        try {
            byte[] imageBytes = receipt.getBytes();
            String base64String = Base64.getEncoder().encodeToString(imageBytes); // This gets Base64 Encoding of the image (it is needed for the API call)

            expenseServices.readReceipt(base64String, store, day, month, username); // This makes a call to the Cloud Vision API
                                                                                    // (I'm using the OCR - Optical Character Recognition)
        }
        catch (IOException | URISyntaxException | InterruptedException e) {
            return "redirect:/expenses/manager?error=true";
        }

        return "redirect:/expenses/manager";
    }


    @PostMapping("/expenses/add")
    public String addExpense(@RequestParam String product, @RequestParam Double price, @RequestParam Double quantity,
                             @RequestParam String store, @RequestParam String day, @RequestParam String month, @RequestParam String username) {

        Integer dayI = Integer.parseInt(day);
        Integer monthI = Integer.parseInt(month);
        String date = expenseServices.dateToString(dayI, monthI);

        product = product.substring(0, 1).toUpperCase() + product.substring(1).toLowerCase();
        ExpenseModel expense = new ExpenseModel(product, price, quantity, store, date, dayI, monthI, username);

        List<ExpenseModel> newExpense = new ArrayList<>(); // I'm creating a List because the "Add()" function accepts
        newExpense.add(expense);                            // multiple expenses at once (Cloud Vision API)

        expenseServices.add(newExpense);

        return "redirect:/expenses/manager";
    }


    @GetMapping("/expenses/delete/{id}")
    public String delete(@PathVariable Integer id) {
        expenseServices.delete(id);

        return "redirect:/expenses/manager";
    }
}
