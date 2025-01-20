package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.util.Headers;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(name = Headers.HEADER_USER_ID) Long userId,
                                                    @RequestBody @Valid ItemRequestRequestDto requestDto) {
        log.info("Creating new item request {}", requestDto);
        return itemRequestClient.createItemRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getMyItemRequests(@RequestHeader(name = Headers.HEADER_USER_ID) Long userId) {
        log.info("Retrieving my item requests {}", userId);
        return itemRequestClient.getMyItemRequest(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllOtherItemRequests(@RequestHeader(name = Headers.HEADER_USER_ID) Long userId) {
        log.info("Retrieving all item requests {}", userId);
        return itemRequestClient.getAllOtherItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@PathVariable Long requestId) {
        log.info("Retrieving item request {}", requestId);
        return itemRequestClient.getItemRequestById(requestId);
    }
}