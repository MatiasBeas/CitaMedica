package CasoHospital.Cita_medica.service;

import CasoHospital.Cita_medica.dto.CitaMedicaRequestDto;
import CasoHospital.Cita_medica.dto.CitaMedicaResponseDto;
import CasoHospital.Cita_medica.exception.RecursoNoEncontradoException;
import CasoHospital.Cita_medica.model.CitaMedica;
import CasoHospital.Cita_medica.repository.CitaMedicaRepository;
import CasoHospital.Cita_medica.webclient.BonoClient;
import CasoHospital.Cita_medica.webclient.StaffClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - CitaMedicaService")
public class CitaMedicaServiceTest {

    @Mock
    private CitaMedicaRepository citaMedicaRepository;

    @Mock
    private StaffClient staffClient;

    @Mock
    private BonoClient bonoClient;

    @InjectMocks
    private CitaMedicaService citaMedicaService;

    @Test
    @DisplayName("GIVEN: Pageable WHEN: obtenerTodas THEN: Retorna Page de CitaMedicaResponseDto")
    void shouldObtenerTodas() {
        CitaMedica cita = new CitaMedica(1L, 10L, "11.111.111-1", "22.222.222-2", LocalDate.now(), LocalTime.of(10, 0), "Programada", new BigDecimal("20000"));
        Page<CitaMedica> paginaFalsa = new PageImpl<>(List.of(cita));
        Pageable pageable = PageRequest.of(0, 10);

        when(citaMedicaRepository.findAll(pageable)).thenReturn(paginaFalsa);
        Page<CitaMedicaResponseDto> resultado = citaMedicaService.obtenerTodas(pageable);
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        assertEquals(1L, resultado.getContent().get(0).getNumCita());
        verify(citaMedicaRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("GIVEN: ID válido WHEN: obtenerPorId THEN: Retorna CitaMedicaResponseDto")
    void shouldObtenerPorId() {
        CitaMedica cita = new CitaMedica(1L, 10L, "11.111.111-1", "22.222.222-2", LocalDate.now(), LocalTime.of(10, 0), "Programada", new BigDecimal("20000"));
        when(citaMedicaRepository.findById(1L)).thenReturn(Optional.of(cita));

        CitaMedicaResponseDto resultado = citaMedicaService.obtenerPorId(1L);
        assertNotNull(resultado);
        assertEquals(1L, resultado.getNumCita());
        assertEquals("Programada", resultado.getEstadoCita());
        verify(citaMedicaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("GIVEN: ID inexistente WHEN: obtenerPorId THEN: Lanza Exception")
    void shouldThrowExceptionWhenObtenerPorIdNoExiste() {
        when(citaMedicaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> {
            citaMedicaService.obtenerPorId(99L);
        });
    }

    @Test
    @DisplayName("GIVEN: DTO válido WHEN: guardar THEN: Retorna CitaMedicaResponseDto guardada")
    void shouldGuardarCitaExitosamente() {
        CitaMedicaRequestDto request = new CitaMedicaRequestDto();
        request.setFolio_bono(100L);
        request.setNum_registro(200L);
        request.setFechaCita(LocalDate.now());
        request.setHoraCita(LocalTime.of(15, 30));

        Map<String, Object> mockBono = Map.of(
                "run", "12.345.678-9",
                "montoCopago", "5000",
                "montoSeguro", "15000"
        );

        Map<String, Object> mockStaff = Map.of(
                "run", "98.765.432-1"
        );

        CitaMedica citaGuardada = new CitaMedica(1L, 100L, "98.765.432-1", "12.345.678-9", LocalDate.now(), LocalTime.of(15, 30), "Programada", new BigDecimal("20000"));

        when(bonoClient.obtenerBono(100L)).thenReturn(mockBono);
        when(staffClient.obtenerStaff(200L)).thenReturn(mockStaff);
        when(citaMedicaRepository.save(any(CitaMedica.class))).thenReturn(citaGuardada);

        CitaMedicaResponseDto resultado = citaMedicaService.guardar(request);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getNumCita());
        assertEquals(new BigDecimal("20000"), resultado.getValorCita());
        verify(bonoClient, times(1)).obtenerBono(100L);
        verify(staffClient, times(1)).obtenerStaff(200L);
        verify(citaMedicaRepository, times(1)).save(any(CitaMedica.class));
    }

    @Test
    @DisplayName("GIVEN: Bono inexistente WHEN: guardar THEN: Lanza Exception")
    void shouldThrowExceptionWhenBonoNoExisteAlGuardar() {
        CitaMedicaRequestDto request = new CitaMedicaRequestDto();
        request.setFolio_bono(99L);

        when(bonoClient.obtenerBono(99L)).thenReturn(null);

        assertThrows(RecursoNoEncontradoException.class, () -> {
            citaMedicaService.guardar(request);
        });

        verify(citaMedicaRepository, never()).save(any(CitaMedica.class));
    }

    @Test
    @DisplayName("GIVEN: ID válido WHEN: eliminar THEN: Ejecuta deleteById")
    void shouldEliminarCita() {
        Long idCita = 1L;
        when(citaMedicaRepository.existsById(idCita)).thenReturn(true);
        doNothing().when(citaMedicaRepository).deleteById(idCita);

        citaMedicaService.eliminar(idCita);
        verify(citaMedicaRepository, times(1)).existsById(idCita);
        verify(citaMedicaRepository, times(1)).deleteById(idCita);
    }
}