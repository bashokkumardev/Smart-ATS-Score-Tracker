package com.ashok.jobtracker.security;

import com.ashok.jobtracker.entity.Role;
import com.ashok.jobtracker.exception.ForbiddenException;
import com.ashok.jobtracker.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static UserPrincipal getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new UnauthorizedException("Not authenticated");
        }
        return principal;
    }

    public static String getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public static boolean isAdmin() {
        return getCurrentUser().getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + Role.ADMIN.name()));
    }

    public static void requireAdmin() {
        if (!isAdmin()) {
            throw new ForbiddenException("Admin access required");
        }
    }

    public static void requireSelfOrAdmin(String registerId) {
        if (!getCurrentUserId().equals(registerId) && !isAdmin()) {
            throw new ForbiddenException("You can only access your own registration");
        }
    }
}
