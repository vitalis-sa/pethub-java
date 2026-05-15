package fiap.pethub.mapper;

import fiap.pethub.dto.response.TutorResponse;
import fiap.pethub.entity.Tutor;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TutorMapper {
    TutorResponse toResponse(Tutor tutor);
}

