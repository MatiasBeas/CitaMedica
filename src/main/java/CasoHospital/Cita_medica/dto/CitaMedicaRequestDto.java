package CasoHospital.Cita_medica.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Estructura requerida para generar una nueva cita medica")
public class CitaMedicaRequestDto {

    @Schema(description = "Numero de folio", example = "10")
    @NotNull(message = "El numero de folio del bono no puede estar vacio")
    @Positive(message = "El numero de folio del bono debe ser mayor a 0")
    private Long folio_bono;

    @Schema(description = "Numero de registro de Staff", example = "12")
    @NotNull(message = "El numero de registro del medico no puede estar vacio")
    @Positive(message = "El numero de registro del medico debe ser mayor a 0")
    private Long num_registro;

    @Schema(description = "Fecha de la cita medica", example = "2026-08-15") // Actualizado a 2026
    @NotNull(message = "La fecha de la cita no puede estar vacia")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaCita;

    @Schema(description = "Hora de la cita medica", example = "12:45")
    @NotNull(message = "La hora de la cita no puede estar vacia")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaCita;

}