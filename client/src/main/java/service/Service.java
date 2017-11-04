package service;

import client.Executor;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import model.Action;
import model.Contribution;
import model.ContributionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.List;

public class Service {
    private static final Logger LOGGER = LoggerFactory.getLogger(Service.class);

    private static final GsonBuilder GSON_BUILDER = new GsonBuilder();
    public static Gson gson;
    private static final Type type = new TypeToken<List<Contribution>>() {
    }.getType();

    private Executor executor;

    public Service(Executor executor) {
        this.executor = executor;
    }

    static {
        GSON_BUILDER.registerTypeAdapter(ContributionType.class, new Service.ContributionTypeDeserializer());
        gson = GSON_BUILDER.create();
    }

    public List<Contribution> list(Action action) {
        return gson.fromJson(executor.execute(getAction(action)), type);
    }

    public long sum(Action action) {
        return Long.parseLong(executor.execute(getAction(action)));
    }

    public int count(Action action) {
        return Integer.parseInt(executor.execute(getAction(action)));
    }

    public Contribution getContributionByAccountId(Action action) {
        return gson.fromJson(executor.execute(getAction(action)), Contribution.class);
    }

    public String add(Action action) {
        return executor.execute(getAction(action));
    }

    public String delete(Action action) {
        return executor.execute(getAction(action));
    }

    private String getAction(Action action) {
        return GSON_BUILDER.create().toJson(action);
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
