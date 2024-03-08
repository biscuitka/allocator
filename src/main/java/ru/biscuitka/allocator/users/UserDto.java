package ru.biscuitka.allocator.users;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    Long id;
    String name;
    Long ticketSeries;
    Long ticketNumber;
    LocalDate ticketDate;
}
