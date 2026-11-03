package CasoHospital.Cita_medica.controller;

import CasoHospital.Cita_medica.assemblers.CitaMedicaModelAssembler;
import CasoHospital.Cita_medica.dto.CitaMedicaRequestDto;
import CasoHospital.Cita_medica.dto.CitaMedicaResponseDto;
import CasoHospital.Cita_medica.service.CitaMedicaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springdoc.core.annotations.ParameterObject;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/citaMedica")
@RequiredArgsConstructor
@Tag(name = "Gestión de Citas Médicas", description = "Endpoints para administrar el agendamiento de citas médicas del Hospital")
public class CitaMedicaController {

    private final CitaMedicaService citaMedicaService;
    private final CitaMedicaModelAssembler assembler;

    //----------------- OBTENER TODAS LAS CITAS -----------------
    @Operation(summary = "Obtener todas las citas médicas", description = "Retorna todas las citas registradas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Citas Medicas retornadas exitosamente.",
                    content = @Content(schema = @Schema(implementation = PagedModel.class)))
    })
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<CitaMedicaResponseDto>>> obtenerTodas(
            @ParameterObject @PageableDefault(page = 0, size = 10) Pageable pageable,
            @Parameter(hidden = true) PagedResourcesAssembler<CitaMedicaResponseDto> pagedAssembler) {
        Page<CitaMedicaResponseDto> pagina = citaMedicaService.obtenerTodas(pageable);
        return ResponseEntity.ok(pagedAssembler.toModel(pagina, assembler));
    }

    //----------------- BUSCAR POR NUMERO DE CITA -----------------
    @Operation(summary = "Obtener cita médica por numero de cita", description = "Retorna la cita medica con el numero de cita")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cita médica encontrada exitosamente"),
            @ApiResponse(responseCode = "404", description = "El numCita ingresado no corresponde a ninguna cita médica")
    })
    @GetMapping("/{idCita}")
    public ResponseEntity<EntityModel<CitaMedicaResponseDto>> obtenerPorId(
            @Parameter(description = "Numero de cita(ID) a buscar", example = "1")
            @PathVariable Long idCita) {
        CitaMedicaResponseDto cita = citaMedicaService.obtenerPorId(idCita);
        return ResponseEntity.ok(assembler.toModel(cita));
    }

    //----------------- BUSCAR POR FOLIO DE BONO -----------------
    @Operation(summary = "Obtener citas por folio de bono", description = "Retorna una lista de citas asociadas a un número de folio")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Citas encontradas exitosamente",
                    content = @Content(schema = @Schema(implementation = PagedModel.class)))
    })
    @GetMapping("/folio/{folio}")
    public ResponseEntity<PagedModel<EntityModel<CitaMedicaResponseDto>>> buscarPorFolio(
            @Parameter(description = "Número de folio del bono asociado", example = "10")
            @PathVariable Long folio,
            @ParameterObject @PageableDefault(page = 0, size = 10) Pageable pageable,
            @Parameter(hidden = true) PagedResourcesAssembler<CitaMedicaResponseDto> pagedAssembler) {

        Page<CitaMedicaResponseDto> pagina = citaMedicaService.buscarPorFolioBono(folio, pageable);
        return ResponseEntity.ok(pagedAssembler.toModel(pagina, assembler));
    }

    //----------------- BUSCAR POR RUN DEL PACIENTE -----------------
    @Operation(summary = "Obtener citas por RUN del paciente", description = "Retorna el historial de citas asociadas al RUN de un paciente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Citas encontradas exitosamente",
                    content = @Content(schema = @Schema(implementation = PagedModel.class)))
    })
    @GetMapping("/run/{run}")
    public ResponseEntity<PagedModel<EntityModel<CitaMedicaResponseDto>>> buscarPorNumRun(
            @Parameter(description = "RUN del paciente (con puntos y guion)", example = "22.359.190-6")
            @PathVariable String run,
            @ParameterObject @PageableDefault(page = 0, size = 10) Pageable pageable,
            @Parameter(hidden = true) PagedResourcesAssembler<CitaMedicaResponseDto> pagedAssembler) {

        Page<CitaMedicaResponseDto> pagina = citaMedicaService.buscarPorNumRun(run, pageable);
        return ResponseEntity.ok(pagedAssembler.toModel(pagina, assembler));
    }

    //----------------- BUSCAR POR FECHA -----------------
    @Operation(summary = "Buscar citas por fecha", description = "Retorna una lista de las citas programadas para un día específico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Citas encontradas exitosamente",
                    content = @Content(schema = @Schema(implementation = PagedModel.class)))
    })
    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<PagedModel<EntityModel<CitaMedicaResponseDto>>> buscarPorFecha(
            @Parameter(description = "Fecha de la cita a buscar (formato YYYY-MM-DD)", example = "2026-04-06")
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @ParameterObject @PageableDefault(page = 0, size = 10) Pageable pageable,
            @Parameter(hidden = true) PagedResourcesAssembler<CitaMedicaResponseDto> pagedAssembler) {

        Page<CitaMedicaResponseDto> pagina = citaMedicaService.buscarPorFecha(fecha, pageable);
        return ResponseEntity.ok(pagedAssembler.toModel(pagina, assembler));
    }

    //----------------- CREAR CITA MEDICA -----------------
    @Operation(summary = "Agendar una nueva cita médica", description = "Crea una cita")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cita agendada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en el cuerpo de la petición"),
            @ApiResponse(responseCode = "404", description = "El folio del bono o el registro del médico no existen")
    })
    @PostMapping
    public ResponseEntity<EntityModel<CitaMedicaResponseDto>> crear(
            @Valid @RequestBody CitaMedicaRequestDto dto) {

        CitaMedicaResponseDto citaGuardada = citaMedicaService.guardar(dto);
        return ResponseEntity.status(201).body(assembler.toModel(citaGuardada));
    }

    //----------------- ACTUALIZAR CITA MEDICA -----------------
    @Operation(summary = "Actualizar cita médica", description = "Modifica los datos de una cita médica existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cita médica actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en el cuerpo de la petición"),
            @ApiResponse(responseCode = "404", description = "El numero de la cita no existe o los datos validados (bono/staff) no son correctos")
    })
    @PutMapping("/{idCita}")
    public ResponseEntity<EntityModel<CitaMedicaResponseDto>> actualizar(
            @Parameter(description = "numero de cita de la cita médica a actualizar", example = "1")
            @PathVariable Long idCita,
            @Valid @RequestBody CitaMedicaRequestDto dto) {

        CitaMedicaResponseDto citaActualizada = citaMedicaService.actualizar(idCita, dto);
        return ResponseEntity.ok(assembler.toModel(citaActualizada));
    }

    //----------------- ELIMINAR CITA MEDICA -----------------
    @Operation(summary = "Eliminar cita médica", description = "Elimina una cita médica del sistema mediante su numero de cita (ID)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cita médica eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "El numero de cita ingresado no corresponde a ninguna cita médica")
    })
    @DeleteMapping("/{idCita}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la cita médica a eliminar", example = "1")
            @PathVariable Long idCita) {

        citaMedicaService.eliminar(idCita);
        return ResponseEntity.noContent().build();
    }
}