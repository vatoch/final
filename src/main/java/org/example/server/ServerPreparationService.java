package org.example.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.example.servlet.FetchMusicServlet;
import org.example.servlet.UploadMusicServlet;

public class ServerPreparationService {

    public static void prepareServer() throws Exception {
        Server server = new Server(8082);
        ServletContextHandler servletContextHandler = new ServletContextHandler();
        servletContextHandler.setContextPath("/");
        server.setHandler(servletContextHandler);
        ServletHolder servletHolder = new ServletHolder(new UploadMusicServlet());
        ServletHolder servletHolder2 = new ServletHolder(new FetchMusicServlet());
        servletContextHandler.addServlet(servletHolder,"/upload");
        servletContextHandler.addServlet(servletHolder2,"/fetch");
        server.start();
        server.join();;
    }
}
