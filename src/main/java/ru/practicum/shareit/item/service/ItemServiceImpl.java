package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto createItem(ItemDto item) {
        userService.getUserById(item.getOwnerId());
        log.info("Создание нового предмета: {}", item);
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(item)));
    }

    @Override
    public List<ItemDtoBooking> getAllItemByOwnerId(Long ownerId) {
        log.info("Получение всех предметов по ID владельца: {}", ownerId);
        userService.getUserById(ownerId);

        return mapToItemDtoBooking(itemRepository.findByOwnerId(ownerId));
    }

    @Override
    public ItemDtoBooking getItemById(Long itemId) {
        log.info("Получение предмета по ID: {}", itemId);
        Item item = findById(itemId);
        return mapToItemDtoBooking(item);
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemDto item) {
        log.info("Обновление предмета по ID: {}", item.getId());
        userService.getUserById(item.getOwnerId());
        Item haveItem = findById(item.getId());
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItemOnUpdate(item, haveItem)));
    }

    @Override
    @Transactional
    public void deleteItemByItemId(Long itemId) {
        log.info("Удаление предмета по ID: {}", itemId);
        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ItemDto> searchItemsByText(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        log.info("Поиск предметов по тексту: {}", text);
        return itemRepository.findByText(text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    @Transactional
    public CommentDto createComment(CommentDto comment, Long userId, Long itemId) {
        if (bookingRepository.checkHaveBooking(itemId, userId) > 0) {
            log.info("Добавление комментария к предмету: {}", itemId);
            Item item = findById(itemId);
            User user = UserMapper.toUser(userService.getUserById(userId));
            comment.setCreated(LocalDateTime.now());
            return CommentMapper.toCommentDto(commentRepository.save(CommentMapper.toComment(comment, item, user)));
        }
        throw new BadRequestException("Чтобы оставить комментарий пользователь должен взять предмет и вернуть его.");
    }

    private Item findById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Не найден предмет с id: " + itemId));
    }

    private ItemDtoBooking mapToItemDtoBooking(Item item) {
        Booking lastBooking = bookingRepository.findLastBooking(item.getId());
        Booking nextBooking = bookingRepository.findNextBooking(item.getId());
        List<CommentDto> comments = CommentMapper.mapToCommentDto(commentRepository.findAllByItemId(item.getId()));
        return ItemMapper.toItemDtoBooking(item, lastBooking, nextBooking, comments);
    }

    private List<ItemDtoBooking> mapToItemDtoBooking(List<Item> items) {
        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .toList();
        Map<Long, Booking> lastBookings = bookingRepository.findLastBookings(itemIds).stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), booking -> booking));
        Map<Long,Booking> nextBookings = bookingRepository.findNextBookings(itemIds).stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), booking -> booking));
        Map<Long, List<CommentDto>> comments = commentRepository.findAllByItemIds(itemIds).stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId(),
                        Collectors.mapping(CommentMapper::toCommentDto, Collectors.toList())));

        return items.stream()
                .map(item -> ItemMapper.toItemDtoBooking(item, lastBookings.get(item.getId()),
                        nextBookings.get(item.getId()), comments.get(item.getId())))
                .toList();
    }
}