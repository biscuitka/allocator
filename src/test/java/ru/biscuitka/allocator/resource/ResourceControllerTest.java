package ru.biscuitka.allocator.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.biscuitka.allocator.utils.TestData;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResourceController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ResourceControllerTest {

    @MockBean
    RersourceService rersourceService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void getAll() throws Exception {
        List<ResourceDto> resourceDtos = TestData.createMassResources();

        when(rersourceService.getAllResources(any(Pageable.class)))
                .thenReturn(resourceDtos);

        mockMvc.perform(get("/resources"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(resourceDtos.get(0).getId()))
                .andExpect(jsonPath("$[0].name").value(resourceDtos.get(0).getName()))
                .andExpect(jsonPath("$[1].id").value(resourceDtos.get(1).getId()))
                .andExpect(jsonPath("$[1].name").value(resourceDtos.get(1).getName()))
                .andExpect(jsonPath("$[2].id").value(resourceDtos.get(2).getId()))
                .andExpect(jsonPath("$[2].name").value(resourceDtos.get(2).getName()));

        verify(rersourceService, times(1)).getAllResources(any(Pageable.class));
        verifyNoMoreInteractions(rersourceService);
    }

    @Test
    void getById() throws Exception {
        ResourceDto resourceDto = TestData.resourceDto1();

        when(rersourceService.getResourceById(eq(resourceDto.getId())))
                .thenReturn(resourceDto);

        mockMvc.perform(get("/resources/" + resourceDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(resourceDto.getId()))
                .andExpect(jsonPath("$.name").value(resourceDto.getName()));

        verify(rersourceService, times(1)).getResourceById(resourceDto.getId());
        verifyNoMoreInteractions(rersourceService);
    }

    @Test
    void create() throws Exception {
        ResourceDto resourceDto = TestData.resourceDto1();

        when(rersourceService.createResource(any(ResourceDto.class)))
                .thenReturn(resourceDto);

        mockMvc.perform(post("/resources")
                        .content(objectMapper.writeValueAsString(resourceDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(resourceDto.getId()))
                .andExpect(jsonPath("$.name").value(resourceDto.getName()));

        verify(rersourceService, times(1)).createResource(resourceDto);
        verifyNoMoreInteractions(rersourceService);
    }

    @Test
    void update() throws Exception {
        ResourceDto resourceDto = TestData.resourceDto1();
        resourceDto.setName("Бабочка");

        when(rersourceService.updateResource(resourceDto))
                .thenReturn(resourceDto);

        mockMvc.perform(patch("/resources/" + resourceDto.getId())
                        .content(objectMapper.writeValueAsString(resourceDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(resourceDto.getId()))
                .andExpect(jsonPath("$.name").value(resourceDto.getName()));

        verify(rersourceService, times(1)).updateResource(resourceDto);
        verifyNoMoreInteractions(rersourceService);
    }

    @Test
    void deleteById() throws Exception {
        mockMvc.perform(delete("/resources/" + TestData.resourceId))
                .andExpect(status().isNoContent());

        verify(rersourceService, times(1)).deleteResourceById(TestData.resourceId);
        verifyNoMoreInteractions(rersourceService);
    }
}