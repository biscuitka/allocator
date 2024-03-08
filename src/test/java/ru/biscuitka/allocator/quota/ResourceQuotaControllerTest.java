package ru.biscuitka.allocator.quota;

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

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResourceQuotaController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ResourceQuotaControllerTest {

    @MockBean
    ResourceQuotaService quotaService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;


    @Test
    void getAll() throws Exception {
        List<ResourceQuotaDto> quotaDtos = TestData.createMassQuotas();

        when(quotaService.getAllQuotas(any(Pageable.class)))
                .thenReturn(quotaDtos);

        mockMvc.perform(get("/quotas?size=15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(quotaDtos.get(0).getId()))
                .andExpect(jsonPath("$[0].resource").value(quotaDtos.get(0).getResource()))
                .andExpect(jsonPath("$[0].district").value(quotaDtos.get(0).getDistrict()))
                .andExpect(jsonPath("$[0].quantity").value(quotaDtos.get(0).getQuantity()))
                .andExpect(jsonPath("$[0].startDate").value(quotaDtos.get(0).getStartDate()
                        .format(TestData.DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$[0].endDate").value(quotaDtos.get(0).getEndDate()
                        .format(TestData.DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$", hasSize(quotaDtos.size())));

        verify(quotaService, times(1)).getAllQuotas(any(Pageable.class));
        verifyNoMoreInteractions(quotaService);


    }

    @Test
    void getById() throws Exception {
        ResourceQuotaDto quotaDto = TestData.quotaDto1();

        when(quotaService.getQuotaById(eq(quotaDto.getId())))
                .thenReturn(quotaDto);

        mockMvc.perform(get("/quotas/" + quotaDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(quotaDto.getId()))
                .andExpect(jsonPath("$.resource").value(quotaDto.getResource()))
                .andExpect(jsonPath("$.district").value(quotaDto.getDistrict()))
                .andExpect(jsonPath("$.quantity").value(quotaDto.getQuantity()))
                .andExpect(jsonPath("$.startDate").value(quotaDto.getStartDate()
                        .format(TestData.DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.endDate").value(quotaDto.getEndDate()
                        .format(TestData.DATE_TIME_FORMATTER)));

        verify(quotaService, times(1)).getQuotaById(eq(quotaDto.getId()));
        verifyNoMoreInteractions(quotaService);
    }

    @Test
    void create() throws Exception {
        NewResourceQuotaDto newQuota = TestData.newQuotaDto1();
        ResourceQuotaDto quotaDto = TestData.quotaDto1();

        when(quotaService.createQuota(any(NewResourceQuotaDto.class)))
                .thenReturn(quotaDto);

        mockMvc.perform(post("/quotas")
                        .content(objectMapper.writeValueAsString(newQuota))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(quotaDto.getId()))
                .andExpect(jsonPath("$.resource").value(quotaDto.getResource()))
                .andExpect(jsonPath("$.district").value(quotaDto.getDistrict()))
                .andExpect(jsonPath("$.quantity").value(quotaDto.getQuantity()))
                .andExpect(jsonPath("$.startDate").value(quotaDto.getStartDate()
                        .format(TestData.DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.endDate").value(quotaDto.getEndDate()
                        .format(TestData.DATE_TIME_FORMATTER)));

        verify(quotaService, times(1)).createQuota(any(NewResourceQuotaDto.class));
        verifyNoMoreInteractions(quotaService);
    }

    @Test
    void update() throws Exception {
        ResourceQuotaDto quotaDto = TestData.quotaDto1();
        quotaDto.setQuantity(87L);

        when(quotaService.updateQuota(any(ResourceQuotaDto.class)))
                .thenReturn(quotaDto);

        mockMvc.perform(patch("/quotas/" + quotaDto.getId())
                        .content(objectMapper.writeValueAsString(quotaDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(quotaDto.getId()))
                .andExpect(jsonPath("$.resource").value(quotaDto.getResource()))
                .andExpect(jsonPath("$.district").value(quotaDto.getDistrict()))
                .andExpect(jsonPath("$.quantity").value(quotaDto.getQuantity()))
                .andExpect(jsonPath("$.startDate").value(quotaDto.getStartDate()
                        .format(TestData.DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.endDate").value(quotaDto.getEndDate()
                        .format(TestData.DATE_TIME_FORMATTER)));

        verify(quotaService, times(1)).updateQuota(any(ResourceQuotaDto.class));
        verifyNoMoreInteractions(quotaService);
    }

    @Test
    void deleteById() throws Exception {
        mockMvc.perform(delete("/quotas/" + TestData.quotaId))
                .andExpect(status().isNoContent());

        verify(quotaService, times(1)).deleteQuotaById(any(Long.class));
        verifyNoMoreInteractions(quotaService);
    }
}