package ru.biscuitka.allocator.application.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.biscuitka.allocator.application.ApplicationType;
import ru.biscuitka.allocator.request.ResourceRequestDto;
import ru.biscuitka.allocator.users.UserDto;

import java.util.List;

/**
 * новая заявка
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewApplicationDto {
    ApplicationType type;
    UserDto applicant;
    List<ResourceRequestDto> resourceRequests;
}
