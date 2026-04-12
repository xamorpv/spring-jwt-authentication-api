package ru.ls.pjwt.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.test.web.servlet.MockMvc;
import ru.ls.pjwt.domain.auth.property.ControllerDevProperties;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestComponent
public class AccessTokenHelper {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ControllerDevProperties controllerDevProperties;

    public void assertUserTokenIsValid(String token) throws Exception {
        assertTokenGrantsUserAccess(token);
        assertTokenGrantsAccess(token);
        assertTokenNotGrantsAdminAccess(token);
    }

    public void assertTokenGrantsAccess(String token) throws Exception {
        mockMvc.perform(get("/api/v1/test/protected")
                        .header("Authorization", "Bearer "+token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(controllerDevProperties.protectedResponse()));
    }

    public void assertTokenGrantsUserAccess(String token) throws Exception {
        mockMvc.perform(get("/api/v1/test/user-only")
                        .header("Authorization", "Bearer "+token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(controllerDevProperties.userOnlyResponse()));
    }

    public void assertTokenNotGrantsAdminAccess(String token) throws Exception {
        mockMvc.perform(get("/api/v1/test/admin-only")
                .header("Authorization", "Bearer "+token))
                .andExpect(status().isForbidden());
    }
}
