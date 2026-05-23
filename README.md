# PetHub API

Uma solução completa para o cuidado veterinário, conectando pets, tutores e clínicas em uma jornada inteligente: do agendamento de consultas ao controle de vacinas, exames, diagnósticos e lembretes, tudo em um só lugar.

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

### `GET /api/pets` – listagem completa

> Em caso de sucesso

[Espaço reservado para print]

### `GET /api/pets/{id}` – busca por ID

> Em caso de sucesso

[Espaço reservado para print]

> Em caso de erro

[Espaço reservado para print]

### `POST /api/pets` – criação com JSON

> Em caso de sucesso

[Espaço reservado para print]

> Em caso de erro

[Espaço reservado para print]

### `PUT /api/pets/{id}` – atualização

> Em caso de sucesso

[Espaço reservado para print]

> Em caso de erro

[Espaço reservado para print]

### `DELETE /api/pets/{id}` – deleção

> Em caso de sucesso

[Espaço reservado para print]

> Em caso de erro

[Espaço reservado para print]

### `GET /api/consultas` – listagem paginada

> Em caso de sucesso

[Espaço reservado para print]

### `POST /api/consultas` – criação

> Em caso de sucesso

[Espaço reservado para print]

> Em caso de erro

[Espaço reservado para print]

### `GET /api/exames` – listagem

> Em caso de sucesso

[Espaço reservado para print]

### `POST /api/exames` – criação

> Em caso de sucesso

[Espaço reservado para print]

> Em caso de erro

[Espaço reservado para print]

### `GET /api/diagnosticos` – listagem

> Em caso de sucesso

[Espaço reservado para print]

### `POST /api/diagnosticos` – criação

> Em caso de sucesso

[Espaço reservado para print]

> Em caso de erro

[Espaço reservado para print]

### `GET /api/leituras-wearable` – listagem

> Em caso de sucesso

[Espaço reservado para print]

### `POST /api/leituras-wearable` – criação

> Em caso de sucesso

[Espaço reservado para print]

> Em caso de erro

[Espaço reservado para print]

### `GET /api/pedidos-medicos` – listagem

> Em caso de sucesso

[Espaço reservado para print]

### `POST /api/pedidos-medicos` – criação

> Em caso de sucesso

[Espaço reservado para print]

> Em caso de erro

[Espaço reservado para print]

### `GET /api/responsaveis/buscar?cpf=` – busca por CPF

> Em caso de sucesso

[Espaço reservado para print]

> Em caso de erro

[Espaço reservado para print]

### POST /api/pets – Criar pet

```json
{
  "nome": "Rex",
  "dataNascimento": "2022-01-15",
  "responsavelId": 1,
  "veterinarioId": 1
}
```

### PUT /api/pets/{id} – Atualizar pet

```json
{
  "nome": "Rex Junior",
  "dataNascimento": "2022-01-15",
  "responsavelId": 1,
  "veterinarioId": 2
}
```

### Resposta (GET, POST, PUT)

```json
{
  "id": 1,
  "nome": "Rex",
  "dataNascimento": "2022-01-15",
  "responsavelId": 1,
  "veterinarioId": 1
}
```

### Resposta de erro – recurso não encontrado (404)

```json
{
  "status": 404,
  "message": "Pet não encontrado com id: 99"
}
```

### Resposta de erro – validação (400)

```json
{
  "status": 400,
  "message": "Erro de validação",
  "errors": [
    {
      "field": "nome",
      "message": "Nome é obrigatório"
    },
    {
      "field": "dataNascimento",
      "message": "Data de nascimento é obrigatória"
    }
  ]
}
```

### POST /api/consultas – Criar consulta

```json
{
  "dataHora": "2025-05-30T14:30:00",
  "tipo": "ROTINA",
  "status": "AGENDADA",
  "petId": 1,
  "veterinarioId": 1
}
```

### Resposta GET /api/consultas (listagem paginada)

```json
{
  "content": [
    {
      "id": 1,
      "dataHora": "2025-05-30T14:30:00",
      "tipo": "ROTINA",
      "status": "AGENDADA",
      "petId": 1,
      "veterinarioId": 1
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": []
  },
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true,
  "empty": false
}
```
