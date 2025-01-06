package app.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

// Util - Transferência local
public class FileTransferUtilLocal {
    public static String saveFile(String fileName, InputStream fileContent) throws IOException {
        File targetFile = new File("uploads/" + fileName);
        FileOutputStream outStream = new FileOutputStream(targetFile);

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fileContent.read(buffer)) != -1) {
            outStream.write(buffer, bytesRead, bytesRead);
        }
        outStream.close();
        return targetFile.getAbsolutePath();
    }
}