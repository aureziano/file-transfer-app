package app.negocio.objetos;

import java.io.InputStream;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;

public class FileInfo {
    private InputStream inputStream;
    private long fileSize;
    private Session session;
    private ChannelSftp channelSftp;

    public FileInfo(InputStream inputStream, long fileSize, Session session, ChannelSftp channelSftp) {
        this.inputStream = inputStream;
        this.fileSize = fileSize;
        this.session = session;
        this.channelSftp = channelSftp;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void close() {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (channelSftp != null && channelSftp.isConnected()) {
            channelSftp.disconnect();
        }
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }
}