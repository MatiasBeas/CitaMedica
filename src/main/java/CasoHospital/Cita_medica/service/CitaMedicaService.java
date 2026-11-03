package CasoHospital.Cita_medica.service;

import CasoHospital.Cita_medica.dto.CitaMedicaRequestDto;
import CasoHospital.Cita_medica.dto.CitaMedicaResponseDto;
import CasoHospital.Cita_medica.exception.RecursoNoEncontradoException;
import CasoHospital.Cita_medica.model.CitaMedica;
import CasoHospital.Cita_medica.repository.CitaMedicaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import CasoHospital.Cita_medica.webclient.BonoClient;
import CasoHospital.Cita_medica.webclient.StaffClient;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CitaMedicaService {

    private final CitaMedicaRepository citaMedicaRepository;
    private final StaffClient staffClient;
    private final BonoClient bonoClient;

    private CitaMedicaResponseDto mapToDto(CitaMedica cita) {
        return new CitaMedicaResponseDto(
                cita.getNumCita(),
                cita.getFolioBono(),
                cita.getRunStaff(),
                cita.getNumRun(),
                cita.getFechaCita(),
                cita.getHoraCita(),
                cita.getEstadoCita(),
                cita.getValorCita()
        );
    }

    public Page<CitaMedicaResponseDto> obtenerTodas(Pageable pageable) {
        return citaMedicaRepository.findAll(pageable).map(this::mapToDto);
    }

    public CitaMedicaResponseDto obtenerPorId(Long idCita) {
        CitaMedica cita = citaMedicaRepository.findById(idCita)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe una cita con el ID " + idCita));
        return mapToDto(cita);
    }

    public Page<CitaMedicaResponseDto> buscarPorFecha(LocalDate fecha, Pageable pageable) {
        Page<CitaMedicaResponseDto> pagina = citaMedicaRepository.findByFechaCita(fecha, pageable).map(this::mapToDto);
        if (pagina.isEmpty()) {
            throw new RecursoNoEncontradoException("No existen citas registradas para la fecha " + fecha);
        }
        return pagina;
    }

    public Page<CitaMedicaResponseDto> buscarPorFolioBono(Long folio, Pageable pageable) {
        Page<CitaMedicaResponseDto> pagina = citaMedicaRepository.findByFolioBono(folio, pageable).map(this::mapToDto);
        if (pagina.isEmpty()) {
            throw new RecursoNoEncontradoException("No existen citas con el folio de bono " + folio);
        }
        return pagina;
    }

    public Page<CitaMedicaResponseDto> buscarPorNumRun(String run, Pageable pageable) {
        Page<CitaMedicaResponseDto> pagina = citaMedicaRepository.findByNumRun(run, pageable).map(this::mapToDto);
        if (pagina.isEmpty()) {
            throw new RecursoNoEncontradoException("No existen citas para el RUN " + run);
        }
        return pagina;
    }

    @Transactional
    public CitaMedicaResponseDto guardar(CitaMedicaRequestDto dto) {
        Map<String, Object> bono = bonoClient.obtenerBono(dto.getFolio_bono());
        if (bono == null) {
            throw new RecursoNoEncontradoException("El bono con folio " + dto.getFolio_bono() + " no existe.");
        }
        String runPaciente = bono.get("run").toString();
        BigDecimal montoCopago = new BigDecimal(bono.get("montoCopago").toString());
        BigDecimal montoSeguro = new BigDecimal(bono.get("montoSeguro").toString());
        BigDecimal valorTotal = montoCopago.add(montoSeguro);
        Map<String, Object> staff = staffClient.obtenerStaff(dto.getNum_registro());
        if (staff == null) {
            throw new RecursoNoEncontradoException("El médico con registro " + dto.getNum_registro() + " no existe.");
        }
        String runMedico = staff.get("run").toString();
        CitaMedica nuevaCita = new CitaMedica();
        nuevaCita.setFolioBono(dto.getFolio_bono());
        nuevaCita.setRunStaff(runMedico);
        nuevaCita.setNumRun(runPaciente);
        nuevaCita.setFechaCita(dto.getFechaCita());
        nuevaCita.setHoraCita(dto.getHoraCita());
        nuevaCita.setEstadoCita("Programada");
        nuevaCita.setValorCita(valorTotal);
        return mapToDto(citaMedicaRepository.save(nuevaCita));
    }

    @Transactional
    public CitaMedicaResponseDto actualizar(Long idCita, CitaMedicaRequestDto dto) {
        CitaMedica existente = citaMedicaRepository.findById(idCita)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe una cita con el ID " + idCita));
        if (!existente.getFolioBono().equals(dto.getFolio_bono())) {
            Map<String, Object> bono = bonoClient.obtenerBono(dto.getFolio_bono());
            if (bono == null) {
                throw new RecursoNoEncontradoException("El nuevo bono con folio " + dto.getFolio_bono() + " no existe.");
            }
            existente.setFolioBono(dto.getFolio_bono());
            existente.setNumRun(bono.get("run").toString());
            BigDecimal montoCopago = new BigDecimal(bono.get("montoCopago").toString());
            BigDecimal montoSeguro = new BigDecimal(bono.get("montoSeguro").toString());
            existente.setValorCita(montoCopago.add(montoSeguro));
        }

        if (!existente.getRunStaff().equals(dto.getNum_registro().toString())) {
            Map<String, Object> staff = staffClient.obtenerStaff(dto.getNum_registro());
            if (staff == null) {
                throw new RecursoNoEncontradoException("El nuevo médico con registro " + dto.getNum_registro() + " no existe.");
            }
            existente.setRunStaff(staff.get("run").toString());
        }
        existente.setFechaCita(dto.getFechaCita());
        existente.setHoraCita(dto.getHoraCita());
        return mapToDto(citaMedicaRepository.save(existente));
    }

    @Transactional
    public void eliminar(Long idCita) {
        if (!citaMedicaRepository.existsById(idCita)) {
            throw new RecursoNoEncontradoException("No existe una cita con el ID " + idCita);
        }
        citaMedicaRepository.deleteById(idCita);
    }
}