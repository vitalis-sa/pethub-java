# Autenticação e autorização — desenho

> Sprint 3 do Challenge 2026 · disciplina Java Advanced · item "Spring Security" (30 pontos)
> Escopo desta entrega: **API apenas**. A camada de visualização virá depois.

## Problema

A API expõe prontuário clínico de animais — consultas, diagnósticos, exames, prescrições e
telemetria de wearable — sem nenhuma autenticação. Qualquer pessoa com a URL lê e escreve tudo.

A rubrica exige autenticação, no mínimo dois tipos de usuário com permissões diferentes, e
proteção de rotas conforme o perfil.

## Decisões

### Identidade sai das entidades existentes, sem tabela nova

`Veterinario` e `Responsavel` já possuem `email` único e `senha` com hash BCrypt. O
`UserDetailsService` procura o email primeiro em `VeterinarioRepository` e, não achando, em
`ResponsavelRepository`. O perfil é determinado por qual repositório respondeu.

Foram consideradas e descartadas duas alternativas:

- **Tabela `TB_USUARIO` separada** — mais canônica, mas exige DDL num schema Oracle compartilhado
  com a API .NET `Vitalis`, e duplicaria as credenciais que já existem nas duas entidades.
- **Coluna `perfil` em cada entidade** — redundante: a tabela onde a linha está já determina o
  perfil.

O risco da abordagem escolhida é o mesmo email existir nas duas tabelas. É mitigado validando
unicidade cruzada no registro: antes de criar, verifica-se que o email não existe do outro lado.

### Token JWT, sem estado no servidor

O app mobile em React Native é o consumidor principal da API. Sessão com cookie funciona mal
nesse contexto, então a autenticação é por token no cabeçalho `Authorization: Bearer`.

Assinatura HS256, validade de 8 horas, `sub` com o email e claim `perfil`. Sem refresh token:
não há requisito para isso, e cada peça a mais é superfície a explicar na avaliação oral.

**O segredo vem da variável de ambiente `JWT_SECRET` e nunca do código-fonte.** Credencial
versionada custa pontos na rubrica de DevOps e é problema de segurança real.

### Proteção declarada por rota, não por anotação

As regras ficam no `SecurityFilterChain`, com `requestMatchers` por método HTTP e caminho. A
rubrica pede "proteção de rotas com base no perfil do usuário" — declarar isso num único lugar
legível responde melhor à exigência, e à pergunta "quem pode fazer o quê", do que `@PreAuthorize`
espalhado por onze controllers.

## Perfis e permissões

Dois perfis, com a diferença que o domínio já impõe: **o veterinário produz o prontuário, o
responsável o consulta.**

| Rotas | Acesso |
|---|---|
| `/api/auth/**` | público |
| `/swagger-ui/**`, `/api-docs/**` | público |
| `POST` `PUT` `DELETE` em consultas, diagnósticos, exames, pedidos-médicos, vacinas-tratamentos | `VETERINARIO` |
| `POST` `PUT` `DELETE` em pets, veterinários, unidades | `VETERINARIO` |
| `POST` em leituras-wearable | `VETERINARIO` |
| `GET` nas rotas clínicas | `VETERINARIO` ou `RESPONSAVEL` |
| `/api/lembretes/**` | `VETERINARIO` ou `RESPONSAVEL` |
| `/api/responsaveis/**` | `VETERINARIO` ou `RESPONSAVEL` |

O dispositivo IoT ainda não tem identidade própria; na Sprint 4 receberá conta de serviço. Até
lá, gravar leitura exige perfil de veterinário.

## Contrato

```
POST /api/auth/login
     { "email": "...", "senha": "..." }
  → 200 { "token": "...", "perfil": "VETERINARIO", "nome": "...", "id": 1 }
  → 401 credenciais inválidas

POST /api/auth/registrar/responsavel
     { "nome", "cpf", "email", "senha" }
  → 201 { token, perfil, nome, id }
  → 409 email ou CPF já cadastrado

POST /api/auth/registrar/veterinario
     { "nome", "crmv", "especialidade", "email", "telefone", "senha" }
  → 201 { token, perfil, nome, id }
  → 409 email ou CRMV já cadastrado
```

Auto-cadastro é aberto nos dois perfis, para que a solução possa ser demonstrada sem semear o
banco. Num sistema em produção, criar veterinário exigiria aprovação — a restrição é conhecida e
deliberada, não um descuido.

## Componentes

```
enums/Perfil.java                       VETERINARIO, RESPONSAVEL
security/UsuarioAutenticado.java        implementa UserDetails
security/UsuarioDetailsService.java     resolve email → veterinário ou responsável
security/JwtService.java                gera e valida token
security/JwtAuthenticationFilter.java   lê o cabeçalho e popula o SecurityContext
config/SecurityConfig.java              SecurityFilterChain, AuthenticationManager, PasswordEncoder
service/AuthService.java                autenticação e registro
controller/AuthController.java          os três endpoints acima
dto/request/LoginRequest.java
dto/response/LoginResponse.java
```

Dependências novas: `spring-boot-starter-security` e `jjwt` (`jjwt-api`, `jjwt-impl`,
`jjwt-jackson`).

## Melhoria pontual incluída

O projeto tem hoje dois codificadores de senha: a classe estática `config/PasswordUtil` e o bean
`passwordEncoder()` em `config/WebConfig`. São duas instâncias independentes de
`BCryptPasswordEncoder` para a mesma responsabilidade — violação de DRY que a rubrica desconta e
fonte de bug silencioso caso as configurações divirjam. Como a mudança mexe exatamente nessa
área, o `PasswordUtil` é removido e tudo passa a usar o bean.

## Impacto nos consumidores

Toda rota fora de `/api/auth/**` passa a exigir token. Isso quebra, no estado atual:

- o app mobile em React Native;
- a integração da API .NET `Vitalis`, que chama endpoints do Java;
- o `scripts/crud-test.sh` do workspace.

O script de teste é atualizado junto com esta mudança. Os outros dois consumidores precisam ser
avisados: a API passa a exigir `Authorization: Bearer <token>`.

## Testes

- `JwtService`: token gerado é válido e carrega email e perfil; token expirado é rejeitado;
  token com assinatura adulterada é rejeitado.
- `UsuarioDetailsService`: encontra veterinário por email com perfil `VETERINARIO`; encontra
  responsável com perfil `RESPONSAVEL`; lança `UsernameNotFoundException` para email inexistente.
- `AuthService`: registro rejeita email já usado no outro perfil.
- Integração (`scripts/crud-test.sh`): requisição sem token responde 401; responsável tentando
  criar consulta responde 403; veterinário autenticado percorre o CRUD completo.
