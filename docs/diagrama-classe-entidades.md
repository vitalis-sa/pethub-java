# Diagrama de Classe das Entidades

Este arquivo foi montado para voce copiar e colar o conteudo no seu editor de diagrama UML no estilo da imagem de referencia.

Use cada bloco abaixo como o conteudo da caixa da classe.
Quando uma entidade nao tiver metodo de dominio explicito, voce pode deixar a area de metodos vazia no desenho.

## Legenda de Setas

Use estes tipos de ligacao no diagrama:

- Associacao simples: linha continua
- Composicao: linha continua com losango preenchido na entidade dona
- Agregacao: linha continua com losango vazio na entidade dona
- Heranca: linha continua com triangulo vazio
- Dependencia: linha tracejada com seta aberta

Neste dominio, o recomendado e:

- `Responsavel -> ResponsavelEndereco`: composicao
- `Responsavel -> ResponsavelContato`: composicao
- Todas as outras ligacoes entre entidades: associacao simples

## Classes

### Responsavel

```text
---------------------------------
| Responsavel                   |
---------------------------------
| + id: Long                    |
| + nome: String                |
| + cpf: String                 |
| + email: String               |
| + senha: String               |
| + ativo: Boolean              |
| + createdAt: LocalDateTime    |
---------------------------------
|                               |
---------------------------------
```

### ResponsavelEndereco

```text
---------------------------------
| ResponsavelEndereco           |
---------------------------------
| + id: Long                    |
| + logradouro: String          |
| + numero: String              |
| + complemento: String         |
| + bairro: String              |
| + cidade: String              |
| + estado: String              |
| + cep: String                 |
| + principal: Boolean          |
---------------------------------
|                               |
---------------------------------
```

### ResponsavelContato

```text
---------------------------------
| ResponsavelContato            |
---------------------------------
| + id: Long                    |
| + tipo: String                |
| + valor: String               |
| + principal: Boolean          |
---------------------------------
|                               |
---------------------------------
```

### Pet

```text
---------------------------------
| Pet                           |
---------------------------------
| + id: Long                    |
| + nome: String                |
| + especie: String             |
| + raca: String                |
| + idade: Integer              |
| + peso: Double                |
| + genero: String              |
---------------------------------
|                               |
---------------------------------
```

### Veterinario

```text
---------------------------------
| Veterinario                   |
---------------------------------
| + id: Long                    |
| + nome: String                |
| + crmv: String                |
| + especialidade: String       |
| + email: String               |
| + telefone: String            |
| + senha: String               |
| + ativo: Boolean              |
---------------------------------
|                               |
---------------------------------
```

### UnidadeVeterinario

```text
---------------------------------
| UnidadeVeterinario            |
---------------------------------
| + id: Long                    |
| + nome: String                |
| + logradouro: String          |
| + numero: String              |
| + bairro: String              |
| + cidade: String              |
| + estado: String              |
| + cep: String                 |
---------------------------------
|                               |
---------------------------------
```

### Consulta

```text
---------------------------------
| Consulta                      |
---------------------------------
| + id: Long                    |
| + dataHora: LocalDateTime     |
| + tipo: TipoConsulta          |
| + observacoes: String         |
| + status: StatusConsulta      |
---------------------------------
|                               |
---------------------------------
```

### Exame

```text
---------------------------------
| Exame                         |
---------------------------------
| + id: Long                    |
| + tipo: String                |
| + data: LocalDate             |
| + resultado: String           |
| + arquivoResultado: String    |
---------------------------------
|                               |
---------------------------------
```

### Diagnostico

```text
---------------------------------
| Diagnostico                   |
---------------------------------
| + id: Long                    |
| + data: LocalDateTime         |
| + sintoma1: String            |
| + sintoma2: String            |
| + sintoma3: String            |
| + sintoma4: String            |
| + duracaoSintomas: String     |
| + perdaApetite: Boolean       |
| + vomito: Boolean             |
| + diarreia: Boolean           |
| + tosse: Boolean              |
| + dificuldadeRespiratoria: Boolean |
| + claudicacao: Boolean        |
| + lesoesPele: Boolean         |
| + secrecaoNasal: Boolean      |
| + secrecaoOcular: Boolean     |
| + temperaturaCorporal: Double |
| + frequenciaCardiaca: Integer |
| + doencaPredita: String       |
| + confiancaPredicao: Double   |
| + analiseGenAI: String        |
---------------------------------
|                               |
---------------------------------
```

### PedidoMedico

```text
---------------------------------
| PedidoMedico                  |
---------------------------------
| + id: Long                    |
| + tipo: TipoPedidoMedico      |
| + descricao: String           |
| + instrucoes: String          |
| + dataLimite: LocalDate       |
| + status: StatusPedidoMedico  |
| + createdAt: LocalDateTime    |
---------------------------------
| + onCreate(): void            |
---------------------------------
```

### VacinaTratamento

```text
---------------------------------
| VacinaTratamento              |
---------------------------------
| + id: Long                    |
| + tipo: TipoVacinaTratamento  |
| + nome: String                |
| + dataAplicacao: LocalDate    |
| + proximaDose: LocalDate      |
| + dose: String                |
| + observacoes: String         |
---------------------------------
|                               |
---------------------------------
```

### LeituraWearable

```text
---------------------------------
| LeituraWearable               |
---------------------------------
| + id: Long                    |
| + timestamp: LocalDateTime    |
| + temperaturaCorporal: Double |
| + frequenciaCardiaca: Integer |
| + anomaliaDetectada: Boolean  |
| + tipoAnomalia: String        |
---------------------------------
|                               |
---------------------------------
```

### Lembrete

```text
---------------------------------
| Lembrete                      |
---------------------------------
| + id: Long                    |
| + tipo: TipoLembrete          |
| + dataAgendada: LocalDate     |
| + mensagem: String            |
| + status: StatusLembrete      |
| + referenciaId: Long          |
| + referenciaTipo: String      |
| + createdAt: LocalDateTime    |
---------------------------------
|                               |
---------------------------------
```

## Ligacoes Entre Entidades

Use esta secao para desenhar as setas exatamente no seu diagrama.

### 1. Responsavel e ResponsavelEndereco

- Ligacao: `Responsavel 1` para `0..* ResponsavelEndereco`
- Seta: composicao
- Como desenhar: losango preenchido no lado de `Responsavel`
- Leitura: um responsavel possui varios enderecos, e o endereco depende do responsavel

### 2. Responsavel e ResponsavelContato

- Ligacao: `Responsavel 1` para `0..* ResponsavelContato`
- Seta: composicao
- Como desenhar: losango preenchido no lado de `Responsavel`
- Leitura: um responsavel possui varios contatos, e o contato depende do responsavel

### 3. Responsavel e Pet

- Ligacao: `Responsavel 1` para `0..* Pet`
- Seta: associacao simples
- Como desenhar: linha continua normal
- Leitura: um responsavel pode ter varios pets; cada pet pertence a um responsavel

### 4. Veterinario e Pet

- Ligacao: `Veterinario 1` para `0..* Pet`
- Seta: associacao simples
- Como desenhar: linha continua normal
- Observacao: no lado de `Pet`, o relacionamento e opcional (`0..1` veterinarioResponsavel)

### 5. Veterinario e UnidadeVeterinario

- Ligacao: `Veterinario 1` para `0..* UnidadeVeterinario`
- Seta: associacao simples
- Como desenhar: linha continua normal
- Leitura: um veterinario pode estar vinculado a varias unidades

### 6. Pet e Consulta

- Ligacao: `Pet 1` para `0..* Consulta`
- Seta: associacao simples
- Como desenhar: linha continua normal

### 7. Veterinario e Consulta

- Ligacao: `Veterinario 1` para `0..* Consulta`
- Seta: associacao simples
- Como desenhar: linha continua normal

### 8. UnidadeVeterinario e Consulta

- Ligacao: `UnidadeVeterinario 1` para `0..* Consulta`
- Seta: associacao simples
- Como desenhar: linha continua normal
- Observacao: no lado de `Consulta`, a unidade e opcional (`0..1`)

### 9. Consulta e Exame

- Ligacao: `Consulta 1` para `0..* Exame`
- Seta: associacao simples
- Como desenhar: linha continua normal

### 10. Pet e Exame

- Ligacao: `Pet 1` para `0..* Exame`
- Seta: associacao simples
- Como desenhar: linha continua normal

### 11. Consulta e Diagnostico

- Ligacao: `Consulta 1` para `0..* Diagnostico`
- Seta: associacao simples
- Como desenhar: linha continua normal

### 12. Pet e Diagnostico

- Ligacao: `Pet 1` para `0..* Diagnostico`
- Seta: associacao simples
- Como desenhar: linha continua normal

### 13. Consulta e PedidoMedico

- Ligacao: `Consulta 1` para `0..* PedidoMedico`
- Seta: associacao simples
- Como desenhar: linha continua normal

### 14. Pet e PedidoMedico

- Ligacao: `Pet 1` para `0..* PedidoMedico`
- Seta: associacao simples
- Como desenhar: linha continua normal

### 15. Pet e VacinaTratamento

- Ligacao: `Pet 1` para `0..* VacinaTratamento`
- Seta: associacao simples
- Como desenhar: linha continua normal

### 16. Veterinario e VacinaTratamento

- Ligacao: `Veterinario 1` para `0..* VacinaTratamento`
- Seta: associacao simples
- Como desenhar: linha continua normal

### 17. Consulta e VacinaTratamento

- Ligacao: `Consulta 1` para `0..* VacinaTratamento`
- Seta: associacao simples
- Como desenhar: linha continua normal
- Observacao: no lado de `VacinaTratamento`, a consulta e opcional (`0..1`)

### 18. Pet e LeituraWearable

- Ligacao: `Pet 1` para `0..* LeituraWearable`
- Seta: associacao simples
- Como desenhar: linha continua normal

### 19. Responsavel e Lembrete

- Ligacao: `Responsavel 1` para `0..* Lembrete`
- Seta: associacao simples
- Como desenhar: linha continua normal

### 20. Pet e Lembrete

- Ligacao: `Pet 1` para `0..* Lembrete`
- Seta: associacao simples
- Como desenhar: linha continua normal

## Layout Sugerido

Se quiser deixar parecido com a imagem, distribua as classes assim:

- Linha superior: `Responsavel`, `Pet`, `Veterinario`, `UnidadeVeterinario`
- Linha central: `Consulta`
- Linha inferior: `ResponsavelEndereco`, `ResponsavelContato`, `Exame`, `Diagnostico`, `PedidoMedico`, `VacinaTratamento`, `LeituraWearable`, `Lembrete`

Com isso, `Pet` e `Consulta` ficam como entidades centrais do desenho.

## Observacao Importante

O projeto atual nao usa heranca entre entidades. Por isso, no diagrama de entidades deste sistema, voce nao precisa usar seta de generalizacao com triangulo.