# Organize Backend

## Descrição

Este é o backend da aplicação Organize, um sistema de gerenciamento de tarefas. Ele fornece uma API RESTful para o frontend consumir, permitindo que os usuários criem, leiam, atualizem e excluam tarefas.

## Tecnologias e Dependências

- **Java 17**
- **Spring Boot 3**
- **Spring Security**: Para autenticação e autorização.
- **JPA (Hibernate)**: Para mapeamento objeto-relacional e persistência de dados.
- **PostgreSQL**: O banco de dados relacional utilizado.
- **Maven**: Para gerenciamento de dependências e build do projeto.
- **JWT (JSON Web Token)**: Para gerar e validar tokens de autenticação.
- **Springdoc (Swagger UI)**: Para documentação da API.
- **Flyway**: Para gerenciamento de migrações de banco de dados.

## Anotações Importantes do Spring Boot

Anotações mais comuns do Spring Boot usadas neste projeto:

- **`@RestController`**: Marca uma classe como um controlador, onde cada método retorna um objeto de domínio em vez de uma visão.
- **`@RequestMapping`**: Mapeia as requisições web para os métodos dos controladores.
- **`@Autowired`**: Injeta as dependências automaticamente.
- **`@Service`**: Marca uma classe como um serviço, que contém a lógica de negócios.
- **`@Repository`**: Marca uma classe como um repositório, que lida com o acesso aos dados.
- **`@Entity`**: Especifica que uma classe é uma entidade e está mapeada para uma tabela no banco de dados.
- **`@Id`**: Especifica a chave primária de uma entidade.
- **`@GeneratedValue`**: Configura a forma como a chave primária é gerada.
- **`@Configuration`**: Marca uma classe como uma fonte de definições de beans.
- **`@Bean`**: Indica que um método produz um bean a ser gerenciado pelo contêiner Spring.

## Estrutura de Pastas

A estrutura de pastas do projeto segue o padrão Maven:

```
.
├── .idea/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── organize/
│   │   │           ├── config/      # Configurações do Spring
│   │   │           ├── controller/  # Controladores REST
│   │   │           ├── dto/         # Data Transfer Objects
│   │   │           ├── model/       # Modelos de dados (entidades)
│   │   │           ├── repository/  # Repositórios (acesso ao banco de dados)
│   │   │           ├── security/    # Configurações de segurança
│   │   │           └── service/     # Lógica de negócios
│   │   └── resources/
│   │       ├── application.properties # Configurações da aplicação
│   │       └── db/
│   │           └── migration/       # Scripts de migração do Flyway
│   └── test/
├── target/
├── .gitignore
├── docker-compose.yml
├── Dockerfile
└── pom.xml
```

- **`src/main/java`**: Contém o código-fonte da aplicação.
    - **`config`**: Classes de configuração do Spring.
    - **`controller`**: Controladores que definem os endpoints da API REST.
    - **`dto`**: Objetos de Transferência de Dados, usados para transferir dados entre o cliente e o servidor.
    - **`model`**: As entidades do banco de dados.
    - **`repository`**: Interfaces que definem as operações de acesso ao banco de dados.
    - **`security`**: Configurações de segurança, como autenticação e autorização.
    - **`service`**: Onde a lógica de negócios da aplicação é implementada.
- **`src/main/resources`**: Contém arquivos de configuração, como o `application.properties`, e os scripts de migração do Flyway.
- **`pom.xml`**: O arquivo de configuração do projeto Maven, que define as dependências e como o projeto é construído.

## Camada de Segurança

A camada de segurança é responsável por proteger a API contra acesso não autorizado. Ela é composta pelos seguintes arquivos:

- **`SecurityConfigurations.java`**: A classe principal de configuração do Spring Security. Ela define as regras de segurança, como quais endpoints são públicos e quais são protegidos.
- **`SecurityFilter.java`**: Um filtro que intercepta todas as requisições para a API. Ele verifica se a requisição contém um token JWT válido e, em caso afirmativo, autentica o usuário.
- **`TokenService.java`**: Um serviço que gera e valida os tokens JWT.

## Banco de Dados

O projeto utiliza o PostgreSQL como banco de dados, com o **Flyway** para gerenciar as migrações de esquema. A configuração da conexão com o banco de dados é feita no arquivo `src/main/resources/application.properties`.

As entidades do banco de dados são definidas na pasta `src/main/java/com/organize/model`. Cada classe nessa pasta representa uma tabela no banco de dados.

### Como Criar uma Nova Entidade

Para criar uma nova entidade no banco de dados, siga os seguintes passos:

1.  **Crie a classe da entidade** na pasta `src/main/java/com/organize/model`.
2.  **Anote a classe com `@Entity`** para que o JPA saiba que ela é uma entidade.
3.  **Adicione os campos da entidade** e anote a chave primária com `@Id` e `@GeneratedValue`.
4.  **Crie um repositório** para a entidade na pasta `src/main/java/com/organize/repository`. O repositório deve estender `JpaRepository`.
5.  **Crie um script de migração Flyway** em `src/main/resources/db/migration/` para criar a nova tabela no banco de dados. O nome do arquivo deve seguir o padrão `V<VERSION>__<DESCRIPTION>.sql`.

## Autenticação

A autenticação é feita usando JWT (JSON Web Token). O fluxo de autenticação é o seguinte:

1.  O usuário envia suas credenciais (email e senha) para o endpoint `/login`.
2.  O backend verifica as credenciais.
3.  Se as credenciais estiverem corretas, o backend gera um token JWT e o retorna para o cliente.
4.  O cliente deve enviar o token JWT no cabeçalho `Authorization` de cada requisição para acessar os endpoints protegidos.

O token JWT contém informações sobre o usuário, como seu ID e suas permissões.

## Rotas

As rotas da API são definidas nos controladores, na pasta `src/main/java/com/organize/controller`. As principais rotas são:

- `POST /login`: Autentica um usuário e retorna um token JWT.
- `POST /users`: Cria um novo usuário.
- `GET /tasks`: Retorna a lista de tarefas do usuário autenticado.
- `POST /tasks`: Cria uma nova tarefa.
- `PUT /tasks/{id}`: Atualiza uma tarefa existente.
- `DELETE /tasks/{id}`: Exclui uma tarefa.

## Swagger

A documentação da API é gerada automaticamente usando o Springdoc e pode ser acessada em `http://localhost:8080/swagger-ui.html`.

## Como Configurar o Ambiente de Desenvolvimento

1.  **Clone o repositório:**
    ```bash
    git clone https://github.com/vieiradg/Organize_Back
    ```
2.  **Instale as dependências:**
    O Maven cuidará da instalação das dependências automaticamente.
3.  **Configure o banco de dados:**
    - Crie um banco de dados para a aplicação (ex: `organize`).
    - Configure as credenciais do banco de dados no arquivo `src/main/resources/application.properties`.
    - **O Flyway cuidará da criação e atualização do esquema do banco de dados automaticamente na inicialização da aplicação.**

## Como Executar a Aplicação

Você pode executar a aplicação usando o Maven:

```bash
mvn spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`.

## Como Contribuir

1.  **Faça um fork do projeto.**
2.  **Crie uma nova branch:**
    ```bash
    git checkout -b minha-feature
    ```
3.  **Faça suas alterações e commit:**
    *   Para alterações no código, implemente e teste.
    *   **Para alterações no esquema do banco de dados, crie um novo script de migração Flyway** em `src/main/resources/db/migration/` com o próximo número de versão (`V<NUMERO>__<DESCRICAO>.sql`).
    ```bash
    git commit -m "feat: adiciona nova feature"
    ```
4.  **Envie suas alterações para o seu fork:**
    ```bash
    git push origin minha-feature
    ```
5.  **Abra um Pull Request.**