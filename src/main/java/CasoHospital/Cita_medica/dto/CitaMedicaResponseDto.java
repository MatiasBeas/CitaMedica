package CasoHospital.Cita_medica.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CitaMedicaResponseDto {

    @Schema(description = "Numero de cita generado automaticamente.", example = "2")
    private Long numCita;

    @Schema(description = "Numero de folio del Bono.", example = "2")
    private Long folioBono;

    @Schema(description = "RUN del Staff Medico asignado.", example = "15.234.567-8")
    private String runStaff;

    @Schema(description = "Run del paciente", example = "11.111.111-1")
    private String numRun;

    @Schema(description = "Fecha de la cita medica", example = "2023-08-03")
    private LocalDate fechaCita;

    @Schema(description = "Hora programada para la cita médica", example = "14:30")
    private LocalTime horaCita;

    @Schema(description = "Estado de la cita Medica", example = "Programada")
    private String estadoCita;

    @Schema(description = "Valor total de la cita medica (Copago + Seguro)", example = "35000")
    private BigDecimal valorCita;
}