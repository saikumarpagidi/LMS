package com.lms.cdac.forms;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserForms {

    @NotBlank(message = "Username is required")
    @Size(min = 3, message = "Minimum 3 characters required")
    private String name;

    @Email(message = "Invalid email address")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Minimum 6 characters required")
    private String password;

    @NotBlank(message = "Phone number is required")
    @Size(min = 8, max = 12, message = "Phone number must be between 8 and 12 digits")
    @Pattern(regexp = "^[+]?\\d{8,12}$", message = "Phone number must contain only numbers and optional +")
    private String phoneNumber;

    private String college;

    private String resourceCenter;
}
