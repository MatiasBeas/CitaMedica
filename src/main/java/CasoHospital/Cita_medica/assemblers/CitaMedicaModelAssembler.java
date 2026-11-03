package CasoHospital.Cita_medica.assemblers;

import CasoHospital.Cita_medica.controller.CitaMedicaController;
import CasoHospital.Cita_medica.dto.CitaMedicaResponseDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CitaMedicaModelAssembler implements RepresentationModelAssembler<CitaMedicaResponseDto, EntityModel<CitaMedicaResponseDto>> {

    @Override
    public EntityModel<CitaMedicaResponseDto> toModel(CitaMedicaResponseDto cita) {
        return EntityModel.of(cita,
                linkTo(methodOn(CitaMedicaController.class)
                        .obtenerPorId(cita.getNumCita()))
                        .withSelfRel(),
                linkTo(methodOn(CitaMedicaController.class)
                        .obtenerTodas(null, null))
                        .withRel("Todas-las-Citas-Medicas")
        );
    }
}