package com.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Named.of;
import static org.mockito.Mockito.mock;


class BookingSystemTest {
    private final LocalDateTime start = LocalDateTime.now();
    private final LocalDateTime end = LocalDateTime.now();
    private final RoomRepository roomRepository = mock(RoomRepository.class);
    private final TimeProvider timeProvider = mock(TimeProvider.class);
    private final NotificationService notificationService = mock(NotificationService.class);

    @Test
    @DisplayName("Ska boka ett rum när det är tillgängligt")
    void shouldSuccessfullyBookARoomWhenItIsAvailable() {
        // Arrange
        BookingSystem system = new BookingSystem(timeProvider, roomRepository, notificationService);
        LocalDateTime start = LocalDateTime.of(2026, 1, 20, 10, 0);
        LocalDateTime end = start.plusHours(2);
        String roomId = "Rum 1";

        // 2 Act
        boolean result = system.bookRoom(roomId, start, end);

        // 3 Assert
        assertThat(result).isTrue();
        assertThat(system.getAvailableRooms(start, end)).isEqualTo(1);
    }


    @Test
    void should_throwException_when_bookingIsEmpty(){
        assertThatThrownBy(() -> of("", ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Booking cannot be null or empty");
    }


    @Test
    void endNotBeforeStart() {

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now();

        assertThat(end).isAfter(start);
    }

    @Test
    void getAvailableRooms() {

    }

    @Test
    void cancelBooking() {


    }



}