package com.governance.evaluation.dto.auth;

import com.governance.evaluation.entity.User;  // ✅ Import User
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private Long userId;
    private String email;
    private String name;
    private User.Role role;  // ✅ Use User.Role

    // Constructor that AuthService uses
    public LoginResponse(String token, Long userId, String email, String name, User.Role role) {
        this.token = token;
        this.type = "Bearer";
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.role = role;
    }
}