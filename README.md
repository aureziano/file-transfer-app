# FileTransferApp

![Java](https://img.shields.io/badge/Java-11-blue)
![JSF](https://img.shields.io/badge/JSF-2.3-orange)
![PrimeFaces](https://img.shields.io/badge/PrimeFaces-8.0-blueviolet)
![Docker](https://img.shields.io/badge/Docker-19.03.12-blue)

## Sumário

- [Descrição](#descrição)
- [Funcionalidades](#funcionalidades)
- [Pré-requisitos](#pré-requisitos)
- [Instalação](#instalação)
- [Executando o Aplicativo](#executando-o-aplicativo)
- [Subindo a Imagem Docker](#subindo-a-imagem-docker)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)

## Descrição

O FileTransferApp é um aplicativo web desenvolvido em Java utilizando JSF e PrimeFaces para upload e gerenciamento de arquivos locais, SFTP e MinIO. Ele permite que os usuários façam upload de arquivos, visualizem imagens e excluam arquivos.

## Funcionalidades

- Upload de arquivos locais
- Upload de arquivos para SFTP
- Upload de arquivos para MinIO
- Visualização de imagens
- Exclusão de arquivos

## Pré-requisitos

- Java 11 ou superior
- Maven 3.6.3 ou superior
- Docker 19.03.12 ou superior

## Instalação

1. Clone o repositório:
    ```bash
    git clone https://github.com/seu-usuario/file-transfer-app.git
    cd file-transfer-app
    ```

2. Compile o projeto usando Maven:
    ```bash
    mvn clean install
    ```

## Executando o Aplicativo

1. Execute o aplicativo usando o Tomcat:
    ```bash
    mvn tomcat7:run
    ```

2. Acesse o aplicativo no navegador:
    ```
    http://localhost:8080/file-transfer-app
    ```

## Subindo a Imagem Docker

1. Crie o arquivo [Dockerfile](http://_vscodecontentref_/0) na raiz do projeto:
    ```dockerfile
    FROM tomcat:9.0.37-jdk11-openjdk

    # Copie o arquivo WAR para o diretório webapps do Tomcat
    COPY target/file-transfer-app.war /usr/local/tomcat/webapps/

    # Exponha a porta 8080
    EXPOSE 8080

    # Comando para iniciar o Tomcat
    CMD ["catalina.sh", "run"]
    ```

2. Construa a imagem Docker:
    ```bash
    docker build -t file-transfer-app .
    ```

3. Execute o contêiner Docker:
    ```bash
    docker run -d -p 8080:8080 file-transfer-app
    ```

4. Acesse o aplicativo no navegador:
    ```
    http://localhost:8080/file-transfer-app
    ```

## Tecnologias Utilizadas

- Java 11
- JSF 2.3
- PrimeFaces 8.0
- Tomcat 9.0.37
- Docker 19.03.12

## Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo LICENSE para mais detalhes.