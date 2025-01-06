#!/bin/sh

# Criar o diretório .ssh se não existir
mkdir -p /root/.ssh

# Verificar se a porta do servidor SFTP está aberta
timeout=60
while ! nc -z sftp-server 22; do
    echo "Aguardando o servidor SFTP estar disponível na porta 2222..."
    timeout=$((timeout - 1))
    if [ $timeout -le 0 ]; then
        echo "Timeout ao aguardar o servidor SFTP"
        exit 1
    fi
    sleep 2
done

# Adicionar a chave do host SFTP ao arquivo known_hosts
echo "Adicionando chave do host SFTP ao known_hosts..."
ssh-keyscan -p 22 -t ed25519,rsa sftp-server >> /root/.ssh/known_hosts

# Ajustar permissões das chaves SSH locais
chmod 600 /root/.ssh/ssh_host_ed25519_key
chmod 600 /root/.ssh/ssh_host_rsa_key

# Iniciar o Tomcat em primeiro plano
echo "Iniciando o Tomcat..."
exec catalina.sh run
