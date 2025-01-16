package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;


public interface RequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query("SELECT i FROM ItemRequest i WHERE i.requestor.id = :userId " +
            "ORDER BY i.created DESC")
    List<ItemRequest> findByRequestor(@Param("userId") Long userId);
}
