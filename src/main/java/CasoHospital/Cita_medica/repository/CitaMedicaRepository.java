package CasoHospital.Cita_medica.repository;
import CasoHospital.Cita_medica.model.CitaMedica;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;


public interface CitaMedicaRepository extends JpaRepository<CitaMedica, Long> {

    Page<CitaMedica> findByFolioBono(Long folioBono, Pageable pageable);
    Page<CitaMedica> findByRunStaff(String runStaff, Pageable pageable);
    Page<CitaMedica> findByNumRun(String numRun, Pageable pageable);
    Page<CitaMedica> findByFechaCita(LocalDate fecha, Pageable pageable);

}