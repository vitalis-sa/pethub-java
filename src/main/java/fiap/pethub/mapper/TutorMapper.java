package fiap.pethub.mapper;

import fiap.pethub.dto.response.TutorContatoResponse;
import fiap.pethub.dto.response.TutorEnderecoResponse;
import fiap.pethub.dto.response.TutorResponse;
import fiap.pethub.entity.Tutor;
import fiap.pethub.entity.TutorContato;
import fiap.pethub.entity.TutorEndereco;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TutorMapper {
    TutorResponse toResponse(Tutor tutor);
    TutorEnderecoResponse toEnderecoResponse(TutorEndereco endereco);
    TutorContatoResponse toContatoResponse(TutorContato contato);
}
