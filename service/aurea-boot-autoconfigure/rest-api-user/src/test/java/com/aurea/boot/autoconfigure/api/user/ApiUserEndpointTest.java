package com.aurea.boot.autoconfigure.api.user;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aurea.boot.autoconfigure.api.user.ApiConsts.User.Mapping;
import com.aurea.boot.autoconfigure.api.user.json.TokenPasswordJson;
import com.aurea.boot.autoconfigure.data.user.User;
import com.aurea.boot.autoconfigure.data.user.UserRepository;
import java.security.Principal;
import java.util.Optional;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class ApiUserEndpointTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new ApiUserEndpoint(userRepository))
            .addFilter(new SecurityContextPersistenceFilter()).build();

    @Test
    @WithMockUser
    public void forgotPassword() throws Exception {
        this.mockMvc.perform(post(Mapping.API_USER + Mapping.FORGOT_PASSWORD)
                .content("{\"email\": \"dummy@email.com\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void resetPassword() throws Exception {
        TokenPasswordJson tokenPasswordJson = new TokenPasswordJson("token", "passowrd");
        this.mockMvc.perform(post(Mapping.API_USER + Mapping.RESET_PASSWORD)
                .content(new ObjectMapper().writeValueAsString(tokenPasswordJson))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void getCurrentUser() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("name");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(new User()));
        mockMvc.perform(get(Mapping.API_USER + Mapping.CURRENT_USER)
                .principal(principal))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void checkResetPasswordToken() throws Exception {
        this.mockMvc.perform(get(Mapping.API_USER + Mapping.CHECK_RESET_PASSWORD_TOKEN)
                .param("token", "tokenValue")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
