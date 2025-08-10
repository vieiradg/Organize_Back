# Documentação do Backend: organize-backend

Este documento detalha a arquitetura, tecnologias, dependências e instruções para rodar o projeto `organize-backend`.

## 1. Visão Geral

O `organize-backend` é a API RESTful da aplicação Organize, desenvolvida em Java com Spring Boot. Ele é responsável por gerenciar a lógica de negócios, persistência de dados e autenticação de usuários para o frontend.

## 2. Tecnologias e Dependências Principais

O projeto utiliza as seguintes tecnologias e bibliotecas:

*   **Java 17**: Linguagem de programação.
*   **Spring Boot 3**: Framework para construção de aplicações Java robustas e escaláveis.
*   **Spring Security**: Para autenticação e autorização baseada em JWT.
*   **JPA (Hibernate)**: Para mapeamento objeto-relacional e persistência de dados com bancos de dados relacionais.
*   **PostgreSQL**: Banco de dados relacional utilizado para armazenar os dados da aplicação.
*   **Maven**: Ferramenta para gerenciamento de dependências e build do projeto.
*   **JWT (JSON Web Token)**: Para gerar e validar tokens de autenticação, garantindo a segurança das requisições.
*   **Springdoc (Swagger UI)**: Para geração automática e interativa da documentação da API.
*   **Lombok**: Biblioteca que reduz o código boilerplate em classes Java.
*   **Flyway**: Ferramenta de migração de banco de dados para gerenciar o esquema de forma versionada.

As dependências completas podem ser encontradas no arquivo `pom.xml`.

## 3. Estrutura de Pastas

A estrutura do projeto segue o padrão Maven para aplicações Spring Boot:

```
.
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── organize/
│   │   │           ├── config/      # Configurações gerais do Spring e da aplicação
│   │   │           ├── controller/  # Controladores REST que definem os endpoints da API
│   │   │           ├── dto/         # Data Transfer Objects (DTOs) para entrada e saída de dados
│   │   │           ├── model/       # Modelos de dados (entidades JPA) que mapeiam as tabelas do banco
│   │   │           ├── repository/  # Interfaces de repositório para acesso aos dados (Spring Data JPA)
│   │   │           ├── security/    # Classes relacionadas à segurança (JWT, filtros, configurações)
│   │   │           └── service/     # Camada de serviço com a lógica de negócios da aplicação
│   │   └── resources/
│   │       ├── application.properties # Arquivo de configuração da aplicação (conexão com DB, etc.)
│   │       └── db/
│   │           └── migration/       # Scripts de migração do Flyway
│   └── test/           # Código-fonte dos testes unitários e de integração
├── target/             # Diretório de saída da build (contém o JAR executável)
├── .gitignore          # Arquivos e pastas a serem ignorados pelo Git
├── docker-compose.yml  # Configuração para orquestração de containers Docker (DB e Backend)
├── Dockerfile          # Definição da imagem Docker para o backend
├── pom.xml             # Arquivo de configuração do Maven (dependências, plugins, etc.)
└── README.md           # Informações gerais do projeto
```

## 4. Camada de Segurança (JWT)

A segurança da API é implementada utilizando JSON Web Tokens (JWT) e Spring Security. Os principais componentes são:

*   **`SecurityConfigurations.java`**: Define as regras de segurança, como quais endpoints são públicos e quais exigem autenticação.
*   **`SecurityFilter.java`**: Um filtro que intercepta as requisições HTTP, valida o token JWT presente no cabeçalho `Authorization` e autentica o usuário no contexto do Spring Security.
*   **`TokenService.java`**: Responsável por gerar e validar os tokens JWT, incluindo a assinatura com uma chave secreta.

**Fluxo de Autenticação:**

1.  O cliente envia credenciais (usuário/senha) para o endpoint de login (`POST /login`).
2.  O backend valida as credenciais e, se corretas, gera um JWT e o retorna ao cliente.
3.  Para acessar rotas protegidas, o cliente deve incluir o JWT no cabeçalho `Authorization` (ex: `Bearer <token>`).
4.  O `SecurityFilter` intercepta a requisição, valida o token e permite ou nega o acesso.

## 5. Banco de Dados (PostgreSQL e Flyway)

O projeto utiliza PostgreSQL como banco de dados. A configuração da conexão é definida no arquivo `src/main/resources/application.properties`.

As entidades do banco de dados são mapeadas pelas classes na pasta `src/main/java/com/organize/model`, anotadas com `@Entity`.

### Gerenciamento de Esquema com Flyway

Para um gerenciamento robusto e versionado do esquema do banco de dados, utilizamos o **Flyway**. Ele garante que as alterações no banco de dados sejam aplicadas de forma controlada e consistente.

**Como funciona:**

1.  **Scripts de Migração**: As alterações no esquema do banco de dados são definidas em arquivos SQL localizados em `src/main/resources/db/migration/`. O nome dos arquivos segue o padrão `V<VERSION>__<DESCRIPTION>.sql` (ex: `V1__create_initial_schema.sql`).
2.  **Execução Automática**: Ao iniciar a aplicação Spring Boot, o Flyway verifica o estado atual do banco de dados e executa quaisquer scripts de migração que ainda não foram aplicados, garantindo que o esquema esteja sempre atualizado.

**Para adicionar uma nova alteração no banco de dados (ex: nova tabela, nova coluna):**

1.  Crie um novo arquivo SQL em `src/main/resources/db/migration/` com um número de versão sequencial (ex: `V2__add_new_feature_table.sql`).
2.  Escreva o SQL necessário para a alteração dentro deste arquivo.
3.  Inicie a aplicação. O Flyway detectará e aplicará a nova migração automaticamente.

### Como Criar uma Nova Entidade

Para adicionar uma nova tabela ao banco de dados (e sua respectiva entidade Java):

1.  Crie uma classe Java em `src/main/java/com/organize/model/` e anote-a com `@Entity` e `@Table` (opcional, para nomear a tabela).
2.  Defina os atributos da classe, mapeando-os para as colunas da tabela. Use `@Id` para a chave primária e `@GeneratedValue` para geração automática.
3.  Crie uma interface de repositório em `src/main/java/com/organize/repository/` que estenda `JpaRepository<SuaEntidade, TipoDoId>`. O Spring Data JPA fornecerá automaticamente métodos CRUD básicos.
4.  **Crie um script de migração Flyway** (conforme descrito acima) para criar a nova tabela no banco de dados.

## 6. Rotas da API

Os endpoints da API são definidos nos controladores em `src/main/java/com/organize/controller`. Exemplos de rotas:

*   `POST /login`: Autentica um usuário e retorna um JWT.
*   `POST /users`: Cria um novo usuário.
*   `GET /tasks`: Retorna a lista de tarefas do usuário autenticado.
*   `POST /tasks`: Cria uma nova tarefa.
*   `PUT /tasks/{id}`: Atualiza uma tarefa existente.
*   `DELETE /tasks/{id}`: Exclui uma tarefa.

## 7. Documentação da API (Swagger)

A documentação interativa da API é gerada automaticamente pelo Springdoc e pode ser acessada em `http://localhost:8080/swagger-ui.html` quando o backend estiver em execução.

## 8. Como Rodar o Projeto

Existem duas formas principais de rodar o `organize-backend`:

### Opção 1: Usando Docker Compose (Recomendado)

Esta é a forma mais fácil, pois configura o banco de dados PostgreSQL e o backend em containers Docker, e o Flyway cuidará das migrações do banco de dados automaticamente.

### Pré-requisitos

*   Docker Desktop (ou Docker Engine e Docker Compose instalados)

### Passos

1.  **Clone o repositório** (se ainda não o fez):
    ```bash
    git clone <URL_DO_REPOSITORIO_BACKEND>
    cd organize-backend
    ```
2.  **Construa e inicie os containers**:
    ```bash
    docker-compose up --build
    ```
    Este comando irá:
    *   Construir a imagem Docker do backend (baseada no `Dockerfile`).
    *   Criar e iniciar o container do PostgreSQL (`organize-db`).
    *   Criar e iniciar o container do backend (`organize-backend-app`).
    *   **O Flyway executará automaticamente as migrações do banco de dados na inicialização do backend.**

    Você pode adicionar `-d` para rodar em segundo plano (`docker-compose up --build -d`).

3.  **Verifique o status dos containers**:
    ```bash
    docker-compose ps
    ```

4.  **Acesse a API**: O backend estará disponível em `http://localhost:8080` e a documentação Swagger em `http://localhost:8080/swagger-ui.html`.

5.  **Para parar os containers**:
    ```bash
    docker-compose down
    ```

### Opção 2: Rodando Localmente (sem Docker para o Backend)

Esta opção requer que você tenha o Java e o Maven instalados, e um banco de dados PostgreSQL rodando separadamente.

### Pré-requisitos

*   Java Development Kit (JDK) 17 ou superior.
*   Apache Maven.
*   Servidor PostgreSQL rodando localmente (ou acessível).

### Passos

1.  **Clone o repositório** (se ainda não o fez):
    ```bash
    git clone <URL_DO_REPOSITORIO_BACKEND>
    cd organize-backend
    ```
2.  **Configure o banco de dados PostgreSQL**:
    *   Crie um banco de dados chamado `organize`.
    *   Crie um usuário `postgres` com a senha `organize2025` (ou ajuste as configurações no `application.properties` para suas credenciais).
    *   Certifique-se de que o PostgreSQL esteja acessível na porta 5432.

3.  **Compile o projeto Maven**:
    ```bash
    mvn clean install
    ```
    Isso irá baixar as dependências e compilar o projeto, gerando o arquivo JAR em `target/`.

4.  **Execute a aplicação Spring Boot**:
    ```bash
    mvn spring-boot:run
    ```
    **O Flyway executará automaticamente as migrações do banco de dados na inicialização da aplicação.**

5.  **Acesse a API**: O backend estará disponível em `http://localhost:8080` e a documentação Swagger em `http://localhost:8080/swagger-ui.html`.

## 9. Como Contribuir

Ficamos felizes com sua contribuição! Para contribuir com o projeto `organize-backend`, siga os passos abaixo:

1.  **Faça um Fork do Repositório**:
    Acesse o repositório original no GitHub e clique no botão "Fork" no canto superior direito. Isso criará uma cópia do repositório em sua conta.

2.  **Clone o seu Fork**:
    ```bash
    git clone https://github.com/SEU_USUARIO/organize-backend.git
    cd organize-backend
    ```

3.  **Crie uma Branch para sua Feature/Correção**:
    É uma boa prática criar uma nova branch para cada alteração que você for fazer. Use um nome descritivo:
    ```bash
    git checkout -b feature/minha-nova-funcionalidade
    # ou
    git checkout -b bugfix/correcao-de-erro
    ```

4.  **Faça suas Alterações**:
    Implemente suas mudanças, adicione novos recursos ou corrija bugs. Certifique-se de seguir as convenções de código existentes no projeto.
    *   **Para alterações no esquema do banco de dados**, crie um novo script de migração Flyway em `src/main/resources/db/migration/` com o próximo número de versão.

5.  **Teste suas Alterações**:
    Antes de enviar suas alterações, execute os testes existentes e, se aplicável, crie novos testes para cobrir seu código.
    ```bash
    mvn test
    ```

6.  **Commit suas Alterações**:
    Escreva mensagens de commit claras e concisas, descrevendo o que foi alterado. Use o padrão de Conventional Commits se o projeto o seguir (ex: `feat: adiciona nova funcionalidade`, `fix: corrige bug de login`).
    ```bash
    git add .
    git commit -m "feat: adiciona nova funcionalidade de tarefas"
    ```

7.  **Envie suas Alterações para o seu Fork**:
    ```bash
    git push origin feature/minha-nova-funcionalidade
    ```

8.  **Abra um Pull Request (PR)**:
    Vá para o seu repositório no GitHub, você verá um botão para criar um Pull Request. Preencha o template do PR (se houver) e descreva suas alterações em detalhes. Um dos mantenedores do projeto revisará seu código e poderá solicitar alterações antes de mesclar.

---