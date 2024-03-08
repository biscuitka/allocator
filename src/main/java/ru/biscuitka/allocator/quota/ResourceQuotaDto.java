package ru.biscuitka.allocator.quota;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.biscuitka.allocator.district.DistrictDto;
import ru.biscuitka.allocator.resource.ResourceDto;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResourceQuotaDto {
    Long id;
    Long quantity;
    LocalDateTime startDate;
    LocalDateTime endDate;
    ResourceDto resource;
    DistrictDto district;
}
