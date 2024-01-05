package org.example.servlet;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.example.database.DatabaseConnection;
import org.example.minio.MinioService;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

@MultipartConfig
public class UploadMusicServlet extends HttpServlet {

    private String BUCKET_NAME = "music";
    private final DatabaseConnection databaseConnection = new DatabaseConnection();
    private final Logger logger = Logger.getLogger("logger");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            String artistName = request.getParameter("artistName");
            String musicName = request.getParameter("musicName");
            boolean isMultipart = ServletFileUpload.isMultipartContent(request);
            if (!isMultipart) {
                response.getWriter().println("Error: This servlet requires file upload.");
                return;
            }

            DiskFileItemFactory factory = new DiskFileItemFactory();

            ServletFileUpload upload = new ServletFileUpload(factory);
            try {
                List<FileItem> items = upload.parseRequest(request);

                for (FileItem item : items) {
                    if (!item.isFormField()) {
                        String fileName = generateRandomName(item.getName());
                        try (InputStream inputStream = item.getInputStream()) {
                            MinioClient minioClient = MinioService.getMinioClient();

                            minioClient.putObject(
                                    PutObjectArgs.builder()
                                            .bucket(BUCKET_NAME)
                                            .object(fileName)
                                            .stream(inputStream, item.getSize(), -1)
                                            .contentType(item.getContentType())
                                            .build()
                            );

                            String insertQuery = "INSERT INTO music (name,artist_name,minio_object) VALUES (?,?,?)";
                            try(Connection connection = databaseConnection.getConnection()) {
                                PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
                                preparedStatement.setString(1,musicName);
                                preparedStatement.setString(2,artistName);
                                preparedStatement.setString(3,fileName);
                                preparedStatement.executeUpdate();
                            }catch (Exception e) {
                                logger.log(Level.WARNING,"Problem with database");
                            }
                        }
                    }
                }
            } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                     NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                     FileUploadException | InternalException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
     }
     private String generateRandomName(String name) {
        StringBuilder stringBuilder = new StringBuilder(name);
        int start = 65;
        int end = 90;
        Random random = new Random();

        for(int i = 0;i<7;i++) {
            int rand = random.nextInt(start,end);
            char c = (char)rand;
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
     }
    }