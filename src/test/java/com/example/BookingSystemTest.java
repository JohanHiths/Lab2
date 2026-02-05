package com.example;

import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Timer;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Named.of;
import static org.mockito.Mockito.*;
//

@ExtendWith(MockitoExtension.class)
class BookingSystemTest {

    private BookingSystem bookingSystem;
    private final RoomRepository roomRepository = mock(RoomRepository.class);
    private final TimeProvider timeProvider = mock(TimeProvider.class);
    private final NotificationService notificationService = mock(NotificationService.class);
    LocalDateTime start = LocalDateTime.of(2026, 1, 20, 10, 0);
    LocalDateTime end = start.plusHours(1);


    @BeforeEach
    public void setup(){
        bookingSystem = new BookingSystem(timeProvider, roomRepository, notificationService);
        when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.now());

    }

    @Test
    @DisplayName("Ska boka ett rum när det är tillgängligt")
    void shouldSuccessfullyBookARoomWhenItIsAvailable() throws NotificationException {
        BookingSystem system = new BookingSystem(timeProvider, roomRepository, notificationService);

        LocalDateTime start = LocalDateTime.of(2026, 1, 20, 10, 0);
        LocalDateTime end = start.plusHours(2);
        String roomId = "Rum 1";
        Room room = new Room(roomId, "Rum 1");

        when(timeProvider.getCurrentTime()).thenReturn(start.minusMinutes(1));
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(roomRepository.findAll()).thenReturn(List.of(room));

        assertThat(system.getAvailableRooms(start, end)).hasSize(1);

        boolean result = system.bookRoom(roomId, start, end);

        verify(roomRepository).save(room);
        verify(notificationService).sendBookingConfirmation(any());

        assertThat(result).isTrue();
        assertThat(system.getAvailableRooms(start, end)).hasSize(0);
    }


    @Test
    @DisplayName("Ska inte kunna boka ett rum när det är otillgängligt")
    void shouldUnSuccessfullyBookARoomWhenItIsNotAvailable(){
        BookingSystem system = new BookingSystem(timeProvider, roomRepository, notificationService);
        LocalDateTime start = LocalDateTime.of(2026, 1, 20, 10, 0);
        LocalDateTime end = start.plusHours(2);
        String roomId = "Rum 1";
        Room room = new Room(roomId, "Rum 1");

        when(timeProvider.getCurrentTime()).thenReturn(start.minusMinutes(1));
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(roomRepository.findAll()).thenReturn(List.of(room));

        boolean firstBookingResult = system.bookRoom(roomId, start, end);
        assertThat(firstBookingResult).isTrue();
        verify(roomRepository).save(room);

        boolean secondBookingResult = system.bookRoom(roomId, start, end);
        assertThat(secondBookingResult).isFalse();

        assertThat(system.getAvailableRooms(start, end)).hasSize(0);

        verify(roomRepository, times(1)).save(room);

    }


    @Test
    @DisplayName("Ska göra en exception när booking är tom")
    void should_throwException_when_bookingIsEmpty() {
        BookingSystem system = new BookingSystem(timeProvider, roomRepository, notificationService);
        assertThatThrownBy(() -> system.bookRoom(null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Bokning kräver giltiga start- och sluttider samt rum-id");
    }


    @Test
    @DisplayName("Ska kunna boka tillgängliga rum")
    void getAvailableRooms() {
        BookingSystem system = new BookingSystem(timeProvider, roomRepository, notificationService);
        Room roomA = new Room("RoomA", "Rum 1");
        Room roomB = new Room("RoomB", "Rum 2");

        roomA.addBooking(new Booking("1", "1", start, end));

        when(roomRepository.findAll()).thenReturn(List.of(roomA, roomB));

        assertThat(system.getAvailableRooms(start, end).get(0).getId()).isEqualTo("RoomB");

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
    /**
     * Testar att en existerande bokning kan avbokas korrekt.
     *  Given: Ett rum har en registrerad bokning i framtiden.
     * When: Metoden cancelBooking anropas med giltigt boknings-id.
     * Then: - Metoden ska returnera true.
     * - Bokningen ska tas bort från rummet.
     * - Det uppdaterade rummet ska sparas i databasen.
     * - En avbokningsbekräftelse ska skickas ut.
     */

    @Test
    @DisplayName("Ska kunna avboka en bokning i framtiden")
    void cancelBooking() throws NotificationException {
        LocalDateTime start = LocalDateTime.of(2026, 1, 20, 10, 0);
        LocalDateTime end = start.plusHours(1);
        Room room = new Room("1", "Rum 1");
        Booking booking = new Booking("B1", "1", start, end);
        String bookingId = "B1";

        room.addBooking(booking);

        BookingSystem system = new BookingSystem(timeProvider, roomRepository, notificationService);
        when(timeProvider.getCurrentTime()).thenReturn(start.minusMinutes(1));
        when(roomRepository.findAll()).thenReturn(List.of(room));

        boolean result = system.cancelBooking(bookingId);

        assertThat(result).isTrue();
        assertThat(room.hasBooking(booking.getId())).isFalse();
        verify(roomRepository).save(room);
        verify(notificationService).sendCancellationConfirmation(any());

    }
    /**
     * Testar att systemet hanterar tidsintervallen korrekt✅.
     * En bokning ska inte kunna accepteras om sluttiden är före eller samma som starttiden❌.
     * * Given: Ett giltigt rums-id men ogiltiga tidsinställningar (slut före start).
     * When: Metoden bookRoom anropas.
     * Then: Ett IllegalArgumentException ska kastas för att förhindra felaktiga bokningar.
     */
    @ParameterizedTest(name = "Kör {index}: Start={0}, Slut={1} - ska kasta exception")
    @CsvSource({"2026-01-20T10:00, 2026-01-20T09:00, false"})
    @DisplayName("Slut ska inte vara före start")
    void should_notwork_when_end_is_before_start() {

        LocalDateTime start = LocalDateTime.parse("2026-01-20T10:00");
        LocalDateTime end = LocalDateTime.parse("2026-01-20T09:00");

        assertThatThrownBy(() -> bookingSystem.bookRoom("1", start, end))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Kan inte boka tid i dåtid");


        assertThat(!end.isBefore(start)).isFalse();
        verify(roomRepository, never()).save(any(Room.class));

    }
    @ParameterizedTest
    @CsvSource({"'', 2026-01-20T10:00, 2026-01-20T11:00",
            "'  ', 2026-01-20T10:00, 2026-01-20T11:00",
            ", 2026-01-20T10:00, 2026-01-20T11:00"})
    @DisplayName("Validera Ogiltliga rum-id")
    void should_throwException_when_roomId_is_invalid(String roomIdFromCsv, String startStr, String endStr) {

        LocalDateTime start = LocalDateTime.parse(startStr);
        LocalDateTime end = LocalDateTime.parse(endStr);

        when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.of(2025, 1, 1, 0, 0));

        when(roomRepository.findById(roomIdFromCsv)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingSystem.bookRoom(roomIdFromCsv, start, end))
                .isInstanceOf(IllegalArgumentException.class);

        verify(roomRepository, never()).save(any());

    }

    @Test
    @DisplayName("Ska kasta exception om sluttid är före starttid")
    void shouldThrowExceptionWhenEndTimeIsBeforeStartTime() {
        LocalDateTime start = LocalDateTime.now().plusHours(2);
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        assertThrows(IllegalArgumentException.class, () -> {
            bookingSystem.getAvailableRooms(start, end);
        });
    }

    @Test
    @DisplayName("Booking id kan inte vara null")
    void shouldThrowExceptionWhenBookingIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            bookingSystem.cancelBooking(null);
        });
    }
    @Test
    @DisplayName("Ska kunna göra en avbokning ")
    void should_successfully_cancel_booking() throws NotificationException {
        LocalDateTime bookingStart = LocalDateTime.of(2026, 1, 20, 10, 0);
        LocalDateTime bookingEnd = bookingStart.plusHours(1);
        LocalDateTime currentTime = bookingStart.plusMinutes(30);

        Room room = new Room("1", "Rum 1");
        Booking booking = new Booking("B1", "1", bookingStart, bookingEnd);
        room.addBooking(booking);

        when(roomRepository.findAll()).thenReturn(List.of(room));
        when(timeProvider.getCurrentTime()).thenReturn(currentTime);

        BookingSystem system = new BookingSystem(timeProvider, roomRepository, notificationService);


        assertThatThrownBy(() -> system.cancelBooking("B1"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Kan inte avboka påbörjad eller avslutad bokning");

    }






}

