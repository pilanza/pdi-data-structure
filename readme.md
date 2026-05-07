# 📸 PDI - Processamento Digital de Imagens

Projeto desenvolvido para a disciplina de PDI, utilizando Java com JavaFX para interface gráfica.

---

## 📋 Índice

- [Pré-requisitos](#pré-requisitos)
- [Instalação do JDK](#instalação-do-jdk)
- [Instalação do Maven](#instalação-do-maven)
- [Configuração do JavaFX](#configuração-do-javafx)
- [Como rodar o projeto](#como-rodar-o-projeto)
- [Usando o Git](#usando-o-git)

---

## Pré-requisitos

Antes de começar, você vai precisar instalar:

- **JDK 25** (Java Development Kit)
- **Maven** (gerenciador de dependências)
- **VSCode** com a extensão **Extension Pack for Java**

---

## Instalação do JDK

O JDK é o kit necessário para **compilar e rodar** código Java. Sem ele, nada funciona.

1. Acesse: [adoptium.net](https://adoptium.net)
2. Baixe o **JDK 25** para Windows (arquivo `.msi`)
3. Execute o instalador e siga os passos normalmente
4. Para verificar se funcionou, abra o terminal e digite:

```bash
java -version
javac -version
```

Os dois comandos devem mostrar a versão 25. Se apenas o `java` funcionar e o `javac` não, o JDK não foi instalado corretamente — apenas o JRE foi.

---

## Instalação do Maven

O Maven é o responsável por baixar o JavaFX automaticamente e compilar o projeto.

1. Acesse: [maven.apache.org/download.cgi](https://maven.apache.org/download.cgi)
2. Baixe o arquivo **Binary zip archive** (o `.zip`, não o source)
3. Extraia em uma pasta, por exemplo: `C:\maven`
4. Adicione o Maven no PATH do Windows:
   - Pesquise **"variáveis de ambiente"** no menu iniciar
   - Clique em **"Editar as variáveis de ambiente do sistema"**
   - Clique em **"Variáveis de Ambiente..."**
   - Em **"Variáveis do sistema"**, selecione `Path` e clique em **Editar**
   - Clique em **Novo** e adicione: `C:\maven\bin`
   - Clique OK em tudo
5. Abra um **novo terminal** e verifique:

```bash
mvn -version
```

Deve mostrar a versão do Maven e o caminho do JDK.

---

## Configuração do JavaFX

O JavaFX já está configurado no arquivo `pom.xml` do projeto. Você **não precisa instalar nada manualmente** — o Maven baixa tudo sozinho na primeira vez que você rodar o projeto.

O `pom.xml` está na raiz do projeto e contém as dependências do JavaFX 23.

---

## Como rodar o projeto

1. Abra o terminal na pasta raiz do projeto (onde está o `pom.xml`)
2. Execute:

```bash
mvn javafx:run
```

Na primeira vez, o Maven vai baixar as dependências — isso pode demorar alguns minutos. Da segunda vez em diante, será muito mais rápido.

> ⚠️ **Atenção:** sempre execute o comando dentro da pasta do projeto, não em outra pasta.

---

## Usando o Git

O Git é uma ferramenta de **controle de versão** — ele registra o histórico de alterações do código e permite que várias pessoas trabalhem no mesmo projeto sem sobrescrever o trabalho umas das outras.

### Conceitos básicos

Pense no Git como um sistema de **salvamentos de um jogo**:

- **Repositório (repo):** é o projeto inteiro, com todo o histórico de salvamentos
- **Commit:** é um salvamento — um registro de "o que mudou e quando"
- **Branch:** é uma linha paralela do projeto (como salvar em outro slot)
- **Push:** é enviar seus salvamentos para o servidor (GitHub)
- **Pull:** é baixar os salvamentos mais recentes do servidor

---

### Fluxo básico de trabalho

Toda vez que você fizer alterações no código e quiser salvar no GitHub, siga esses 3 passos:

#### Passo 1 — `git add` (selecionar o que vai salvar)

```bash
git add .
```

O `.` significa "adiciona tudo que mudou". Se quiser adicionar apenas um arquivo específico:

```bash
git add src/main/java/com/pdi/views/MainView.java
```

#### Passo 2 — `git commit` (criar o salvamento)

```bash
git commit -m "Descrição do que você fez"
```

A mensagem entre aspas deve descrever o que foi alterado. Exemplos de boas mensagens:

```bash
git commit -m "Adiciona MenuBar com menu Arquivo"
git commit -m "Implementa filtro de média para imagens em escala de cinza"
git commit -m "Corrige bug no carregamento de imagens PNG"
```

> ⚠️ **Dica:** seja específico na mensagem. "ajustes" ou "mudanças" não dizem nada — imagine que você vai precisar encontrar esse commit daqui a 2 meses.

#### Passo 3 — `git push` (enviar para o GitHub)

```bash
git push
```

Isso envia todos os commits locais para o repositório remoto no GitHub.

---

### Antes de começar a trabalhar: `git pull`

Sempre que for começar a trabalhar, primeiro baixe as alterações mais recentes dos seus colegas:

```bash
git pull
```

Isso evita conflitos e garante que você está trabalhando na versão mais atual do código.

---

### Resumo do fluxo completo

```bash
# 1. Antes de começar: baixa as atualizações
git pull

# 2. Faz suas alterações no código...

# 3. Seleciona as alterações
git add .

# 4. Cria o salvamento com uma descrição
git commit -m "O que você fez"

# 5. Envia para o GitHub
git push
```

---

### Verificar o status

Se quiser ver o que mudou antes de fazer o commit:

```bash
git status
```

Mostra quais arquivos foram modificados, adicionados ou deletados.

Para ver o histórico de commits:

```bash
git log --oneline
```

---

> 💡 **Dúvidas?** Fale com o restante do grupo antes de fazer alterações grandes no código, especialmente nos arquivos que outras pessoas também estão editando.