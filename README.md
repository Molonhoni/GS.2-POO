# Territory Predictive Insights

**Global Solution 2026.2 — Programação Orientada a Objetos (POO)**  
**Tema:** CitizenTech — tecnologias para necessidades reais de cidades e comunidades  
**Recorte:** monitoramento de chuvas intensas e risco de alagamentos

## 1. Visão geral

O **Territory Predictive Insights** é uma aplicação Java de console que simula o monitoramento de condições ambientais em uma região urbana. A proposta é apoiar a identificação antecipada de situações que podem favorecer alagamentos, utilizando medições de sensores, o histórico armazenado em banco de dados e regras simples de previsão.

O sistema foi desenvolvido como uma solução educacional: os sensores são simulados, e os limiares representam parâmetros de demonstração, não referências oficiais de defesa civil ou previsões meteorológicas validadas.

### Público beneficiado e decisão apoiada

O projeto foi pensado para **equipes de defesa civil, associações de bairro, agentes comunitários e gestores públicos** que precisam acompanhar regiões vulneráveis à chuva intensa. A aplicação pode ajudar a responder a perguntas como: *as medições estão aumentando? Há indícios que justifiquem verificar um ponto de risco?* Com essas informações, pessoas responsáveis podem priorizar inspeções, acompanhar locais críticos e avaliar possíveis medidas preventivas.

O alerta é **um apoio à análise humana**, nunca uma autorização automática para evacuações, bloqueios de vias ou outras decisões de segurança.

## 2. Funcionalidades

- Simulação de leituras de três tipos de sensores, com validação dos valores.
- Armazenamento de cada medição válida em banco H2 persistente.
- Consulta do histórico de medições por sensor.
- Duas estratégias de previsão sobre o histórico: média móvel e comparação entre períodos.
- Classificação de risco nos níveis `NORMAL`, `ATENCAO`, `ALERTA` e `CRITICO`.
- Geração e armazenamento automático de alertas quando a previsão ultrapassa um limiar.
- Consulta aos alertas emitidos, incluindo sensor, previsão, limite e estratégia usada.
- Consulta do estado dos sensores e simulação de indisponibilidade.
- Visualização do status completo da região monitorada.
- Alteração dos limiares com código de acesso definido ao iniciar o sistema.
- **Bônus:** exportação do histórico consultado para CSV, compatível com planilhas.

Não há interface gráfica nem API REST: a interação é realizada pelo terminal, conforme o escopo proposto.

## 3. Tecnologias

| Tecnologia | Utilização |
| --- | --- |
| Java 17 | Linguagem e recursos de orientação a objetos |
| Spring Boot 4.1.2 | Inicialização da aplicação e gerenciamento de componentes |
| Spring Data JPA / Hibernate | Mapeamento das entidades e acesso aos dados |
| H2 Database | Persistência local de medições e alertas |
| Maven / Maven Wrapper | Dependências, compilação e execução de testes |
| JUnit 5 | Testes automatizados |
| Mockito | Simulação de repositórios nos testes de serviço |

## 4. Organização do código

```text
src/
├── main/
│   ├── java/br/com/citizentech/territory/
│   │   ├── dominio/          # Sensores, entidades, limiares e previsões
│   │   ├── excecao/          # Exceções customizadas
│   │   ├── persistencia/     # Repositórios Spring Data JPA
│   │   ├── servico/          # Monitoramento, padrões criacionais e CSV
│   │   ├── SistemaMonitoramento.java
│   │   └── TerritoryPredictiveInsightsFevApplication.java
│   └── resources/
│       └── application.properties
└── test/
    ├── java/br/com/citizentech/territory/
    │   ├── dominio/
    │   ├── persistencia/
    │   └── servico/
    └── resources/
        └── application.properties
```

A aplicação separa as regras de domínio, a persistência, os serviços e a interação com o usuário. Assim, o menu não precisa executar cálculos de previsão ou montar consultas diretamente no banco.

## 5. Conceitos de POO aplicados

**Abstração, herança e encapsulamento.** `ComponenteTerritorial` é uma classe abstrata que reúne características comuns dos componentes monitorados e declara `verificarStatus()`. `SensorSimulado` herda dessa classe e implementa a interface `Sensor`. Os atributos do domínio são protegidos e expostos por métodos apropriados. `DadosTerritorio` restringe a atualização dos limiares por código de acesso.

**Interface e polimorfismo.** `Sensor` define o contrato de leitura, funcionamento, tipo e unidade. As implementações `SensorChuva`, `SensorNivelAgua` e `SensorUmidadeSolo` representam grandezas diferentes. Também há polimorfismo em `EstrategiaPrevisao`: `PrevisaoMediaMovel` e `PrevisaoComparacao` sobrescrevem `prever()` e reutilizam comportamentos da superclasse.

**Exceções customizadas.** `DadosInvalidosException` é utilizada para leituras, limites e informações inconsistentes; `FonteIndisponivelException` identifica a impossibilidade de utilizar um sensor. O menu captura essas situações e informa o problema ao usuário, sem deixar blocos `catch` vazios.

### Estratégias de previsão

| Estratégia | Mínimo de leituras | Regra aplicada |
| --- | ---: | --- |
| Média móvel | 5 | Média das cinco medições mais recentes do sensor |
| Comparação histórica | 10 | Compara as médias das cinco leituras recentes e das cinco anteriores; projeta a diferença observada, sem retornar valor negativo |

A comparação histórica utiliza a expressão `max(0, 2 × média_recente − média_anterior)`. Se o histórico não tiver o número mínimo de leituras, a previsão é recusada com uma mensagem explicativa.

## 6. Padrões criacionais

Foram utilizados dois padrões criacionais:

- **Factory — `FabricaPrevisao`:** centraliza a criação das estratégias de previsão a partir de um identificador. Essa decisão evita espalhar verificações de tipo pelo menu e pelo serviço de monitoramento. O restante da aplicação trabalha com a abstração `EstrategiaPrevisao`.
- **Singleton — `ConfiguracaoSistema`:** mantém uma única instância da configuração durante a execução, permitindo compartilhar o `DadosTerritorio` inicializado entre os componentes. O construtor é privado, e a inicialização do território ocorre uma única vez por execução.

A Factory foi aplicada onde existe escolha entre implementações. O Singleton foi reservado à configuração compartilhada, e **não** substitui a persistência do banco de dados.

## 7. Sensores e limiares de demonstração

A região simulada é a **Zona Norte**. Os sensores são identificados por `S01`, `S02` e `S03`.

| Sensor | Unidade | Faixa de leitura simulada | Atenção a partir de | Alerta a partir de | Crítico a partir de |
| --- | --- | --- | ---: | ---: | ---: |
| Chuva (`S01`) | mm/h | 0 a 120 | 30 | 60 | 90 |
| Nível da água (`S02`) | cm | 0 a 300 | 80 | 150 | 220 |
| Umidade do solo (`S03`) | % | 0 a 100 | 50 | 70 | 90 |

Os intervalos são **didáticos**. Na aplicação real, cada limiar precisaria ser calibrado para o equipamento, a localização e os protocolos das autoridades responsáveis.

O nível `NORMAL` não gera um registro de alerta. Os demais níveis geram um alerta com o valor previsto, o limite atingido, o tipo de sensor e a estratégia empregada.

## 8. Persistência de dados

As entidades JPA `Medicao` e `Alerta` são persistidas, respectivamente, pelos repositórios `MedicaoRepository` e `AlertaRepository`.

- **Aplicação:** H2 em modo arquivo (`jdbc:h2:file:./data/territory_db`). Os dados permanecem disponíveis entre execuções, desde que a pasta `data/` seja preservada.
- **Testes:** H2 em memória, configurado separadamente em `src/test/resources/application.properties`, para não misturar testes com dados locais da aplicação.
- **Criação de tabelas:** o JPA gerencia o esquema com `spring.jpa.hibernate.ddl-auto=update` no ambiente local.

Não é necessário executar um script SQL manualmente. Os repositórios Spring Data JPA também evitam a montagem de SQL por concatenação de strings.

**Observação:** o código de acesso e as alterações nos limiares são mantidos apenas durante a execução atual. Ao reiniciar, o usuário define um novo código e os limites voltam aos valores padrão. O histórico persistente exigido para esta GS é o de **medições e alertas**.

A pasta `data/` fica fora do Git por meio do `.gitignore`, assim como `target/`, gerada pelo Maven.

## 9. Como executar

### Pré-requisitos

- JDK 17 ou superior compatível com o projeto.
- Terminal PowerShell, Prompt de Comando ou equivalente.
- Não é necessário instalar um servidor de banco de dados separado: o H2 é iniciado com a aplicação.

### Obter o projeto

Para quem ainda não tem uma cópia local:

```bash
git clone https://github.com/Molonhoni/territory-predictive-insights-fev.git
cd territory-predictive-insights-fev
```

Se o projeto já estiver salvo localmente, basta abrir a pasta que contém `pom.xml`.

### Compilar e executar os testes

**Windows (PowerShell):**

```powershell
.\mvnw.cmd clean package
```

**Linux / macOS:**

```bash
./mvnw clean package
```

O Maven baixa as dependências, compila o projeto, executa os testes e gera o arquivo JAR na pasta `target/`.

### Iniciar a aplicação

```powershell
java -jar .\target\territory-predictive-insights-fev-0.0.1-SNAPSHOT.jar
```

Na primeira tela, defina um código de acesso com pelo menos quatro caracteres. Esse código será solicitado somente para a alteração dos limiares da execução atual. **Não utilize senhas pessoais ou reais.**

### Menu disponível

| Opção | Ação |
| --- | --- |
| `1` | Verificar sensores |
| `2` | Coletar medições |
| `3` | Executar previsões |
| `4` | Consultar histórico de um sensor |
| `5` | Consultar alertas |
| `6` | Gerenciar os limiares do território |
| `7` | Visualizar status completo |
| `8` | Ativar ou desativar um sensor simulado |
| `9` | Exportar histórico em CSV |
| `0` | Encerrar |

**Roteiro de demonstração:** use a opção `2`, selecione todos os sensores (`0`) e colete dez leituras por sensor. Depois execute as duas estratégias na opção `3`, consulte as medições na opção `4` e os alertas na opção `5`. Encerre e inicie novamente a aplicação para conferir se o histórico permanece disponível. Os valores mudam entre coletas porque são simulados.

Se nenhuma previsão ultrapassar os limiares, não haverá alerta novo; isso não significa necessariamente falha. Os limites podem ser ajustados pela opção `6` para demonstrar diferentes níveis de risco.

## 10. Exportação CSV — bônus

A opção `9` recupera do banco o histórico do sensor selecionado e gera um arquivo em:

```text
data/exportacoes/historico_S01_YYYYMMDD_HHMMSS_SSS.csv
```

O CSV é gravado em UTF-8, com `;` como separador e colunas como identificador, sensor, região, valor, unidade e horário. O exportador trata aspas e caracteres especiais e não sobrescreve silenciosamente um arquivo existente. Os arquivos de exportação permanecem locais e não são versionados no GitHub.

## 11. Testes e validações

A aplicação conta com **46 testes automatizados aprovados** na validação local realizada durante o desenvolvimento, sem falhas ou erros na última execução informada.

Os testes utilizam o padrão **AAA (Arrange, Act, Assert)** e cobrem, entre outros pontos:

- Criação de sensores, validação de medições e exceções customizadas.
- Alteração autorizada e validação dos limiares.
- Cálculos das duas estratégias de previsão e históricos insuficientes.
- Criação de estratégias pela Factory e comportamento do Singleton.
- Persistência e consulta de medições e alertas com JPA.
- Lógica de alertas com repositórios simulados pelo Mockito, sem acesso ao banco.
- Recusa de sensores indisponíveis, regiões incompatíveis e estratégias inexistentes.
- Geração do CSV, tratamento de caracteres especiais e proteção contra sobrescrita.

Para verificar novamente:

```powershell
.\mvnw.cmd clean test
```

O resultado esperado é `BUILD SUCCESS`, com zero falhas e zero erros. A contagem de testes poderá aumentar se novas verificações forem incluídas.

## 12. Decisões técnicas e limitações

Optamos por um **menu de console**, porque o objetivo principal da disciplina é avaliar POO, persistência, validação, padrões criacionais e testes — e não desenvolver uma interface web. O Spring Data JPA foi escolhido para facilitar a persistência sem exigir SQL escrito manualmente; o H2 em arquivo permite executar a aplicação localmente sem infraestrutura externa. As estratégias de previsão foram mantidas simples e explicáveis, respeitando o foco da atividade.

A solução não integra sensores físicos, dados oficiais de chuva ou serviços externos. A configuração de limiares não é persistida e o código de acesso em memória tem finalidade apenas didática. Para uso real, também seriam necessários controle de usuários, calibragem de sensores, identificação de falhas e integração a protocolos de resposta.

## 13. Reflexão crítica

**Quando o sistema deve se calar?** Uma previsão baseada em poucos dados pode transmitir uma segurança que não existe. Por isso, a média móvel exige ao menos cinco medições, e a comparação histórica exige dez. Quando não há histórico suficiente, o sistema não inventa um resultado: informa que a previsão não pode ser realizada. Mesmo com essas quantidades, um conjunto de leituras concentradas em um intervalo muito curto pode não representar a evolução de uma chuva ao longo de horas. Em uma aplicação real, também seria necessário verificar a idade das medições, a frequência das coletas, a calibração dos sensores e possíveis falhas de comunicação.

**O que os dados não representam?** Nossos sensores são simulados e associados a uma região genérica. Uma medição em um ponto não descreve necessariamente todas as ruas, desníveis, bueiros, galerias pluviais ou áreas de risco da Zona Norte. Também não avaliamos saturação da rede de drenagem, previsão meteorológica oficial, ocupação do solo nem a situação individual das pessoas afetadas. Os limiares foram definidos para demonstrar regras de negócio, não para estabelecer limites confiáveis de segurança pública. Por isso, tanto falsos positivos quanto falsos negativos são possíveis.

**Por que o alerta apoia a decisão, em vez de decidir pela pessoa?** O papel do software é organizar evidências: registrar o histórico, mostrar tendências, indicar o sensor associado e explicar qual limite foi ultrapassado. A decisão sobre inspeção, comunicação de risco ou intervenção precisa considerar observação em campo, orientações oficiais e responsabilidade humana. O alerta pode ajudar a priorizar a atenção, mas não deve produzir, sozinho, ações que afetem a segurança da comunidade. Essa distinção é essencial para que o sistema seja útil sem sugerir uma precisão que o modelo não tem.

## 14. Uso de inteligência artificial

Ferramentas de IA generativa foram utilizadas **ao longo do desenvolvimento** para apoiar a elaboração de propostas de código, a compreensão de conceitos, a investigação de erros e a revisão dos testes. Três etapas em que esse apoio foi especialmente relevante foram:

| Commit | Apoio recebido | Como o resultado foi verificado |
| --- | --- | --- |
| **2 — Persistência com JPA e H2** | Exemplos de mapeamento das entidades, organização dos repositórios e configuração de um H2 persistente; apoio no diagnóstico de erros relacionados à estrutura dos pacotes. | Conferência dos diretórios Java, compilação com Maven e testes de gravação e consulta em repositórios JPA. |
| **3 — Estratégias de previsão** | Discussão da separação entre média móvel e comparação histórica e suporte para investigar falhas causadas pela ordenação temporal das medições. | Ajuste dos horários utilizados nos testes e novas execuções até obter os resultados esperados para crescimento, queda e histórico insuficiente. |
| **5 — Serviço de monitoramento e alertas** | Sugestões de estrutura para integrar sensores, repositórios, estratégias e limiares, além de exemplos de testes com Mockito. | Execução dos testes de serviço com repositórios simulados, conferência dos níveis de risco e verificação de que cenários normais não gravam alertas. |

A IA não foi tratada como fonte de dados ambientais reais nem como substituta da validação do programa. As sugestões foram incorporadas ao projeto e verificadas no ambiente de desenvolvimento por meio de testes, mensagens de compilação e execução das funcionalidades. Os integrantes devem estar preparados para explicar o código entregue.

## 15. Integrantes

| Integrante | RM |
| --- | --- |
| Felipe Paula Burba Molonhoni | 564395 |
| Vitor Limeira dos Santos | 565280 |

## 16. Repositório

[GitHub — Territory Predictive Insights](https://github.com/Molonhoni/territory-predictive-insights-fev)

**Observação de privacidade:** os dados monitorados representam região, equipamento e medições simuladas. O sistema não coleta nomes, CPF ou endereços de moradores.
