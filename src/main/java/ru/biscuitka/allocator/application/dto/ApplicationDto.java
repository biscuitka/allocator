package ru.biscuitka.allocator.application.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.biscuitka.allocator.application.ApplicationStatus;
import ru.biscuitka.allocator.application.ApplicationType;
import ru.biscuitka.allocator.request.ResourceRequestDto;
import ru.biscuitka.allocator.users.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApplicationDto {
    Long id;
    ApplicationType type;
    ApplicationStatus status;
    LocalDateTime created;
    UserDto applicant;
    List<ResourceRequestDto> resourceRequests;
}
