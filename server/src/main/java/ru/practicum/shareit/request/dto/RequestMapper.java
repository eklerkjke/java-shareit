package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RequestMapper {
    public static ItemRequestDto toRequestDto(ItemRequest request) {
        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requestor(request.getRequestor())
                .created(request.getCreated())
                .build();
    }

    public static ItemRequest toRequest(ItemRequestDto requestDto, User user) {
        return ItemRequest.builder()
                .description(requestDto.getDescription())
                .requestor(user)
                .created(LocalDateTime.now())
                .build();
    }

    public static List<ItemRequestDto> mapToItemRequestDto(List<ItemRequest> requestList) {
        return requestList
                .stream()
                .map(RequestMapper::toRequestDto)
                .toList();
    }
}
