package com.ouharri.cirestechnologies.model.dto.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ouharri.cirestechnologies.model.enums.Gender;
import com.ouharri.cirestechnologies.model.enums.Role;
import com.ouharri.cirestechnologies.model.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserGeneratedResponses {

    /**
     * The first name of the user.
     * Must be present and not empty.
     */
    @NotNull(message = "FirstName must be present")
    @Size(message = "Firstname cannot be empty", min = 1)
    String firstname;

    /**
     * The last name of the user.
     * Should not exceed 30 characters.
     */
    @Size(message = "Lastname is too long", max = 30)
    String lastname;

    /**
     * The user's birthDate.
     */
    private Date birthDate;

    /**
     * The user's city.
     */
    private String city;

    /**
     * The user's country.
     */
    private String country;

    /**
     * The URL to the user's image.
     */
    String avatar;

    /**
     * The user's company.
     */
    private String company;

    /**
     * The user's jobPosition.
     */
    private String jobPosition;

    /**
     * The phone number of the user.
     * Must match the format '0XXXXXXXXX' where X is a digit.
     */
    @Pattern(message = "Phone number must match the format '0XXXXXXXXX'", regexp = "0\\d{9}")
    String mobile;

    /**
     * The user's username.
     */
    private String username;

    /**
     * The user's password.
     */
    private String password;

    /**
     * The email address of the user.
     * Must be a valid email format and should not exceed 80 characters.
     */
    @Size(message = "Email is too long", max = 80)
    @Email(message = "Email was not provided")
    String email;

    /**
     * The gender of the user.
     */
    Gender gender;

    /**
     * The status of the user.
     */
    UserStatus status;

    /**
     * The role of the user.
     */
    Role role;
}
