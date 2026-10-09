# FinTrack

O FinTrack é um sistema de controle de finanças pessoais, desenvolvido em Java
como projeto de curso. A primeira versão era via console (POO, Collections e
exceções); a versão atual tem interface gráfica em JavaFX e persistência em
banco de dados SQLite.

## Tecnologias

- Java 21 e Maven
- Programação Orientada a Objetos, Collections e exceções personalizadas
- Generics (`RepositorioGenerico<T>`, curingas `?`, `? extends T`, `? super T`)
- JavaFX 21 com FXML e CSS
- JDBC com SQLite (padrão DAO, `PreparedStatement`, transações)
- JUnit 5

## Funcionalidades

- Cadastro e edição de receitas e despesas (descrição, valor, tipo e data)
- Listagem das transações em tabela, com valores coloridos por tipo
- Remoção com confirmação
- Cálculo do saldo
- Relatório com total de receitas, total de despesas e saldo, com filtro do mês atual
- Dados salvos no arquivo `fintrack.db`, criado na primeira execução

## Como executar

Pelo NetBeans: abra o projeto e use **Run Project**. A classe principal é
`com.mycompany.fintrack.app.Launcher`, que abre a interface gráfica.

Pela linha de comando:

```bash
mvn javafx:run
```

A versão de console continua disponível em `com.mycompany.fintrack.app.Fintrack`
(no NetBeans: botão direito no arquivo → **Run File**).

## Testes

```bash
mvn test
```

Os testes ficam em `src/test/java` e cobrem o modelo, o repositório genérico, as
regras de saldo e o `TransacaoDAO` (com SQLite em memória).

## Estrutura

```
src/main/java/com/mycompany/fintrack/
  app/         Fintrack (console), FinApp (JavaFX), Launcher
  controller/  FinTracker, PrincipalController, TransacaoFormController, RelatorioController
  dao/         Conexao, TransacaoDAO
  exceptions/  EntradaInvalidaException
  model/       Transacao, TransacaoMensal, TipoTransacao
  repository/  RepositorioGenerico
  utils/       Formatador
src/main/resources/com/mycompany/fintrack/view/
  principal.fxml, transacao-form.fxml, relatorio.fxml, estilo.css
```
