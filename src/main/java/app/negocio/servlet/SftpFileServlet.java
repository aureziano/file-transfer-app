package app.negocio.servlet;

import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import app.negocio.objetos.FileInfo;
import app.util.SftpUtil;

@WebServlet("/sftpFileServlet")
public class SftpFileServlet extends HttpServlet {  
    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_IMAGE_PATH = "./WEB-INF/classes/images/sem-foto.png";
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String fileName = request.getParameter("fileName");

        if (fileName == null || fileName.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "File name is required");
            return;
        }

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

        FileInfo fileInfo = null;
        InputStream in = null;
        OutputStream out = null;

        try {
            // Obter o InputStream e o tamanho do arquivo remoto
            fileInfo = SftpUtil.downloadFileAsStream(fileName);

            // Verifica se o arquivo foi encontrado
            if (fileInfo == null || fileInfo.getInputStream() == null) {
                // Se o arquivo não for encontrado, usar a imagem padrão
                File defaultFile = new File(getServletContext().getRealPath(DEFAULT_IMAGE_PATH));
                in = new FileInputStream(defaultFile);
                response.setContentType(getServletContext().getMimeType(defaultFile.getName()));
                response.setContentLengthLong(defaultFile.length());
            } else {
                // Define o cabeçalho de tamanho do arquivo
                response.setContentLengthLong(fileInfo.getFileSize());
                in = fileInfo.getInputStream();
            }

            out = response.getOutputStream();
            byte[] buffer = new byte[8192]; // Buffer maior
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush(); // Ensure all data is written out

        } catch (Exception e) {
            e.printStackTrace();
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving file from SFTP.");
            }
        } finally {
            if (fileInfo != null) {
                fileInfo.close();
            }
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }
}