package com.ouharri.cirestechnologies.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Enum representing different roles in the system, each with a specific set of permissions.
 * Roles are used to grant authority and access control within the application.
 *
 * @author <a href="mailto:ouharrioutman@gmail.com">Ouharri Outman</a>
 */
@Getter
@RequiredArgsConstructor
public enum Role {

    /**
     * Represents a basic user with no special permissions.
     */
    USER(Collections.emptySet()),

    /**
     * Represents a role with a broad range of permissions.
     */
    ROLE(
            Set.of(
                    Permission.ROLE_READ,
                    Permission.ROLE_UPDATE,
                    Permission.ROLE_DELETE,
                    Permission.ROLE_CREATE
            )
    ),
    /**
     * Represents an administrator with the highest level of permissions,
     * including all administrator permissions and additional administrator privileges.
     */
    ADMIN(
            Set.of(
                    Permission.ADMIN_READ,
                    Permission.ADMIN_UPDATE,
                    Permission.ADMIN_DELETE,
                    Permission.ADMIN_CREATE
            )
    );


    private final Set<Permission> permissions;

    /**
     * Converts the role's permissions into a list of SimpleGrantedAuthority objects.
     * This list includes the role itself and all its associated permissions.
     *
     * @return List of SimpleGrantedAuthority objects representing the role and its permissions.
     */
    public List<SimpleGrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        authorities.addAll(getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .toList()
        );
        return authorities;
    }

}