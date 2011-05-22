package com.bokella.webxtractor.server.util.io.gae;

import java.net.*;
import java.util.concurrent.TimeUnit;
import org.apache.http.conn.*;
import org.apache.http.params.*;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.*;

public class GAEConnectionManager
  implements ClientConnectionManager {
  
  public GAEConnectionManager() {
    SocketFactory no_socket_factory = new SocketFactory() {
        public Socket connectSocket(Socket sock, String host, int port, 
                                    InetAddress localAddress, int localPort, 
                                    HttpParams params) {
          return null;
        }

        public Socket createSocket() {
          return null;
        }

        public boolean isSecure(Socket s) {
          return false;
        }
      };

    schemeRegistry = new SchemeRegistry();
    schemeRegistry.register(new Scheme("http",  no_socket_factory, 80));
    schemeRegistry.register(new Scheme("https", no_socket_factory, 443));
  }


  public SchemeRegistry getSchemeRegistry() {
    return schemeRegistry;
  }

  public ClientConnectionRequest requestConnection(final HttpRoute route, 
                                                             final Object state) {
    return new ClientConnectionRequest() {
      public void abortRequest() {
        // Nothing to do
      }

      public ManagedClientConnection getConnection(long timeout, TimeUnit tunit) {
        return GAEConnectionManager.this.getConnection(route, state);
      }
    };
  }

  public void releaseConnection(ManagedClientConnection conn, 
                                          long validDuration, TimeUnit timeUnit) {
  }

  public void closeIdleConnections(long idletime, TimeUnit tunit) {
  }

  public void closeExpiredConnections() {
  }

  public void shutdown() {
  }

  private ManagedClientConnection getConnection(HttpRoute route, Object state) {
    return new GAEClientConnection(this, route, state);
  }

  private SchemeRegistry schemeRegistry;
}
