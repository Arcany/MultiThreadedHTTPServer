import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

public class JettyServer {
    public static void main(String[] args) throws Exception {
        int maxThreads = 100;
        int minThreads = 20;
        int idleTimeout = 120;

        QueuedThreadPool threadPool = new QueuedThreadPool(maxThreads, minThreads, idleTimeout);
        Server server = new Server(threadPool);
        ServerConnector http = new ServerConnector(server);
        http.setPort(1337);
        server.addConnector(http);
        ServletHandler handler = new ServletHandler();
        handler.addServletWithMapping(SumNumHandler.class, "/");
        server.setHandler(handler);
        server.start();
        server.join();
    }
}
