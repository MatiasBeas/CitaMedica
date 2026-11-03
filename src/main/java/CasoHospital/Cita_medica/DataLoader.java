package CasoHospital.Cita_medica;

import CasoHospital.Cita_medica.model.CitaMedica;
import CasoHospital.Cita_medica.model.Role;
import CasoHospital.Cita_medica.model.User;
import CasoHospital.Cita_medica.repository.CitaMedicaRepository;
import CasoHospital.Cita_medica.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner{

        private final CitaMedicaRepository citaMedicaRepository;
        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;

        @Override
        public void run(String... args) throws Exception {

            if (userRepository.findByUsername("valentina").isEmpty()) {
                User adminUser = new User();
                adminUser.setUsername("valentina");
                adminUser.setPassword(passwordEncoder.encode("123"));
                adminUser.setRole(Role.ADMIN);
                userRepository.save(adminUser);
                log.info("Usuario administrador creado automáticamente (username: valentina).");
            }
            if (userRepository.findByUsername("Maty").isEmpty()) {
                User adminUser2 = new User();
                adminUser2.setUsername("Maty");
                adminUser2.setPassword(passwordEncoder.encode("1234"));
                adminUser2.setRole(Role.ADMIN);
                userRepository.save(adminUser2);
                log.info("Usuario administrador creado automáticamente (username: Maty).");
            }

            if (citaMedicaRepository.count() > 0) {
                log.info(">>> DataFaker: La BD ya tiene citas médicas. Carga omitida.");
                return;
            }

            log.info(">>> DataFaker: Generando 100 citas médicas validadas con el negocio...");
            Faker faker = new Faker(Locale.of("es"));

            String[] estados = {"Programada", "Cancelada", "Realizada"};

            List<String> poolPacientes = List.of(
                    "22.359.190-6", "18.765.432-1", "11.111.111-1",
                    "22.222.222-2", "10.333.333-3"
            );

            List<String> poolDoctores = List.of(
                    "19.754.739-4", "24.569.451-0", "94.222.646-5",
                    "23.151.289-2", "30.639.957-4", "89.210.159-8",
                    "37.276.672-2", "85.584.364-2", "17.347.129-0",
                    "31.679.481-0", "82.154.553-4", "71.354.152-5",
                    "34.341.704-8", "25.874.730-3"
            );

            for (int i = 0; i < 100; i++) {
                CitaMedica cita = new CitaMedica();
                long folioAsignado = (long) (i + 1);
                cita.setFolioBono(folioAsignado);

                cita.setRunStaff(poolDoctores.get(faker.random().nextInt(poolDoctores.size())));
                cita.setNumRun(poolPacientes.get(faker.random().nextInt(poolPacientes.size())));

                double valorTotalCita = faker.number().randomDouble(0, 7000, 260000);
                cita.setValorCita(BigDecimal.valueOf(valorTotalCita));

                String estadoSeleccionado = estados[faker.random().nextInt(estados.length)];
                cita.setEstadoCita(estadoSeleccionado);

                int diasAtrasBono = faker.number().numberBetween(0, 365);
                LocalDate fechaEmisionBono = LocalDate.now().minusDays(diasAtrasBono);

                LocalDate fechaCita;

                if (estadoSeleccionado.equals("Programada")) {
                    int diasFuturos = faker.number().numberBetween(1, 31);
                    fechaCita = LocalDate.now().plusDays(diasFuturos);

                } else if (estadoSeleccionado.equals("Realizada")) {
                    long diasDisponibles = ChronoUnit.DAYS.between(fechaEmisionBono, LocalDate.now());
                    int diasAdicionales = faker.number().numberBetween(0, (int) diasDisponibles + 1);
                    fechaCita = fechaEmisionBono.plusDays(diasAdicionales);

                } else {
                    int diasDesdeEmision = faker.number().numberBetween(0, 395);
                    fechaCita = fechaEmisionBono.plusDays(diasDesdeEmision);
                }

                cita.setFechaCita(fechaCita);

                int hora = faker.number().numberBetween(8, 17);
                Integer[] minutos = {0, 15, 30, 45};
                int minuto = faker.options().option(minutos);
                cita.setHoraCita(LocalTime.of(hora, minuto));

                citaMedicaRepository.save(cita);
            }

            log.info(">>> DataFaker: ¡100 citas médicas insertadas exitosamente");
        }
}