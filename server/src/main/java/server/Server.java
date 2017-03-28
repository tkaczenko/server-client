package server;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import model.Contribution;
import model.ContributionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by tkaczenko on 26.03.17.
 */
public class Server implements Runnable {
    private static final String DEFAULT_DATABASE = "list.json";

    private static final GsonBuilder GSON_BUILDER = new GsonBuilder();
    private static final Type type = new TypeToken<List<Contribution>>() {
    }.getType();

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);
    private static final int DEFAULT_NUMBER_OF_THREADS = 10;

    static Gson gson;
    static List<Contribution> contributions = new CopyOnWriteArrayList<>();

    static {
        GSON_BUILDER.registerTypeAdapter(ContributionType.class, new ContributionTypeDeserializer());
        gson = GSON_BUILDER.create();
    }

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
        if (!new File(DEFAULT_DATABASE).exists()) {
            try {
                copy(DEFAULT_DATABASE);
            } catch (IOException e) {
                LOGGER.error("Cannot copy default database", e);
            }
        }
        try (Reader reader = new InputStreamReader(new FileInputStream(DEFAULT_DATABASE))) {
            contributions = gson.fromJson(reader, type);
        } catch (FileNotFoundException e) {
            LOGGER.error("Cannot find database", e);
        } catch (IOException e) {
            LOGGER.error("Cannot read database", e);
        }
        acceptConnections();
        try (Writer writer = new FileWriter(DEFAULT_DATABASE)) {
            gson.toJson(contributions, writer);
        } catch (IOException e) {
            LOGGER.error("Cannot write to database", e);
        }
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

    private static void copy(String destination) throws IOException {
        try (InputStream inputStream = new BufferedInputStream(Server.class.getResourceAsStream("/list.json"))) {
            try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(destination))) {
                try {
                    int data;
                    do {
                        data = inputStream.read();
                        if (data != -1) {
                            outputStream.write(data);
                        }
                    } while (data != -1);
                } catch (IOException e) {
                    System.out.println("Error: Copying failed.");
                }
            } catch (FileNotFoundException e) {
                System.out.println("Error: Destination file not found.");
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: Source file not found.");
        }
    }

    private static class ContributionTypeDeserializer implements JsonDeserializer<ContributionType> {
        @Override
        public ContributionType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            ContributionType[] types = ContributionType.values();
            for (ContributionType type : types) {
                if (type.getCode() == json.getAsInt())
                    return type;
            }
            return null;
        }
    }
}
