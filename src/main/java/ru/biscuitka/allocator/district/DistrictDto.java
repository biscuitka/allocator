package ru.biscuitka.allocator.district;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DistrictDto {
    Long id;
    String name;
}
