package fiap.pethub.mapper;

import fiap.pethub.dto.request.UnidadeVeterinarioRequest;
import fiap.pethub.dto.response.UnidadeVeterinarioResponse;
import fiap.pethub.entity.UnidadeVeterinario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UnidadeVeterinarioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "veterinario", ignore = true)
    UnidadeVeterinario toEntity(UnidadeVeterinarioRequest request);

    @Mapping(source = "veterinario.nome", target = "nomeVeterinario")
    UnidadeVeterinarioResponse toResponse(UnidadeVeterinario unidade);

    List<UnidadeVeterinarioResponse> toResponseList(List<UnidadeVeterinario> list);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "veterinario", ignore = true)
    void updateEntity(UnidadeVeterinarioRequest request, @MappingTarget UnidadeVeterinario unidade);
}

