package fiap.pethub.mapper;

import fiap.pethub.dto.request.VeterinarioRequest;
import fiap.pethub.dto.response.VeterinarioResponse;
import fiap.pethub.entity.Veterinario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VeterinarioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    Veterinario toEntity(VeterinarioRequest request);

    VeterinarioResponse toResponse(Veterinario veterinario);

    List<VeterinarioResponse> toResponseList(List<Veterinario> list);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "crmv", ignore = true)
    void updateEntity(VeterinarioRequest request, @MappingTarget Veterinario veterinario);
}

