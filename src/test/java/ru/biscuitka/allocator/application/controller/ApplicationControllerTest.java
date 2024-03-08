package ru.biscuitka.allocator.application.controller;

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
import ru.biscuitka.allocator.application.dto.ApplicationDto;
import ru.biscuitka.allocator.application.dto.NewApplicationDto;
import ru.biscuitka.allocator.application.dto.UpdateApplicationDto;
import ru.biscuitka.allocator.application.service.ApplicationService;
import ru.biscuitka.allocator.request.ResourceRequestDto;
import ru.biscuitka.allocator.request.ResourceStatus;
import ru.biscuitka.allocator.users.UserDto;
import ru.biscuitka.allocator.utils.TestData;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApplicationController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ApplicationControllerTest {

    @MockBean
    ApplicationService applicationService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void getAll() throws Exception {
        List<ApplicationDto> applicationDtos = TestData.createApplicationDtoList();

        when(applicationService.getAllApplications(any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(applicationDtos);

        mockMvc.perform(get("/applications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(applicationDtos.size())))
                .andExpect(jsonPath("$[0].id").value(applicationDtos.get(0).getId()))
                .andExpect(jsonPath("$[0].type").value(applicationDtos.get(0).getType().toString()))
                .andExpect(jsonPath("$[0].status").value(applicationDtos.get(0).getStatus().toString()))
                .andExpect(jsonPath("$[0].created").value(applicationDtos.get(0).getCreated()
                        .format(TestData.DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$[0].applicant.id").value(applicationDtos.get(0).getApplicant().getId()))
                .andExpect(jsonPath("$[0].resourceRequests", hasSize(applicationDtos.get(0).getResourceRequests().size())));

        verify(applicationService, times(1)).getAllApplications(any(), any(), any(), any(), any(Pageable.class));
        verifyNoMoreInteractions(applicationService);
    }

    @Test
    void getById() throws Exception {
        UserDto applicant = TestData.userDto1();
        List<ResourceRequestDto> requestDtos = TestData.createMassRequestDtos();

        ApplicationDto applicationDto = TestData.applicationDto1();
        applicationDto.setApplicant(applicant);
        for (ResourceRequestDto requestDto : requestDtos) {
            requestDto.setApplication(applicationDto.getId());
            requestDto.setStatus(ResourceStatus.PENDING);
        }
        applicationDto.setResourceRequests(requestDtos);

        when(applicationService.getById(eq(applicationDto.getId())))
                .thenReturn(applicationDto);

        mockMvc.perform(get("/applications/" + applicationDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(applicationDto.getId()))
                .andExpect(jsonPath("$.type").value(applicationDto.getType().toString()))
                .andExpect(jsonPath("$.status").value(applicationDto.getStatus().toString()))
                .andExpect(jsonPath("$.created").value(applicationDto.getCreated().format(TestData.DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.applicant.id").value(applicationDto.getApplicant().getId()))
                .andExpect(jsonPath("$.resourceRequests", hasSize(applicationDto.getResourceRequests().size())));

        verify(applicationService, times(1)).getById(eq(applicationDto.getId()));
        verifyNoMoreInteractions(applicationService);

    }

    @Test
    void create() throws Exception {
        UserDto applicant = TestData.userDto1();
        List<ResourceRequestDto> requestDtos = TestData.createMassRequestDtos();

        NewApplicationDto newApplicationDto = TestData.newMassApplicationDto1();
        newApplicationDto.setApplicant(applicant);
        newApplicationDto.setResourceRequests(requestDtos);

        ApplicationDto applicationDto = TestData.applicationDto1();
        applicationDto.setApplicant(applicant);
        for (ResourceRequestDto requestDto : requestDtos) {
            requestDto.setApplication(applicationDto.getId());
            requestDto.setStatus(ResourceStatus.PENDING);
        }
        applicationDto.setResourceRequests(requestDtos);


        when(applicationService.createApplication(any(NewApplicationDto.class)))
                .thenReturn(applicationDto);

        mockMvc.perform(post("/applications")
                        .content(objectMapper.writeValueAsString(newApplicationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(applicationDto.getId()))
                .andExpect(jsonPath("$.type").value(applicationDto.getType().toString()))
                .andExpect(jsonPath("$.status").value(applicationDto.getStatus().toString()))
                .andExpect(jsonPath("$.created").value(applicationDto.getCreated().format(TestData.DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.applicant.id").value(applicationDto.getApplicant().getId()))
                .andExpect(jsonPath("$.resourceRequests", hasSize(applicationDto.getResourceRequests().size())));

        verify(applicationService, times(1)).createApplication(any(NewApplicationDto.class));
        verifyNoMoreInteractions(applicationService);
    }

    @Test
    void updateApplication() throws Exception {
        UserDto applicant = TestData.userDto1();
        applicant.setName("Шарик");
        List<ResourceRequestDto> requests = TestData.createMassRequestDtos();

        ApplicationDto applicationDto = TestData.applicationDto1();
        applicationDto.setApplicant(applicant);
        applicationDto.setResourceRequests(requests);

        UpdateApplicationDto updateApplicationDto = TestData.updateApplicationDto1();
        updateApplicationDto.setResourceRequests(requests);
        updateApplicationDto.setApplicant(applicant);

        when(applicationService.updateApplication(any(UpdateApplicationDto.class)))
                .thenReturn(applicationDto);

        mockMvc.perform(patch("/applications/" + updateApplicationDto.getId())
                        .content(objectMapper.writeValueAsString(updateApplicationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(applicationDto.getId()))
                .andExpect(jsonPath("$.type").value(applicationDto.getType().toString()))
                .andExpect(jsonPath("$.status").value(applicationDto.getStatus().toString()))
                .andExpect(jsonPath("$.created").value(applicationDto.getCreated().format(TestData.DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.applicant.id").value(applicationDto.getApplicant().getId()))
                .andExpect(jsonPath("$.applicant.name").value(applicationDto.getApplicant().getName()))
                .andExpect(jsonPath("$.resourceRequests", hasSize(applicationDto.getResourceRequests().size())));

        verify(applicationService, times(1)).updateApplication(any(UpdateApplicationDto.class));
        verifyNoMoreInteractions(applicationService);
    }
}