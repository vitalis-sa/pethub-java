package fiap.pethub.mapper;

import fiap.pethub.dto.request.LeituraWearableRequest;
import fiap.pethub.dto.response.LeituraWearableResponse;
import fiap.pethub.entity.LeituraWearable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LeituraWearableMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pet", ignore = true)
    LeituraWearable toEntity(LeituraWearableRequest request);

    @Mapping(source = "pet.nome", target = "nomePet")
    LeituraWearableResponse toResponse(LeituraWearable leitura);

    List<LeituraWearableResponse> toResponseList(List<LeituraWearable> list);
}

