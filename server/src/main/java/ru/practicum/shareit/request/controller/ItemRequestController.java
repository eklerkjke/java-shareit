package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.util.Headers;

import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final RequestService requestService;

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader(name = Headers.HEADER_USER_ID) Long userId,
                                        @RequestBody ItemRequestDto itemRequestDto) {
        return requestService.createRequest(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getRequests(@RequestHeader(name = Headers.HEADER_USER_ID) Long userId) {
        return requestService.getRequests(userId);
    }

    @GetMapping("/{id}")
    public ItemRequestDto getRequestById(@PathVariable Long id) {
        return requestService.getRequest(id);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests() {
        return requestService.getAllRequests();
    }
}
