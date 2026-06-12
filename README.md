[README.md](https://github.com/user-attachments/files/28865208/README.md)
<div align="center">

# 📋 Agenda de Contatos

Aplicativo desktop para gerenciar contatos pessoais, desenvolvido em Java com interface gráfica moderna.

![Java](https://img.shields.io/badge/Java-17%2B-orange?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-21-blue?style=for-the-badge&logo=java&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14%2B-336791?style=for-the-badge&logo=postgresql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.8%2B-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

</div>

---

## ✨ Funcionalidades

- **Adicionar** contatos com nome, telefone, e-mail e endereço
- **Buscar** contatos pelo nome em tempo real
- **Editar** os dados de qualquer contato
- **Excluir** contatos com confirmação de segurança
- **Banco de dados automático** — na primeira execução, o app cria tudo sozinho

---

## 🖥️ Pré-requisitos

Antes de instalar o projeto, você precisa ter os seguintes programas no seu computador:

| Programa | Versão mínima | Para que serve | Download |
|---|---|---|---|
| **Java (JDK)** | 17 | Rodar o aplicativo | [Adoptium.net](https://adoptium.net/) |
| **Maven** | 3.8 | Gerenciar dependências | [maven.apache.org](https://maven.apache.org/download.cgi) |
| **PostgreSQL** | 14 | Banco de dados | [postgresql.org](https://www.postgresql.org/download/) |
| **IntelliJ IDEA** | Qualquer | IDE para abrir o projeto | [jetbrains.com](https://www.jetbrains.com/idea/download/) *(Community é gratuita)* |


## ⚙️ Instalação

### 1. Clone ou baixe o projeto

**Via Git:**
```bash
git clone https://github.com/seu-usuario/agenda-contatos.git
cd agenda-contatos
```

**Ou clique em `Code → Download ZIP`** no GitHub e extraia a pasta.

---

### 2. Abra no IntelliJ IDEA

1. Abra o IntelliJ IDEA
2. Clique em **File → Open**
3. Selecione a pasta `agenda-contatos`
4. Aguarde o Maven baixar as dependências automaticamente *(pode demorar alguns minutos na primeira vez)*

---

### 3. Configure sua senha do PostgreSQL

Abra o arquivo abaixo no IntelliJ:

```
src/main/java/com/agenda/dao/ConexaoDB.java
```

Encontre a linha e substitua pelo que você definiu durante a instalação do PostgreSQL:

```java
private static final String SENHA = "sua_senha_aqui"; // ← coloque sua senha aqui
```

> Essa é a **única configuração necessária**. O restante (criar banco, tabela e dados de exemplo) é feito automaticamente pelo app.

---

### 4. Execute o aplicativo

**Pelo IntelliJ:**
- Abra o arquivo `src/main/java/com/agenda/Main.java`
- Clique no botão **▶ Run** (ou pressione `Shift + F10`)

**Pelo terminal:**
```bash
mvn javafx:run
```

---

## 🚀 Primeira execução

Na primeira vez que rodar, o app cria tudo automaticamente:

```
✅ Banco de dados 'agenda_contatos' criado!
📋 Tabela 'contatos' criada!
👥 Contatos de exemplo inseridos!
✅ Conectado ao banco!
```

A tela principal será aberta com **5 contatos de exemplo** já cadastrados para você testar.

---

## 📖 Como usar

### Tela principal

```
┌─────────────────────────────────────────────────────────────┐
│  📋 Agenda de Contatos    [🔍 Buscar...]   [＋ Novo Contato] │
├─────────────────────────────────────────────────────────────┤
│  Nome              Telefone         E-mail       Endereço    │
│  ─────────────────────────────────────────────────────────  │
│  Ana Costa         (24) 97777-9012  ana@...      Rua...      │
│  Beatriz Ferreira  (24) 95555-7890  beatriz@...  Rua...      │
│  Carlos Souza      (21) 96666-3456  carlos@...   Av....      │
├─────────────────────────────────────────────────────────────┤
│  3 contatos                         [✏ Editar] [🗑 Excluir] │
└─────────────────────────────────────────────────────────────┘
```

---

### ➕ Adicionar um contato

1. Clique no botão **＋ Novo Contato** (canto superior direito)
2. Preencha o formulário — somente o **Nome** é obrigatório
3. Clique em **Salvar**

---

### 🔍 Buscar um contato

Digite qualquer parte do nome no campo **"Buscar por nome..."** — a lista filtra automaticamente enquanto você digita.

Para voltar a ver todos, apague o texto do campo.

---

### ✏️ Editar um contato

1. **Clique** no contato na tabela para selecioná-lo
2. Clique no botão **✏ Editar** (canto inferior direito)
3. Altere os campos desejados
4. Clique em **Salvar**

---

### 🗑️ Excluir um contato

1. **Clique** no contato na tabela para selecioná-lo
2. Clique no botão **🗑 Excluir**
3. Confirme a exclusão na janela que aparecer

> ⚠️ A exclusão é **permanente** e não pode ser desfeita.

---

## ❌ Solução de problemas

<details>
<summary><strong>O app não abre e mostra "Erro de Conexão"</strong></summary>

Verifique:

1. **O PostgreSQL está rodando?**
   - Windows: abra o **Gerenciador de Serviços** (`services.msc`) e procure por `postgresql` → deve estar como *Em execução*
   - Linux/Mac: rode no terminal: `sudo service postgresql start`

2. **A senha está correta?**
   - Abra `ConexaoDB.java` e confira o valor de `SENHA`

</details>

<details>
<summary><strong>Erro ao baixar dependências no IntelliJ</strong></summary>

- Verifique se está conectado à internet
- Clique com o botão direito no `pom.xml` → **Maven → Reload Project**

</details>

<details>
<summary><strong>"Java not found" ao tentar rodar</strong></summary>

- Confirme que o Java 17+ está instalado: `java -version` no terminal
- No IntelliJ: **File → Project Structure → SDK** → selecione o Java 17

</details>

---

## 🏗️ Estrutura do projeto

```
agenda-contatos/
├── src/main/
│   ├── java/
│   │   ├── module-info.java                 # Módulos Java (necessário para JavaFX)
│   │   └── com/agenda/
│   │       ├── Main.java                    # Ponto de entrada
│   │       ├── model/Contato.java           # Representa um contato
│   │       ├── dao/
│   │       │   ├── ConexaoDB.java           # Conexão e setup automático do banco
│   │       │   └── ContatoDAO.java          # Operações CRUD
│   │       └── controller/
│   │           └── MainController.java      # Lógica da interface
│   └── resources/com/agenda/
│       ├── main-view.fxml                   # Layout da tela
│       └── style.css                        # Estilos visuais
├── banco.sql     # Script manual (alternativa ao setup automático)
├── pom.xml       # Dependências Maven
└── README.md
```

---

## 🛠️ Tecnologias

- **[Java 17](https://adoptium.net/)** — linguagem principal
- **[JavaFX 21](https://openjfx.io/)** — interface gráfica
- **[PostgreSQL](https://www.postgresql.org/)** — banco de dados relacional
- **[JDBC](https://docs.oracle.com/javase/tutorial/jdbc/)** — conexão Java ↔ banco
- **[Maven](https://maven.apache.org/)** — gerenciador de dependências
- Padrão de projeto: **MVC + DAO**

---

## 📌 Melhorias futuras

- [ ] Foto de perfil nos contatos
- [ ] Exportar lista para CSV
- [ ] Grupos e categorias
- [ ] Ordenação clicando nas colunas
- [ ] Validação de formato de e-mail e telefone

---

<div align="center">

Desenvolvido como projeto de estudos com Java + JavaFX + PostgreSQL

</div>
