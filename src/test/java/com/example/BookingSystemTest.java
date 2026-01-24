package com.example;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Timer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Named.of;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class BookingSystemTest {

    private BookingSystem bookingSystem;
    private final RoomRepository roomRepository = mock(RoomRepository.class);
    private final TimeProvider timeProvider = mock(TimeProvider.class);
    private final NotificationService notificationService = mock(NotificationService.class);
    LocalDateTime start = LocalDateTime.of(2026, 1, 20, 10, 0);
    LocalDateTime end = start.plusHours(1);


    @BeforeEach
    public void setup(){
        BookingSystem bookingSystem = new BookingSystem(timeProvider, roomRepository, notificationService);
        bookingSystem.bookRoom("1", start, end);
         RoomRepository roomRepository = mock(RoomRepository.class);
         TimeProvider timeProvider = mock(TimeProvider.class);
         NotificationService notificationService = mock(NotificationService.class);


    }

    @Test
    @DisplayName("Ska boka ett rum när det är tillgängligt")
    void shouldSuccessfullyBookARoomWhenItIsAvailable() {

        BookingSystem system = new BookingSystem(timeProvider, roomRepository, notificationService);
        LocalDateTime start = LocalDateTime.of(2026, 1, 20, 10, 0);
        LocalDateTime end = start.plusHours(2);
        String roomId = "Rum 1";
        Room room = new Room(roomId, "Rum 1");
        when(timeProvider.getCurrentTime()).thenReturn(start.minusMinutes(1));
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(roomRepository.findAll()).thenReturn(List.of(room));


        boolean result = system.bookRoom(roomId, start, end);


        assertThat(result).isTrue();
        assertThat(system.getAvailableRooms(start, end)).isEqualTo(1);
    }


    @Test
    void should_throwException_when_bookingIsEmpty() {
        BookingSystem system = new BookingSystem(timeProvider, roomRepository, notificationService);
        assertThatThrownBy(() -> system.bookRoom(null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Bokning kräver giltiga start- och sluttider samt rum-id");
    }


    @Test
    @DisplayName("Ska kunna boka tillgängliga rum")
    void getAvailableRooms() {
        Room room = new Room("1", "Rum 1");

        List<Room> rooms = new ArrayList<>();

        boolean result = rooms.add(room);

        assertThat(result).isTrue();
        assertThat(room.isAvailable(start, end)).isTrue();


    }
    @Test
    @DisplayName("Ingen Dubbel Bokning")
    void should_not_allow_double_booking_for_overlapping_times() {
        Room roomA = new Room("RoomA", "Rum 1");

        LocalDateTime bookedSlot = LocalDateTime.of(2026, 1, 20, 10, 0);
        LocalDateTime bookedSlot2 = LocalDateTime.of(2026, 1, 20, 11, 0);


        roomA.addBooking(new Booking("1", "1", start, end));
        roomA.addBooking(new Booking("2", "1", start, end));

        assertThat(roomA.isAvailable(start, end)).isFalse();
    }
//
    @Test
    @DisplayName("Ska kunna avboka")
    void cancelBooking() throws NotificationException {
        LocalDateTime start = LocalDateTime.of(2026, 1, 20, 10, 0);
        LocalDateTime end = start.plusHours(1);

        Room room = new Room("1", "Rum 1");
        Booking booking = new Booking("B1", "1", start, end);
        String bookingId = "B1";

        room.addBooking(booking);

        roomRepository.findAll();

        cancelBooking();


        assertThat(room.hasBooking(booking.getId())).isFalse();
        roomRepository.save(room);
        notificationService.sendBookingConfirmation(booking);

    }


}

