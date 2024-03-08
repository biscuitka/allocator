package ru.biscuitka.allocator.district;

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


@WebMvcTest(DistrictController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class DistrictControllerTest {
    @MockBean
    DistrictService districtService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void getAll() throws Exception {
        List<DistrictDto> districtDtos = TestData.createAllDistricts();

        when(districtService.getAllDistricts(any(Pageable.class)))
                .thenReturn(districtDtos);

        mockMvc.perform(get("/districts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(districtDtos.get(0).getId()))
                .andExpect(jsonPath("$[0].name").value(districtDtos.get(0).getName()))
                .andExpect(jsonPath("$[1].id").value(districtDtos.get(1).getId()))
                .andExpect(jsonPath("$[1].name").value(districtDtos.get(1).getName()))
                .andExpect(jsonPath("$[2].id").value(districtDtos.get(2).getId()))
                .andExpect(jsonPath("$[2].name").value(districtDtos.get(2).getName()))
                .andExpect(jsonPath("$[3].id").value(districtDtos.get(3).getId()))
                .andExpect(jsonPath("$[3].name").value(districtDtos.get(3).getName()))
                .andExpect(jsonPath("$[4].id").value(districtDtos.get(4).getId()))
                .andExpect(jsonPath("$[4].name").value(districtDtos.get(4).getName()));

        verify(districtService, times(1)).getAllDistricts(any(Pageable.class));
        verifyNoMoreInteractions(districtService);
    }

    @Test
    void getById() throws Exception {
        DistrictDto districtDto = TestData.districtDto1();

        when(districtService.getDistrictById(eq(districtDto.getId()))).thenReturn(districtDto);

        mockMvc.perform(get("/districts/" + districtDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(districtDto.getId()))
                .andExpect(jsonPath("$.name").value(districtDto.getName()));

        verify(districtService, times(1)).getDistrictById(districtDto.getId());
        verifyNoMoreInteractions(districtService);
    }

    @Test
    void create() throws Exception {
        DistrictDto districtDto = TestData.districtDto1();
        when(districtService.createDistrict(any(DistrictDto.class)))
                .thenReturn(districtDto);

        mockMvc.perform(post("/districts")
                        .content(objectMapper.writeValueAsString(districtDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(districtDto.getId()))
                .andExpect(jsonPath("$.name").value(districtDto.getName()));

        verify(districtService, times(1)).createDistrict(any(DistrictDto.class));
        verifyNoMoreInteractions(districtService);
    }

    @Test
    void update() throws Exception {
        DistrictDto districtDto = TestData.districtDto1();
        districtDto.setName("Морской");

        when(districtService.updateDistrict(any(DistrictDto.class)))
                .thenReturn(districtDto);

        mockMvc.perform(patch("/districts/" + districtDto.getId())
                        .content(objectMapper.writeValueAsString(districtDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(districtDto.getId()))
                .andExpect(jsonPath("$.name").value(districtDto.getName()));

        verify(districtService, times(1)).updateDistrict(any(DistrictDto.class));
        verifyNoMoreInteractions(districtService);
    }

    @Test
    void deleteById() throws Exception {
        mockMvc.perform(delete("/districts/" + TestData.districtId))
                .andExpect(status().isNoContent());

        verify(districtService, times(1)).deleteDistrictById(TestData.districtId);
        verifyNoMoreInteractions(districtService);
    }
}