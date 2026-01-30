package com.example;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Room {
    private final String id;
    private final String name;
    private final Set<Booking> bookings = new HashSet<>();

    public Room(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public boolean isAvailable(LocalDateTime startTime, LocalDateTime endTime) {
        return bookings.stream()
                .noneMatch(booking ->
                        booking.overlaps(startTime, endTime));
    }

    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    public void removeBooking(String bookingId) {
        bookings.removeIf(booking -> booking.getId().equals(bookingId));
    }

    public boolean hasBooking(String bookingId) {
        return bookings.stream()
                .anyMatch(booking -> booking.getId().equals(bookingId));
    }

    public Booking getBooking(String bookingId) {
        return bookings.stream()
                .filter(booking -> booking.getId().equals(bookingId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Bokning finns inte"));
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
