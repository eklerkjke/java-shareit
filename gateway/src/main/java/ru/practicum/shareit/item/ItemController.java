package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.util.Headers;
import ru.practicum.shareit.validation.CreateValidationGroup;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(name = Headers.HEADER_USER_ID) Long ownerId,
                                             @RequestBody @Validated(CreateValidationGroup.class)
                                             ItemRequestDto requestDto) {
        log.info("Creating new item: {}", requestDto);
        return itemClient.createItem(requestDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(name = Headers.HEADER_USER_ID) Long ownerId,
                                             @RequestBody @Valid ItemRequestDto requestDto, @PathVariable Long itemId) {
        log.info("Updating existing item: {}", itemId);
        return itemClient.updateItem(requestDto, ownerId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByOwnerId(@RequestHeader(name = Headers.HEADER_USER_ID) Long ownerId) {
        log.info("Retrieving all items by ownerId: {}", ownerId);
        return itemClient.getAllItemsByOwnerId(ownerId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(name = Headers.HEADER_USER_ID) Long userId,
                                              @PathVariable Long itemId) {
        log.info("Retrieving item by id: {}", itemId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemsByText(@RequestHeader(name = Headers.HEADER_USER_ID) Long userId,
                                                    @RequestParam String text) {
        log.info("Retrieving all items by text: {}", text);
        return itemClient.searchItemsByText(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(name = Headers.HEADER_USER_ID) Long userId,
                                                @PathVariable Long itemId,
                                                @RequestBody @Valid CommentRequestDto requestDto) {
        log.info("Creating new comment for user: {}, item: {}", userId, itemId);
        return itemClient.createComment(userId, itemId, requestDto);
    }
}
