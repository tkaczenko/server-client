package server;

import database.Contributions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by tkaczenko on 26.03.17.
 */
public class Server implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);
    private static final int DEFAULT_NUMBER_OF_THREADS = 10;

    private int numOfThreads = DEFAULT_NUMBER_OF_THREADS;

    private int listenPort;
    private ServerSocket serverSocket = null;
    private boolean isStopped = false;
    private Thread runningThread = null;
    private ExecutorService threadPool = Executors.newFixedThreadPool(numOfThreads);

    public Server(int listenPort) {
        this.listenPort = listenPort;
    }

    @Override
    public void run() {
        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }
        Contributions.copy();
        Contributions.read();
        acceptConnections();
        Contributions.write();
        this.threadPool.shutdown();
        LOGGER.info("Server stopped");
    }

    private void acceptConnections() {
        try {
            this.serverSocket = new ServerSocket(listenPort);
            Socket incomingConnection = null;
            while (!isStopped()) {
                try {
                    incomingConnection = serverSocket.accept();
                } catch (IOException e) {
                    if (isStopped()) {
                        return;
                    }
                    LOGGER.error("Error accepting client connection", e);
                }
                handleConnection(incomingConnection);
            }
        } catch (IOException e) {
            LOGGER.error("Cannot create server socket", e);
        }
    }

    private void handleConnection(Socket incomingConnection) {
        this.threadPool.execute(new ConnectionHandler(incomingConnection));
    }

    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop() {
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            LOGGER.error("Error closing server", e);
        }
    }

    public int getNumOfThreads() {
        return numOfThreads;
    }

    public void setNumOfThreads(int numOfThreads) {
        this.numOfThreads = numOfThreads;
    }
}
