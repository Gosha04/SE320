package com.SE320.therapy.cli.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.SE320.therapy.controller.UserController;
import com.SE320.therapy.dto.UserResponse;
import com.SE320.therapy.objects.UserType;
import com.SE320.therapy.service.AuthResponse;

class UserCommandsRefreshSessionTest extends MenuTestSupport {

    @Test
    void refreshSessionIfPossible_returnsTrueWhenNoCurrentUser() throws Exception {
        UserController userController = org.mockito.Mockito.mock(UserController.class);
        UserCommands userCommands = new UserCommands(userController, scannerFrom(""));

        boolean refreshed = invokeRefreshSessionIfPossible(userCommands);

        assertTrue(refreshed);
        verifyNoInteractions(userController);
    }

    @Test
    void refreshSessionIfPossible_blankRefreshTokenClearsSessionAndReturnsFalse() throws Exception {
        UserController userController = org.mockito.Mockito.mock(UserController.class);
        UserCommands userCommands = new UserCommands(userController, scannerFrom(""));
        UserResponse user = user("11111111-1111-1111-1111-111111111111");

        setField(userCommands, "currentUser", user);
        setField(userCommands, "accessToken", "access-token");
        setField(userCommands, "refreshToken", "   ");

        boolean refreshed = invokeRefreshSessionIfPossible(userCommands);

        assertFalse(refreshed);
        assertNull(getField(userCommands, "currentUser"));
        assertNull(getField(userCommands, "accessToken"));
        assertNull(getField(userCommands, "refreshToken"));
        assertTrue(getOutput().contains("Your session can no longer be refreshed. Please log in again."));
        verifyNoInteractions(userController);
    }

    @Test
    void refreshSessionIfPossible_successfullyRefreshesSession() throws Exception {
        UserController userController = org.mockito.Mockito.mock(UserController.class);
        UserCommands userCommands = new UserCommands(userController, scannerFrom(""));

        UserResponse currentUser = user("11111111-1111-1111-1111-111111111111");
        UserResponse refreshedUser = user("22222222-2222-2222-2222-222222222222");
        setField(userCommands, "currentUser", currentUser);
        setField(userCommands, "accessToken", "old-access");
        setField(userCommands, "refreshToken", "old-refresh");

        when(userController.refresh("old-refresh"))
                .thenReturn(new AuthResponse("new-access", "new-refresh", refreshedUser));

        boolean refreshed = invokeRefreshSessionIfPossible(userCommands);

        assertTrue(refreshed);
        verify(userController).refresh("old-refresh");
        assertEquals(refreshedUser, getField(userCommands, "currentUser"));
        assertEquals("new-access", getField(userCommands, "accessToken"));
        assertEquals("new-refresh", getField(userCommands, "refreshToken"));
    }

    @Test
    void refreshSessionIfPossible_responseStatusExceptionClearsSessionAndReturnsFalse() throws Exception {
        UserController userController = org.mockito.Mockito.mock(UserController.class);
        UserCommands userCommands = new UserCommands(userController, scannerFrom(""));
        UserResponse currentUser = user("11111111-1111-1111-1111-111111111111");
        setField(userCommands, "currentUser", currentUser);
        setField(userCommands, "accessToken", "access-token");
        setField(userCommands, "refreshToken", "refresh-token");

        when(userController.refresh("refresh-token"))
                .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "expired"));

        boolean refreshed = invokeRefreshSessionIfPossible(userCommands);

        assertFalse(refreshed);
        verify(userController).refresh("refresh-token");
        assertNull(getField(userCommands, "currentUser"));
        assertNull(getField(userCommands, "accessToken"));
        assertNull(getField(userCommands, "refreshToken"));
        assertTrue(getOutput().contains("Your session expired. Please log in again."));
        assertTrue(getOutput().contains("expired"));
    }

    @Test
    void refreshSessionIfPossible_unexpectedExceptionDoesNotClearSessionAndReturnsFalse() throws Exception {
        UserController userController = org.mockito.Mockito.mock(UserController.class);
        UserCommands userCommands = new UserCommands(userController, scannerFrom(""));
        UserResponse currentUser = user("11111111-1111-1111-1111-111111111111");
        setField(userCommands, "currentUser", currentUser);
        setField(userCommands, "accessToken", "access-token");
        setField(userCommands, "refreshToken", "refresh-token");

        when(userController.refresh("refresh-token"))
                .thenThrow(new RuntimeException("temporary outage"));

        boolean refreshed = invokeRefreshSessionIfPossible(userCommands);

        assertFalse(refreshed);
        verify(userController).refresh("refresh-token");
        assertSame(currentUser, getField(userCommands, "currentUser"));
        assertEquals("access-token", getField(userCommands, "accessToken"));
        assertEquals("refresh-token", getField(userCommands, "refreshToken"));
        assertTrue(getOutput().contains("Unable to refresh your session right now."));
    }

    private boolean invokeRefreshSessionIfPossible(UserCommands userCommands) throws Exception {
        Method method = UserCommands.class.getDeclaredMethod("refreshSessionIfPossible");
        method.setAccessible(true);
        return (boolean) method.invoke(userCommands);
    }

    private Object getField(UserCommands userCommands, String fieldName) throws Exception {
        Field field = UserCommands.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(userCommands);
    }

    private void setField(UserCommands userCommands, String fieldName, Object value) throws Exception {
        Field field = UserCommands.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(userCommands, value);
    }

    private UserResponse user(String id) {
        return new UserResponse(
                UUID.fromString(id),
                UserType.PATIENT,
                "Jane",
                "Doe",
                "jane@example.com",
                "5551234",
                true
        );
    }
}
