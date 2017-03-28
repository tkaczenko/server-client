package client;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import model.Action;
import model.Contribution;
import model.ContributionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by tkaczenko on 26.03.17.
 */
public class Client {
    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);

    private static final GsonBuilder GSON_BUILDER = new GsonBuilder();
    public static Gson gson;
    private static final Type type = new TypeToken<List<Contribution>>() {
    }.getType();

    static {
        GSON_BUILDER.registerTypeAdapter(ContributionType.class, new ContributionTypeDeserializer());
        gson = GSON_BUILDER.create();
    }

    private String hostIp;
    private int hostPort;

    private BufferedReader socketReader;
    private PrintWriter socketWriter;

    public Client(String hostIp, int hostPort) {
        this.hostIp = hostIp;
        this.hostPort = hostPort;
    }

    public void setUpConnection() {
        try {
            Socket client = new Socket(hostIp, hostPort);
            socketReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            socketWriter = new PrintWriter(client.getOutputStream());
        } catch (UnknownHostException e) {
            LOGGER.error("Error setting up socket connection: unknown host at " + hostIp + ":" + hostPort, e);
        } catch (IOException e) {
            LOGGER.error("Error setting up socket connection: ", e);
        }
    }

    public void tearDownConnection() {
        try {
            socketWriter.close();
            socketReader.close();
        } catch (IOException e) {
            LOGGER.error("Error tearing down socket connection: ", e);
        }
    }

    public List<Contribution> list(Action action) {
        return gson.fromJson(getInfoByServer(action), type);
    }

    public long sum(Action action) {
        return Long.parseLong(getInfoByServer(action));
    }

    public int count(Action action) {
        return Integer.parseInt(getInfoByServer(action));
    }

    public Contribution getContributionByAccountId(Action action) {
        return gson.fromJson(getInfoByServer(action), Contribution.class);
    }

    public String add(Action action) {
        return getInfoByServer(action);
    }

    public String delete(Action action) {
        return getInfoByServer(action);
    }

    private String getInfoByServer(Action action) {
        socketWriter.println(GSON_BUILDER.create().toJson(action));
        socketWriter.flush();
        String answer = null;
        try {
            answer = socketReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return answer;
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
