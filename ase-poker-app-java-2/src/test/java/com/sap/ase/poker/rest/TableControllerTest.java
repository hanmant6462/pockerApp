package com.sap.ase.poker.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.ase.poker.data.PlayerNamesRepository;
import com.sap.ase.poker.dto.GetTableResponseDto;
import com.sap.ase.poker.model.GameState;
import com.sap.ase.poker.model.Player;
import com.sap.ase.poker.service.TableService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureMockMvc(addFilters = false)
public class TableControllerTest {

    private static final String PATH = "/api/v1/";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    TableService tableService;

    @MockBean
    PlayerNamesRepository playerNamesRepository;

    @Test
    void getTable_returnsGetTableResponseDtoWithTableStatus() throws Exception {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn("alice");

        Mockito.when(tableService.getPlayers()).thenReturn(Arrays.asList(
                new Player("alice", "Alice", 100),
                new Player("bob", "Bob", 100)));
        Mockito.when(tableService.getState()).thenReturn(GameState.FLOP);

        MockHttpServletResponse response = mockMvc.perform(get(PATH).principal(mockPrincipal))
                .andExpect(status().isOk()).andReturn().getResponse();

        GetTableResponseDto result = objectMapper.readValue(response.getContentAsString(), GetTableResponseDto.class);

        assertThat(result.getPlayers()).hasSize(2);
        assertThat(result.getState()).isEqualTo(GameState.FLOP.getValue());
    }
}
