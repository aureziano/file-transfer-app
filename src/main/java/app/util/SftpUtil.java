package app.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

import app.negocio.objetos.FileInfo;

public class SftpUtil {
    private static final Logger LOGGER = Logger.getLogger(SftpUtil.class.getName());

    private static ChannelSftp getChannelSftpClient() {

        String host = ConfigUtil.getProperty("sftp.host");
        String user = ConfigUtil.getProperty("sftp.user");
        String password = ConfigUtil.getProperty("sftp.password");
        int port = Integer.parseInt(ConfigUtil.getProperty("sftp.port"));

        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp channelSftp = null;

        try {
            session = jsch.getSession(user, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error connecting to SFTP server", e);
        }

        return channelSftp;
    }

    public static File downloadFile(String fileName) {
        String sftpDir = "/data";

        File file = null;

        // Ensure the target directory exists
        File targetDir = new File("/data");
        if (!targetDir.exists() && !targetDir.mkdirs()) {
            throw new IllegalStateException("Failed to create target directory: /data");
        }

        file = new File(targetDir, fileName);

        // Open the SFTP channel
        ChannelSftp channelSftp = getChannelSftpClient();

        LOGGER.info("Downloading file: " + sftpDir + "/" + fileName);
        try (InputStream inputStream = channelSftp.get(sftpDir + "/" + fileName);
                FileOutputStream outputStream = new FileOutputStream(file)) {

            byte[] buffer = new byte[8192]; // Increase buffer size for performance
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            LOGGER.info("File downloaded successfully: " + file.getAbsolutePath());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error downloading file: " + fileName, e);
        } finally {
            if (channelSftp != null && channelSftp.isConnected()) {
                channelSftp.disconnect();
            }
        }

        return file;
    }

    public static FileInfo downloadFileAsStream(String fileName) throws Exception {
        String sftpDir = "/data"; // Diretório do SFTP

        // Obter o canal SFTP
        ChannelSftp channelSftp = getChannelSftpClient();

        try {
            // Obter o tamanho do arquivo remoto
            SftpATTRS fileEntry = channelSftp.lstat(sftpDir + "/" + fileName);
            long fileSize = fileEntry.getSize(); // Obtém o tamanho do arquivo

            // Obter o InputStream do arquivo
            InputStream inputStream = channelSftp.get(sftpDir + "/" + fileName);

            Session session = channelSftp.getSession();

            // Retorna o InputStream e o tamanho do arquivo em um único objeto
            return new FileInfo(inputStream, fileSize, session, channelSftp);
        } catch (SftpException e) {
            if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                // Arquivo não encontrado
                return null;
            } else {
                throw e;
            }
        }
    }

    public static void uploadFile(InputStream inputStream, String fileName) throws Exception {
        String sftpDir = "/data";

        // Obter o canal SFTP
        ChannelSftp channelSftp = getChannelSftpClient();
        Session session = channelSftp.getSession();

        try {
            // Verificar se o arquivo já existe
            SftpATTRS attrs = null;
            try {
                attrs = channelSftp.lstat(sftpDir + "/" + fileName);
            } catch (Exception e) {
                // Arquivo não existe
            }

            if (attrs != null) {
                System.out.println("File already exists: " + sftpDir + "/" + fileName);
                return; // Não fazer o upload
            }

            channelSftp.put(inputStream, sftpDir + "/" + fileName);
            System.out.println("File uploaded successfully: " + sftpDir + "/" + fileName);
        } finally {
            if (channelSftp != null && channelSftp.isConnected()) {
                channelSftp.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }

    public static void deleteFile(String fileName) throws JSchException {
        String sftpDir = "/data";

        // Obter o canal SFTP
        ChannelSftp channelSftp = getChannelSftpClient();
        Session session = channelSftp.getSession();

        try {
            channelSftp.rm(sftpDir + "/" + fileName);
            LOGGER.info("File deleted successfully: " + sftpDir + "/" + fileName);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting file: " + fileName, e);
        } finally {
            if (channelSftp != null && channelSftp.isConnected()) {
                channelSftp.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }
}