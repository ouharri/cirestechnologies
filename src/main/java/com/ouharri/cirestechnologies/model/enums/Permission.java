package com.ouharri.cirestechnologies.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum representing various permissions that can be granted to roles in the system.
 * Each permission corresponds to a specific action or set of actions that a user is allowed to perform.
 *
 * @author <a href="mailto:ouharrioutman@gmail.com">Ouharri Outman</a>
 */
@Getter
@RequiredArgsConstructor
public enum Permission {

    /**
     * Permission to read admin-related data.
     */
    ADMIN_READ("admin:read"),

    /**
     * Permission to update admin-related data.
     */
    ADMIN_UPDATE("admin:update"),

    /**
     * Permission to create admin-related data.
     */
    ADMIN_CREATE("admin:create"),

    /**
     * Permission to delete admin-related data.
     */
    ADMIN_DELETE("admin:delete"),

    /**
     * Permission to read management-related data.
     */
    ROLE_READ("management:read"),

    /**
     * Permission to update management-related data.
     */
    ROLE_UPDATE("management:update"),

    /**
     * Permission to create management-related data.
     */
    ROLE_CREATE("management:create"),

    /**
     * Permission to delete management-related data.
     */
    ROLE_DELETE("management:delete");

    private final String permission;
}