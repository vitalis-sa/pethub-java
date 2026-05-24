# PetHub API

Uma solução completa para o cuidado veterinário, conectando pets, responsáveis e clínicas em uma jornada inteligente: do agendamento de consultas ao controle de vacinas, exames, diagnósticos e lembretes, tudo em um só lugar.

## Grupo

| Nome | RM |
|---|---|
| Ana Flávia Camelo | RM561489 |
| Gustavo Kenji Terada | RM562745 |
| João Guilherme Carvalho Novaes | RM566234 |
| Pedro Chasci Puga | RM565154 |
| Lucas Figueiredo Vieira | RM561342 |

## Tecnologias

| Tecnologia | Descrição |
|---|---|
| Java 21 | Linguagem principal do projeto, utilizando recursos modernos como records e inferência de tipos |
| Spring Boot 4.0.6 | Framework base que auto-configura o servidor embutido (Tomcat), gerencia beans e simplifica a criação da API REST |
| Maven | Gerenciador de dependências e build do projeto; responsável por baixar bibliotecas e compilar o código |
| Spring Data JPA | Abstrai o acesso ao banco de dados, gerando automaticamente as queries SQL a partir de interfaces como JpaRepository |
| Oracle Database (FIAP) | Banco de dados relacional utilizado para persistir os registros de pets, consultas, exames e outros dados da clínica |
| Caffeine Cache | Cache in-memory com expiração de 10 minutos e limite de 500 entradas para otimizar leituras frequentes |
| Lombok | Elimina código repetitivo (boilerplate) gerando automaticamente getters, setters, construtores e toString via anotações como @Data, @RequiredArgsConstructor |
| MapStruct | Gera o código de conversão entre entidades e DTOs (entrada/saída) sem precisar escrever mapeamentos manualmente |
| Spring Validation | Valida os dados recebidos nas requisições com anotações como @NotBlank, @NotNull e @Size, retornando erros 400 automaticamente quando inválidos |
| Springdoc OpenAPI | Gera documentação automaticamente e fornece interface Swagger UI para testar os endpoints |
| Spring Security Crypto | Fornece BCrypt para hash de senhas de forma segura |


## Como executar

1. Configure o `application.properties` com suas credenciais Oracle (não versionado)
2. Execute: `mvn spring-boot:run`
3. API disponível em: `http://localhost:8080`
4. Documentação Swagger em: `http://localhost:8080/swagger-ui.html`

## Endpoints

| Método | Rota | Descrição |
|---|---|---|
| GET | /api/pets | Listar todos os pets (paginado) |
| GET | /api/pets/{id} | Buscar pet por ID |
| GET | /api/pets/responsavel?cpf= | Buscar pets por CPF do responsável |
| POST | /api/pets | Criar novo pet |
| PUT | /api/pets/{id} | Atualizar pet por ID |
| DELETE | /api/pets/{id} | Deletar pet por ID |
| GET | /api/consultas | Listar todas as consultas (paginado) |
| GET | /api/consultas/{id} | Buscar consulta por ID |
| POST | /api/consultas | Criar nova consulta |
| PUT | /api/consultas/{id} | Atualizar consulta |
| DELETE | /api/consultas/{id} | Deletar consulta |
| GET | /api/exames | Listar todos os exames (paginado) |
| GET | /api/exames/{id} | Buscar exame por ID |
| POST | /api/exames | Criar novo exame |
| PUT | /api/exames/{id} | Atualizar exame |
| DELETE | /api/exames/{id} | Deletar exame |
| GET | /api/diagnosticos | Listar todos os diagnósticos (paginado) |
| GET | /api/diagnosticos/{id} | Buscar diagnóstico por ID |
| POST | /api/diagnosticos | Criar novo diagnóstico |
| PUT | /api/diagnosticos/{id} | Atualizar diagnóstico |
| DELETE | /api/diagnosticos/{id} | Deletar diagnóstico |
| GET | /api/vacinas-tratamentos | Listar todas as vacinas/tratamentos (paginado) |
| GET | /api/vacinas-tratamentos/{id} | Buscar vacina/tratamento por ID |
| POST | /api/vacinas-tratamentos | Criar nova vacina/tratamento |
| PUT | /api/vacinas-tratamentos/{id} | Atualizar vacina/tratamento |
| DELETE | /api/vacinas-tratamentos/{id} | Deletar vacina/tratamento |
| GET | /api/pedidos-medicos | Listar todos os pedidos médicos (paginado) |
| GET | /api/pedidos-medicos/{id} | Buscar pedido médico por ID |
| POST | /api/pedidos-medicos | Criar novo pedido médico |
| PUT | /api/pedidos-medicos/{id} | Atualizar pedido médico |
| DELETE | /api/pedidos-medicos/{id} | Deletar pedido médico |
| GET | /api/leituras-wearable | Listar leituras wearable (paginado) |
| GET | /api/leituras-wearable/{id} | Buscar leitura wearable por ID |
| POST | /api/leituras-wearable | Criar nova leitura wearable |
| PUT | /api/leituras-wearable/{id} | Atualizar leitura wearable |
| DELETE | /api/leituras-wearable/{id} | Deletar leitura wearable |
| GET | /api/lembretes | Listar todos os lembretes (paginado) |
| GET | /api/lembretes/{id} | Buscar lembrete por ID |
| POST | /api/lembretes | Criar novo lembrete |
| PUT | /api/lembretes/{id} | Atualizar lembrete |
| DELETE | /api/lembretes/{id} | Deletar lembrete |
| GET | /api/veterinarios | Listar todos os veterinários (paginado) |
| GET | /api/veterinarios/{id} | Buscar veterinário por ID |
| POST | /api/veterinarios | Criar novo veterinário |
| PUT | /api/veterinarios/{id} | Atualizar veterinário |
| DELETE | /api/veterinarios/{id} | Deletar veterinário |
| GET | /api/unidades-veterinario | Listar todas as unidades (paginado) |
| GET | /api/unidades-veterinario/{id} | Buscar unidade por ID |
| POST | /api/unidades-veterinario | Criar nova unidade |
| PUT | /api/unidades-veterinario/{id} | Atualizar unidade |
| DELETE | /api/unidades-veterinario/{id} | Deletar unidade |
| GET | /api/responsaveis | Listar todos os responsáveis (paginado) |
| GET | /api/responsaveis/{id} | Buscar responsável por ID |
| GET | /api/responsaveis/buscar?cpf= | Buscar responsável por CPF |
| POST | /api/responsaveis | Criar novo responsável |
| PUT | /api/responsaveis/{id} | Atualizar responsável |
| DELETE | /api/responsaveis/{id} | Deletar responsável |


## Estrutura do Projeto

```
fiap.pethub
├── PethubApplication.java       → Classe principal com configuração OpenAPI
│
├── config/                      → Configurações da aplicação
│   ├── CacheConfig.java         → Configuração de Cache Caffeine
│   ├── PasswordUtil.java        → Utilitário de hash BCrypt
│   └── WebConfig.java           → Configurações Web
│
├── controller/                  → Controladores (endpoints REST)
│   ├── PetsController.java
│   ├── ConsultasController.java
│   ├── ExamesController.java
│   ├── DiagnosticosController.java
│   ├── VacinaTratamentosController.java
│   ├── PedidosMedicosController.java
│   ├── LeiturasWearableController.java
│   ├── LembretesController.java
│   ├── VeterinariosController.java
│   ├── UnidadesVeterinarioController.java
│   └── ResponsaveisController.java
│
├── dto/                         → Data Transfer Objects
│   ├── request/                 → DTOs de entrada (validação)
│   │   ├── CreatePetRequest.java
│   │   ├── CreateConsultaRequest.java
│   │   └── ...
│   └── response/                → DTOs de saída
│       ├── PetResponse.java
│       ├── ConsultaResponse.java
│       └── ...
│
├── entity/                      → Entidades JPA
│   ├── Pet.java
│   ├── Consulta.java
│   ├── Exame.java
│   ├── Diagnostico.java
│   ├── VacinaTratamento.java
│   ├── PedidoMedico.java
│   ├── LeituraWearable.java
│   ├── Lembrete.java
│   ├── Veterinario.java
│   ├── UnidadeVeterinario.java
│   ├── Responsavel.java
│   ├── ResponsavelContato.java
│   └── ResponsavelEndereco.java
│
├── enums/                       → Enumerações
│   ├── StatusConsulta.java
│   ├── StatusLembrete.java
│   ├── StatusPedidoMedico.java
│   ├── TipoAlertaHidratacao.java
│   ├── TipoConsulta.java
│   ├── TipoLembrete.java
│   ├── TipoPedidoMedico.java
│   └── TipoVacinaTratamento.java
│
├── exception/                   → Tratamento de exceções
│   ├── GlobalExceptionHandler.java  → Handler centralizado
│   ├── ResourceNotFoundException.java
│   ├── ErrorResponse.java
│   ├── ValidationErrorResponse.java
│   └── FieldErrorDetail.java
│
├── mapper/                      → Mapeadores MapStruct
│   ├── PetMapper.java
│   ├── ConsultaMapper.java
│   └── ...
│
├── repository/                  → Repositórios JPA
│   ├── PetRepository.java
│   ├── ConsultaRepository.java
│   └── ...
│
├── service/                     → Lógica de negócio
│   ├── PetService.java
│   ├── ConsultaService.java
│   └── ...
│
└── resources/
    └── application.properties   → Configurações da aplicação
```

## Requisitos Técnicos Atendidos

| Requisito | Status | Evidência |
|---|---|---|
| Validação de campos com Bean Validation | ✅ | @NotNull, @NotBlank, @Size em DTOs |
| Paginação de resultados | ✅ | Page<T> + Pageable em endpoints |
| Ordenação de resultados | ✅ | sort parameter em Pageable |
| Busca com parâmetros | ✅ | 12+ filtros (nome, cpf, status, etc) |
| Uso de cache | ✅ | Caffeine Cache com @Cacheable |
| Tratamento de erros/exceções | ✅ | GlobalExceptionHandler com 6+ handlers |
| Utilização de DTOs | ✅ | 15+ DTOs request/response + MapStruct |
| Documentação com Swagger | ✅ | OpenAPI 3.0 + Swagger UI |

## Prints das telas (Postman)
Fluxo seguindo as regras de negócio da solução.

### 01 - Criar unidade veterinaria - `POST /api/unidades`
<img width="363" height="571" alt="image" src="https://github.com/user-attachments/assets/584b69f9-941f-45be-badf-9bffca58c44f" />

### 02 - Criar veterinario - `POST /api/veterinarios`
<img width="376" height="557" alt="image" src="https://github.com/user-attachments/assets/e44c2f88-2d4c-4f64-b62f-1c540a289cea" />

### 03 - Criar responsavel - `POST /api/responsaveis`
<img width="410" height="505" alt="image" src="https://github.com/user-attachments/assets/32113249-fd3d-42cc-b5a2-01457aa3d179" />

### 04 - Adicionar contato do responsavel - `POST /api/responsaveis/{id}/contatos`
<img width="375" height="398" alt="image" src="https://github.com/user-attachments/assets/06355eef-2767-439e-bbf6-c97609fe8e78" />

### 05 - Adicionar endereco do responsavel - `POST /api/responsaveis/{id}/enderecos`
<img width="387" height="574" alt="image" src="https://github.com/user-attachments/assets/b9b2ec26-44a8-4894-acb1-d7ef0acaa112" />

### 06 - Criar pet - `POST /api/pets`
<img width="443" height="572" alt="image" src="https://github.com/user-attachments/assets/c6b7f33d-8694-4f31-acc5-8e710b475d52" />

### 07 - Criar consulta - `POST /api/consultas`
<img width="376" height="538" alt="image" src="https://github.com/user-attachments/assets/aa97af95-9398-4760-aa5c-0eade88b003e" />

### 08 - Criar diagnostico - `POST /api/diagnosticos`
<img width="408" height="902" alt="image" src="https://github.com/user-attachments/assets/18099359-a6e6-4e02-8d8c-6dea54bf9f60" />

### 09 - Criar exame - `POST /api/exames`
<img width="411" height="493" alt="image" src="https://github.com/user-attachments/assets/0bc54a79-ad0b-4728-9a49-4753e00ed110" />

### 10 - Criar pedido medico - `POST /api/pedidos-medicos`
<img width="412" height="519" alt="image" src="https://github.com/user-attachments/assets/94be250e-0e62-4181-bbde-705bf7025646" />

### 11 - Criar vacina/tratamento - `POST /api/vacinas-tratamentos`
<img width="435" height="564" alt="image" src="https://github.com/user-attachments/assets/040d7cf3-37c2-4d4c-8fff-72a16ec0ea72" />

### 12 - Criar leitura wearable - `POST /api/leituras-wearable`
<img width="356" height="506" alt="image" src="https://github.com/user-attachments/assets/20eee015-ca0c-4983-be4d-511dafbcafbe" />

### 13 - Criar lembrete - `POST /api/lembretes`
<img width="419" height="608" alt="image" src="https://github.com/user-attachments/assets/0eab360e-61bf-4e13-a560-0b8e48bc1f7a" />

### 14 - Atualizar status do lembrete - `PATCH /api/lembretes/1/status?status={status}`
<img width="440" height="547" alt="image" src="https://github.com/user-attachments/assets/2e197512-fd7c-4a60-b587-2ac3af0548ad" />

> Listagem para confirmação de informações

### 13 - Criar lembrete - `POST /api/lembretes`


