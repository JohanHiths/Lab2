package com.example;

import java.time.LocalDateTime;

public interface TimeProvider {
    LocalDateTime getCurrentTime();
}
