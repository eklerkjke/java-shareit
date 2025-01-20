package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Headers;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RequestService itemRequestService;

    private ItemRequestDto itemRequestDto;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Test name", null);
        itemRequestDto = new ItemRequestDto(user.getId(), "Request description", null, null, List.of());
    }

    @Test
    void createItemRequest() throws Exception {
        when(itemRequestService.createRequest(any(), anyLong())).thenReturn(itemRequestDto);
        mockMvc.perform(post("/requests")
                        .header(Headers.HEADER_USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestDto)));
    }

    @Test
    void getAllOtherItemRequests() throws Exception {
        List<ItemRequestDto> itemRequests = List.of(itemRequestDto);
        when(itemRequestService.getAllRequests()).thenReturn(itemRequests);
        mockMvc.perform(get("/requests/all")
                        .header(Headers.HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequests)));
    }

    @Test
    void getItemRequests() throws Exception {
        List<ItemRequestDto> listItemsRequests = List.of(itemRequestDto);
        when(itemRequestService.getRequests(anyLong())).thenReturn(listItemsRequests);
        mockMvc.perform(get("/requests")
                        .header(Headers.HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(listItemsRequests)));
    }

    @Test
    void getItemRequestsById() throws Exception {
        when(itemRequestService.getRequest(anyLong())).thenReturn(itemRequestDto);
        mockMvc.perform(get("/requests/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestDto)));
    }
}
