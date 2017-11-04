package database;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import model.Contribution;
import model.ContributionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.Server;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Contributions {
    private static final String DEFAULT_DATABASE = "list.json";

    private static final Logger LOGGER = LoggerFactory.getLogger(Contributions.class);
    private static final GsonBuilder GSON_BUILDER = new GsonBuilder();
    private static final Type type = new TypeToken<List<Contribution>>() {
    }.getType();

    public static Gson gson;
    public static List<Contribution> contributions = new CopyOnWriteArrayList<>();

    static {
        GSON_BUILDER.registerTypeAdapter(ContributionType.class, new Contributions.ContributionTypeDeserializer());
        gson = GSON_BUILDER.create();
    }

    public static void copy() {
        if (!new File(Contributions.DEFAULT_DATABASE).exists()) {
            try {
                Contributions.copy(Contributions.DEFAULT_DATABASE);
            } catch (IOException e) {
                LOGGER.error("Cannot copy default database", e);
            }
        }
    }

    public static void read() {
        try (Reader reader = new InputStreamReader(new FileInputStream(Contributions.DEFAULT_DATABASE))) {
            contributions = gson.fromJson(reader, type);
        } catch (FileNotFoundException e) {
            LOGGER.error("Cannot find database", e);
        } catch (IOException e) {
            LOGGER.error("Cannot write database", e);
        }
    }

    public static void write() {
        try (Writer writer = new FileWriter(DEFAULT_DATABASE)) {
            gson.toJson(contributions, writer);
        } catch (IOException e) {
            LOGGER.error("Cannot write to database", e);
        }
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
