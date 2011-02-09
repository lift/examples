import _root_.org.mortbay.jetty.Connector;
import org.mortbay.jetty.nio._
import _root_.org.mortbay.jetty.Server;
import _root_.org.mortbay.jetty.bio.SocketConnector;
import _root_.org.mortbay.jetty.webapp.WebAppContext;

object RunWebApp extends Application {
        val server = new Server();
        val connector = new SelectChannelConnector();
        connector.setPort(8888);
        server.setConnectors(Array(connector));

        val bb = new WebAppContext()
        bb.setServer(server)
        bb.setContextPath("/")
        bb.setWar("src/main/webapp")

        server.addHandler(bb)

        try {
            println(">>> STARTING EMBEDDED JETTY SERVER, PRESS ANY KEY TO STOP");
            server.start();
            while (System.in.available() == 0) {
                Thread.sleep(5000)
            }
            server.stop()
            server.join()
        } catch {
          case exc : Exception => {
            exc.printStackTrace()
            System.exit(100)
          }
        }
}
