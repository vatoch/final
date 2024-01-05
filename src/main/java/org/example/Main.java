package org.example;

import org.example.database.DatabaseConnection;
import org.example.minio.MinioService;
import org.example.server.ServerPreparationService;


public class Main {
    public static void main(String[] args) throws Exception {

        ServerPreparationService.prepareServer();
    }

    static {
        MinioService.prepareBucket();
       DatabaseConnection.prepareTables();
    }
}