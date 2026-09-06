# Autorização por posse de recurso — desenho

> Sprint 3 · complementa `seguranca-design.md`, que entregou autenticação e proteção por perfil.

## Problema

A autorização atual é RBAC puro: verifica o perfil (`VETERINARIO` / `RESPONSAVEL`), nunca a
relação entre quem pede e o recurso pedido. Na prática, qualquer tutor autenticado lê o
prontuário de **todos** os pets do sistema, e pode editar o cadastro de outro tutor via
`PUT /api/responsaveis/{id}`.

## Decisões

### A posse restringe apenas o RESPONSAVEL

O veterinário é corpo clínico: atende qualquer pet, como numa clínica real onde o profissional
de plantão precisa ver o paciente à sua frente. Restringi-lo a "seus" pets quebraria o
atendimento — ele não conseguiria abrir consulta para um animal que não estivesse atribuído a
ele.

Foram consideradas e descartadas duas alternativas: escopo por veterinário responsável (trava o
atendimento) e escopo por unidade (o modelo mal suporta — `Pet` não tem unidade, apenas
`Veterinario` e `Consulta` têm).

### Um salto resolve qualquer dono

Toda entidade clínica carrega `pet_id`, e `Pet` carrega `responsavel_id`:

```
Responsavel
  └── Pet ── Consulta ── Diagnostico / Exame / PedidoMedico / VacinaTratamento
        ├──  LeituraWearable
        └──  Lembrete
```

Nenhum código precisa caminhar o grafo: `recurso.getPet().getResponsavel().getId()` responde
tudo. Consequência prática: a regra é idêntica nos seis serviços clínicos.

### Imposição na camada de serviço, não em anotação

`@PreAuthorize` com SpEL foi descartado por um motivo técnico, não estético: **não resolve
listagem**. `@PostFilter` sobre `Page` filtra o conteúdo mas deixa `totalElements` e o número de
páginas mentindo, quebrando a paginação. Como listagem é a maior superfície da API, isso é
eliminatório.

Filtro global no Hibernate foi descartado por ser máquina pesada, difícil de justificar na
avaliação oral e desconfortável de desengatar para o veterinário.

Fica um componente único, `security/EscopoDoUsuario`, como o único ponto que lê o
`SecurityContext`. Os serviços dependem dele, não do Spring Security — mesma separação que
mantém `ResourceNotFoundException` independente de framework.

### Recurso alheio responde 404, não 403

403 confirmaria que aquele id existe e permitiria enumerar o banco tutor por tutor. É o mesmo
raciocínio já aplicado no login, que responde "credenciais inválidas" sem revelar se o email
existe. O custo é depuração um pouco menos direta.

403 continua sendo a resposta quando o **perfil** não permite a operação (tutor tentando criar
consulta) — isso o `SecurityConfig` já resolve, e ali não há vazamento: a regra é pública.

### O cache precisa conhecer a audiência

Oito serviços anotam `findById` com `@Cacheable(key = "#id")`. `@Cacheable` curto-circuita o
método inteiro: num acerto de cache o corpo não executa, e portanto **a verificação de posse não
rodaria**. O tutor B receberia o recurso que o tutor A carregou minutos antes.

A chave passa a incluir a audiência:

```java
@Cacheable(value = "exames", key = "#id + '-' + @escopoDoUsuario.chaveDeCache()")
```

`chaveDeCache()` devolve `"vet"` para qualquer veterinário — todos enxergam o mesmo conjunto, uma
entrada compartilhada está correta — e `"resp:<id>"` para cada responsável. Numa falha de cache o
método executa e a posse é verificada; num acerto, a chave garante que o valor foi autorizado
para exatamente aquela audiência.

Essa sutileza é justamente o tipo de coisa que se perde numa refatoração futura, então fica
coberta por um teste dedicado de regressão.

## Componentes

```
security/EscopoDoUsuario.java        ehVeterinario(), idDoResponsavel(),
                                     exigirPosse(responsavelId), chaveDeCache()
exception/AcessoNegadoException.java estende RuntimeException, mapeada para 404
```

Cada repositório clínico ganha uma linha (`findByPet_Responsavel_Id`) — o Spring Data resolve
travessia aninhada sem JPQL. `PetRepository`, `LembreteRepository`,
`ResponsavelContatoRepository` e `ResponsavelEnderecoRepository` já têm o método necessário.

## Regras por entidade

| Entidade | VETERINARIO | RESPONSAVEL |
|---|---|---|
| Pet | todos | só os seus (`responsavel_id`) |
| Consulta, Diagnóstico, Exame, Pedido Médico, Vacina | todos | só os de pets seus |
| Leitura Wearable | todos | só as de pets seus |
| Lembrete | todos | só os seus (`responsavel_id`) |
| Responsável (+ contatos, endereços) | lê todos, não edita alheio | só o próprio cadastro |
| Veterinário, Unidade | inalterado | inalterado (leitura) |

Listagem **filtra**, não recusa: `GET /api/pets` como tutor devolve a página dos pets dele.
Recurso único alheio responde 404.

## Testes

Unitários por serviço, com `EscopoDoUsuario` mockado: veterinário enxerga tudo; responsável
enxerga só o próprio; responsável recebe `AcessoNegadoException` no recurso alheio.

Regressão dedicada ao cache: o responsável B não recebe o recurso do responsável A mesmo depois
de A tê-lo carregado.

Integração em `scripts/crud-test.sh`: dois tutores com pets distintos, confirmando que a
listagem de A não contém nada de B e que o acesso direto ao recurso de B responde 404.
