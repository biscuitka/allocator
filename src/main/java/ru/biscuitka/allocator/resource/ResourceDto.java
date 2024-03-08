package ru.biscuitka.allocator.resource;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResourceDto {
    Long id;
    @NotBlank(message = "Поле name обязательно для заполнения")
    @Size(min = 1, max = 100)
    String name;
}
