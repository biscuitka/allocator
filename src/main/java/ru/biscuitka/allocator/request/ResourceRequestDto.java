package ru.biscuitka.allocator.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResourceRequestDto {
    Long id;
    ResourceStatus status;
    Long quantity;
    Long resource;
    Long district;
    Long application;
}
