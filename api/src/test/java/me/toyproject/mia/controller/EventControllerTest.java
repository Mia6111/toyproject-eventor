package me.toyproject.mia.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class EventControllerTest {
    public static final String EVENT_RESOURCE = "/v1/events";
    @Autowired
    private MockMvc mockMvc;
    @Test
    public void findAllRegisterOpenEvents() {
        mockMvc.perform(get(EVENT_RESOURCE +"/{id}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value())

    }

    @Test
    public void createEvent() {
    }

    @Test
    public void findById() {
    }

    @Test
    public void modifyEvent() {
    }

    @Test
    public void deleteEvent() {
    }
}