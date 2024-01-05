package org.example.servlet;

import com.google.gson.Gson;
import org.example.database.DatabaseConnection;
import org.example.minio.MinioService;
import org.example.model.MusicDTO;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FetchMusicServlet extends HttpServlet {

    private final MinioService minioService = new MinioService();
    private final DatabaseConnection databaseConnection = new DatabaseConnection();
    private final Logger logger = Logger.getLogger("logger");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<MusicDTO> musicDTOS = fetchMusicFromDatabase();
        Gson gson = new Gson();
        String json = gson.toJson(musicDTOS);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(json);

    }


    public List<MusicDTO> fetchMusicFromDatabase() {
        String selectQuery = "SELECT id,name,artist_name,minio_object FROM music";
        List<MusicDTO> result = new ArrayList<>();
        try(Connection connection = databaseConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet!=null) {
                while (resultSet.next()) {
                    MusicDTO musicDTO = new MusicDTO();
                    String musicName = resultSet.getString("name");
                    String artistName = resultSet.getString("artist_name");
                    String url = minioService.getMinioUrl("music", resultSet.getString("minio_object"));
                    musicDTO.setMusicName(musicName);
                    musicDTO.setMinioUrl(url);
                    musicDTO.setArtistName(artistName);
                    result.add(musicDTO);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.WARNING,"Problem with database");
        }
        return result;
    }
}
