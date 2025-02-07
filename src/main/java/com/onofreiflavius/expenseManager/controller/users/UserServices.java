package com.onofreiflavius.expenseManager.controller.users;

import com.onofreiflavius.expenseManager.controller.sessions.SessionRepository;
import com.onofreiflavius.expenseManager.controller.sessions.SessionTokenGenerator;
import com.onofreiflavius.expenseManager.model.SessionModel;
import com.onofreiflavius.expenseManager.model.UserModel;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserServices {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    public UserServices(UserRepository userRepository, SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }


    //               ||
    //    Methods    ||
    //               \/

    // This verifies if a user exists
    public Boolean userExists(UserModel userModel) {
        if (userRepository.existsById(userModel.getUsername())) {
            Optional<UserModel> user = userRepository.findById(userModel.getUsername());
            if (user.isPresent()) {
                return PasswordServices.checkPassword(userModel.getPassword(), user.get().getPassword());
            }
        }

        return false;
    }

    public String getSession(String sessionToken) {
        Optional<SessionModel> existingSession = sessionRepository.findById(sessionToken);
        if (existingSession.isPresent()) {
            return existingSession.get().getUsername();
        } else {
            return null;
        }
    }

    // This deletes a session token
    public void deleteSessionToken(String username) {
        Optional<SessionModel> existingSession = sessionRepository.sessionExistenceByUsername(username);
        if (existingSession.isPresent()) {
            sessionRepository.deleteById(existingSession.get().getToken());
        }
    }

    // This adds a session token
    public String addSessionToken(String username) {
        String sessionToken = SessionTokenGenerator.generateToken();
        while (userRepository.existsById(sessionToken)) {
            sessionToken = SessionTokenGenerator.generateToken();
        }

        deleteSessionToken(username);
        sessionRepository.save(new SessionModel(sessionToken, username));
        return sessionToken;
    }

    // This sets a 'session-id' cookie
    public void setCookie(HttpServletResponse response, String sessionToken) {
        Cookie cookie = new Cookie("session-id", sessionToken);
        cookie.setPath("/");

        response.addCookie(cookie);
    }

    // This returns the value of a 'session-id' cookie
    public String getCookie(HttpServletRequest request) {
        Cookie[] cookie = request.getCookies();
        if (cookie != null) {
            for (Cookie c : cookie) {
                if (c.getName().equals("session-id")) {
                    return c.getValue();
                }
            }
        }

        return null;
    }

    // This verifies if a session is active
    public String activeSession(HttpServletRequest request, String pathIfFalse, String pathIfTrue) {
        String sessionId = getCookie(request);
        if (sessionId != null) {
            String username = getSession(sessionId);
            if (username != null) {
                return pathIfFalse;
            }
        }

        return pathIfTrue;
    }

    // This adds a new user
    public Boolean addUser(UserModel userModel) {
        if (userRepository.existsById(userModel.getUsername())) {
            return false;
        } else {
            userModel.setPassword(PasswordServices.hashPassword(userModel.getPassword()));
            userRepository.save(userModel);
            return true;
        }
    }

}
