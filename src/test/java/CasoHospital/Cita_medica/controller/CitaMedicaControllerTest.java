package CasoHospital.Cita_medica.controller;

import CasoHospital.Cita_medica.assemblers.CitaMedicaModelAssembler;
import CasoHospital.Cita_medica.dto.CitaMedicaRequestDto;
import CasoHospital.Cita_medica.dto.CitaMedicaResponseDto;
import CasoHospital.Cita_medica.security.JwtService;
import CasoHospital.Cita_medica.service.CitaMedicaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CitaMedicaController.class)
@ActiveProfiles("test")
@Import(CitaMedicaModelAssembler.class)
@WithMockUser
@DisplayName("Tests Unitarios - CitaMedicaController")
public class CitaMedicaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CitaMedicaService citaMedicaService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @DisplayName("GIVEN: Existen citas WHEN: GET /api/citaMedica THEN: Retorna 200 OK y la lista paginada")
    void shouldReturnTodasLasCitas() throws Exception {
        CitaMedicaResponseDto cita1 = new CitaMedicaResponseDto();
        cita1.setNumCita(1L);
        cita1.setFechaCita(LocalDate.now());
        CitaMedicaResponseDto cita2 = new CitaMedicaResponseDto();
        cita2.setNumCita(2L);
        cita2.setFechaCita(LocalDate.now());
        List<CitaMedicaResponseDto> listaFalsa = Arrays.asList(cita1, cita2);
        Page<CitaMedicaResponseDto> paginaFalsa = new PageImpl<>(listaFalsa);

        when(citaMedicaService.obtenerTodas(any(Pageable.class))).thenReturn(paginaFalsa);

        mockMvc.perform(get("/api/citaMedica")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.citaMedicaResponseDtoList[0].numCita").value(1))
                .andExpect(jsonPath("$._embedded.citaMedicaResponseDtoList[1].numCita").value(2));
    }

    @Test
    @DisplayName("GIVEN: ID válido WHEN: GET /api/citaMedica/{id} THEN: Retorna 200 OK")
    void shouldReturnCitaById() throws Exception {
        CitaMedicaResponseDto cita = new CitaMedicaResponseDto();
        cita.setNumCita(1L);

        when(citaMedicaService.obtenerPorId(1L)).thenReturn(cita);

        mockMvc.perform(get("/api/citaMedica/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numCita").value(1));
    }

    @Test
    @DisplayName("GIVEN: DTO válido WHEN: POST /api/citaMedica THEN: Retorna 201 Created")
    void shouldCreateCita() throws Exception {
        CitaMedicaRequestDto request = new CitaMedicaRequestDto();
        request.setFolio_bono(10L);
        request.setNum_registro(12L);
        request.setFechaCita(LocalDate.now());
        request.setHoraCita(LocalTime.of(12, 45));

        CitaMedicaResponseDto response = new CitaMedicaResponseDto();
        response.setNumCita(1L);

        when(citaMedicaService.guardar(any(CitaMedicaRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/citaMedica")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numCita").value(1));
    }

    @Test
    @DisplayName("GIVEN: ID válido WHEN: DELETE /api/citaMedica/{id} THEN: Retorna 204 No Content")
    void shouldDeleteCita() throws Exception {
        Long numCita = 1L;

        doNothing().when(citaMedicaService).eliminar(numCita);

        mockMvc.perform(delete("/api/citaMedica/" + numCita).with(csrf()))
                .andExpect(status().isNoContent());
    }
}