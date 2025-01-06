package app.view;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.inject.Named;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;

import org.primefaces.model.file.UploadedFile;

import app.negocio.objetos.Anexo;
import app.util.MinioUtil;
import app.util.SftpUtil;
import javax.annotation.PostConstruct;

@Named
@ViewScoped
public class FileUploadBean implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private UploadedFile localFile;
    private UploadedFile sftpFile;
    private UploadedFile minioFile;
    private String localFileName;
    private String sftpFileName;
    private String minioFileName;
    private String localFilePath;
    private String sftpFilePath;
    private String minioFilePath;

    private List<Anexo> minioFiles ;
    private List<Anexo> sftpFiles ;
    private List<Anexo> localFiles ;

    private String filename;

    @PostConstruct
    public void init() {
        minioFiles = new ArrayList<>();
        sftpFiles = new ArrayList<>();
        localFiles = new ArrayList<>(); 
        filename = "imagem.png";
        System.out.println("FileUploadBean initialized");
    }

    

    public List<Anexo> getMinioFiles() {
        return minioFiles;
    }

    public void setMinioFiles(List<Anexo> minioFiles) {
        this.minioFiles = minioFiles;
    }

    public List<Anexo> getSftpFiles() {
        return sftpFiles;
    }

    public void setSftpFiles(List<Anexo> sftpFiles) {
        this.sftpFiles = sftpFiles;
    }

    public List<Anexo> getLocalFiles() {
        return localFiles;
    }

    public void setLocalFiles(List<Anexo> localFiles) {
        this.localFiles = localFiles;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public UploadedFile getLocalFile() {
        return localFile;
    }

    public void setLocalFile(UploadedFile localFile) {
        this.localFile = localFile;
    }

    public UploadedFile getSftpFile() {
        return sftpFile;
    }

    public void setSftpFile(UploadedFile sftpFile) {
        this.sftpFile = sftpFile;
    }

    public UploadedFile getMinioFile() {
        return minioFile;
    }

    public void setMinioFile(UploadedFile minioFile) {
        this.minioFile = minioFile;
    }

    public String getLocalFileName() {
        return localFileName;
    }

    public String getSftpFileName() {
        return sftpFileName;
    }

    public String getMinioFileName() {
        return minioFileName;
    }

    public String getLocalFilePath() {
        return localFilePath;
    }

    public String getSftpFilePath() {
        return sftpFilePath;
    }

    public String getMinioFilePath() {
        return minioFilePath;
    }

    public String uploadLocalFile() {
        if (localFile != null) {
            try (InputStream input = localFile.getInputStream()) {
                Path targetPath = Paths.get("/data", localFile.getFileName());
                System.out.println("Initial target path: " + targetPath);

                // Verificar se o arquivo já existe e gerar um nome único
                if (Files.exists(targetPath)) {
                    System.out.println("File already exists: " + targetPath);
                    String baseName = localFile.getFileName();
                    String extension = "";
                    int dotIndex = baseName.lastIndexOf('.');
                    if (dotIndex > 0) {
                        extension = baseName.substring(dotIndex);
                        baseName = baseName.substring(0, dotIndex);
                    }
                    int counter = 1;
                    while (Files.exists(targetPath)) {
                        targetPath = Paths.get("/data", baseName + "_" + counter + extension);
                        counter++;
                    }
                    System.out.println("New target path: " + targetPath);
                }

                Files.copy(input, targetPath, StandardCopyOption.REPLACE_EXISTING);
                localFileName = targetPath.getFileName().toString();
                localFilePath = targetPath.toString();
                localFiles.add(new Anexo(localFileName, Files.readAllBytes(targetPath), localFilePath));

                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Local file uploaded successfully"));
                return null; // Permanecer na mesma página
            } catch (Exception e) {
                e.printStackTrace();
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Local file upload failed"));
                return null; // Permanecer na mesma página
            }
        }
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No local file selected"));
        return null; // Permanecer na mesma página
    }

    public String uploadSftpFile() {
        if (sftpFile != null) {
            try (InputStream input = sftpFile.getInputStream()) {
                SftpUtil.uploadFile(input, sftpFile.getFileName());
                sftpFileName = sftpFile.getFileName();
                sftpFilePath = "/data/" + sftpFile.getFileName();
                sftpFiles.add(new Anexo(sftpFileName, Files.readAllBytes(Paths.get(sftpFilePath)), sftpFilePath));

                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "SFTP file uploaded successfully"));
                return null; // Permanecer na mesma página
            } catch (Exception e) {
                e.printStackTrace();
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "SFTP file upload failed"));
                return null; // Permanecer na mesma página
            }
        }
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No SFTP file selected"));
        return null; // Permanecer na mesma página
    }

    public String uploadMinioFile() {
        if (minioFile != null) {
            try (InputStream input = minioFile.getInputStream()) {
                // Save the file locally first
                Files.copy(input, Paths.get("/data", minioFile.getFileName()),
                        StandardCopyOption.REPLACE_EXISTING);
                minioFileName = minioFile.getFileName();
                minioFilePath = "/data/" + minioFile.getFileName();

                // Read the file into a byte array
                byte[] fileBytes = Files.readAllBytes(Paths.get("/data", minioFile.getFileName()));
                try (InputStream byteArrayInputStream = new ByteArrayInputStream(fileBytes)) {
                    // Upload the file to MinIO
                    MinioUtil.uploadFile("data/" + minioFile.getFileName(), byteArrayInputStream,
                            minioFile.getSize(),
                            minioFile.getContentType());
                }
                minioFiles.add(new Anexo(minioFileName, fileBytes, minioFilePath));

                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "MinIO file uploaded successfully"));
                return null; // Permanecer na mesma página
            } catch (Exception e) {
                e.printStackTrace();
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "MinIO file upload failed"));
                return null; // Permanecer na mesma página
            }
        }
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No MinIO file selected"));
        return null; // Permanecer na mesma página
    }

    public void deleteLocalFile(Anexo file) {
        try {

            // Construir o caminho completo do arquivo
            Path filePath = Paths.get(file.getCaminho());
    
            System.out.println("Deleting local file: " + file.getCaminho());
            
            // Verificar se o arquivo existe antes de tentar deletar
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                localFiles.remove(file);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Local file deleted successfully"));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", "File not found"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to delete local file"));
        }
    }

    public void deleteSftpFile(Anexo file) {
        try {
            SftpUtil.deleteFile(file.getNome());
            sftpFiles.remove(file);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "SFTP file deleted successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to delete SFTP file"));
        }
    }

    public void deleteMinioFile(Anexo file) {
        try {
            MinioUtil.deleteFile("data/" + file.getNome());
            minioFiles.remove(file);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "MinIO file deleted successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to delete MinIO file"));
        }
    }
}