package app.negocio.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import app.util.MinioUtil;
import app.util.SftpUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@WebServlet("/fileServlet")
public class FileServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String source = request.getParameter("source");

        File fileLocal = null;
        File fileSftp = null;
        File fileMinio = null;

        switch (source) {
            case "local":
                fileLocal = new File("/data/imagem.png");
                sendFile(response, fileLocal);
                break;
            case "sftp":
                fileSftp = SftpUtil.downloadFile("imagem.png");
                sendFile(response, fileSftp);
                break;
            case "minio":
                fileMinio = MinioUtil.downloadFile("imagem.png");
                sendFile(response, fileMinio);
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid source parameter");
                return;
        }
        

        // if (file != null && file.exists()) {
        // response.setContentType("image/jpeg");
        // try (FileInputStream fis = new FileInputStream(file);
        // OutputStream os = response.getOutputStream()) {
        // byte[] buffer = new byte[1024];
        // int bytesRead;
        // while ((bytesRead = fis.read(buffer)) != -1) {
        // os.write(buffer, 0, bytesRead);
        // }
        // }
        // } else {
        // response.sendError(HttpServletResponse.SC_NOT_FOUND);
        // }
    }

    private void sendFile(HttpServletResponse response, File file) throws IOException {
        if (file == null || !file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        } else {
            response.setContentType("image/jpeg");
            try (FileInputStream fis = new FileInputStream(file);
                    OutputStream os = response.getOutputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }
        }
    }
}