//package CasoHospital.Cita_medica.confi;
//
//import CasoHospital.Cita_medica.model.CitaMedica;
//import CasoHospital.Cita_medica.repository.CitaMedicaRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.time.LocalTime;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class DataInitializer implements CommandLineRunner {
//
//    private final CitaMedicaRepository citaMedicaRepository;
//
//    @Override
//    public void run(String... args) {
//
//        if (citaMedicaRepository.count() > 0) {
//            log.info(">>> DataInitializer: la BD ya tiene datos, se omite la carga inicial.");
//            return;
//        }
//
//        log.info(">>> DataInitializer: BD vacía detectada, insertando datos de prueba...");
//
//
//        citaMedicaRepository.save(new CitaMedica(null,1001L,
//                        1L,"22.359.190-6",LocalDate.of(2026, 5, 20),
//                        LocalTime.of(10, 30),"PENDIENTE",new BigDecimal("25000"))
//        );
//
//        citaMedicaRepository.save(new CitaMedica(null,1002L,
//                        2L,"18.765.432-1",LocalDate.of(2026, 5, 21),
//                        LocalTime.of(11, 0),"CONFIRMADA",new BigDecimal("30000"))
//        );
//
//        citaMedicaRepository.save(new CitaMedica(null,3L,
//                        3L,"11.111.111-1",LocalDate.of(2026, 5, 22),
//                        LocalTime.of(15, 45),
//                        "FINALIZADA",new BigDecimal("28000"))
//        );
//
//        citaMedicaRepository.save(new CitaMedica(null,1004L,
//                        4L,"22.222.222-2",LocalDate.of(2026, 5, 23),
//                        LocalTime.of(9, 15),
//                        "CANCELADA",new BigDecimal("25000"))
//        );
//
//        citaMedicaRepository.save(new CitaMedica(null,1005L,
//                        5L,"10.333.333-3",
//                        LocalDate.of(2026, 5, 24),LocalTime.of(14, 0),
//                        "PENDIENTE",new BigDecimal("32000"))
//        );
//
//        log.info(">>> DataInitializer: {} citas insertadas correctamente.",
//                citaMedicaRepository.count());
//    }
//}