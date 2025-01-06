package app.negocio.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import app.util.MinioUtil;

@WebServlet("/minioFileServlet")
public class MinioFileServlet extends HttpServlet {
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

        try (InputStream in = MinioUtil.downloadFileAsStream(fileName);
                OutputStream out = response.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } catch (FileNotFoundException e) {
            // Se o arquivo não for encontrado, usar a imagem padrão
            File defaultFile = new File(getServletContext().getRealPath(DEFAULT_IMAGE_PATH));
            try (FileInputStream in = new FileInputStream(defaultFile);
                 OutputStream out = response.getOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                response.setContentType(getServletContext().getMimeType(defaultFile.getName()));
                response.setContentLengthLong(defaultFile.length());
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving file from MinIO");
        }
    }
}