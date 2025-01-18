package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.Headers;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private ItemDto itemDto;
    private ItemDtoBooking itemDtoBooking;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto(null, "Item Name", "Item Description", true, null, null, null);
        itemDtoBooking = new ItemDtoBooking(null, "Item Name", "Item Description", true,
                null, null, null, null, null);
        commentDto = new CommentDto(null, "Comment text", null, "Author name", null);
    }

    @Test
    void createItem() throws Exception {
        when(itemService.createItem(any())).thenReturn(itemDto);
        mockMvc.perform(post("/items")
                        .header(Headers.HEADER_USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDto)));
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.updateItem(any())).thenReturn(itemDto);
        mockMvc.perform(patch("/items/1")
                        .header(Headers.HEADER_USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDto)));
    }

    @Test
    void getAllItemByOwner() throws Exception {
        List<ItemDtoBooking> items = List.of(itemDtoBooking);
        when(itemService.getAllItemByOwnerId(1L)).thenReturn(items);
        mockMvc.perform(get("/items")
                        .header(Headers.HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(items)));
    }

    @Test
    void getItemById() throws Exception {
        when(itemService.getItemById(1L)).thenReturn(itemDtoBooking);
        mockMvc.perform(get("/items/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDtoBooking)));
    }

    @Test
    void searchItemsByText() throws Exception {
        List<ItemDto> items = List.of(itemDto);
        when(itemService.searchItemsByText("text")).thenReturn(items);
        mockMvc.perform(get("/items/search?text=text")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(items)));
    }

    @Test
    void createComment() throws Exception {
        when(itemService.createComment(any(), anyLong(), anyLong())).thenReturn(commentDto);
        mockMvc.perform(post("/items/1/comment")
                        .header(Headers.HEADER_USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(commentDto)));
    }
}