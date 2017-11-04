package client;

import model.Action;
import model.Contribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by tkaczenko on 26.03.17.
 */
public class Client extends Executor {
    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);

    private final Service service = new Service(this);
    private String hostIp;
    private int hostPort;

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
        return service.list(action);
    }

    public long sum(Action action) {
        return service.sum(action);
    }

    public int count(Action action) {
        return service.count(action);
    }

    public Contribution getContributionByAccountId(Action action) {
        return service.getContributionByAccountId(action);
    }

    public String add(Action action) {
        return service.add(action);
    }

    public String delete(Action action) {
        return service.delete(action);
    }
}
