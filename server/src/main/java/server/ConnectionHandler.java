package server;

import model.Action;
import model.Contribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.ServerException;
import service.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import static database.Contributions.contributions;
import static database.Contributions.gson;

/**
 * Created by tkaczenko on 26.03.17.
 */
class ConnectionHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionHandler.class);
    private final Service service = new Service();

    private Socket socketToHandle;
    private PrintWriter streamWriter;
    private BufferedReader streamReader;

    private volatile boolean isRunning = true;

    ConnectionHandler(Socket socketToHandle) {
        this.socketToHandle = socketToHandle;
    }

    @Override
    public void run() {
        try {
            streamWriter = new PrintWriter(socketToHandle.getOutputStream());
            streamReader = new BufferedReader(new InputStreamReader(socketToHandle.getInputStream()));

            String clientCommand = "";
            while (isRunning) {
                try {
                    clientCommand = streamReader.readLine();
                } catch (IOException e) {
                    logger.error("Cannot write service", e);
                }
                Action action = gson.fromJson(clientCommand, Action.class);
                if (action == null) {
                    continue;
                }
                if ("exit".equals(action.getAction().toLowerCase())) {
                    System.out.println("Disconnect");
                    break;
                } else {
                    check(action);
                }
            }
        } catch (IOException e) {
            logger.error("Cannot get streams", e);
        } finally {
            streamWriter.close();
            try {
                streamReader.close();
            } catch (IOException e) {
                logger.error("Cannot close InputStream", e);
            }
        }
    }

    private void check(Action action) throws IOException {
        String command = action.getAction().toLowerCase();
        if ("writeAll".equals(command)) {
            writeAll(contributions);
        } else if ("sum".equals(command)) {
            write(String.valueOf(service.sum()));
        } else if ("count".equals(command)) {
            write(String.valueOf(contributions.size()));
        } else if ("info account".equals(command)) {
            writeOne(service.getContributionByAccountId(action.getParam()));
        } else if ("info depositor".equals(command)) {
            writeAll(service.getContributionsByDepositor(action.getParam()));
        } else if ("show type".equals(command)) {
            writeAll(service.getContributionsByType(action.getParam()));
        } else if ("show bank".equals(command)) {
            writeAll(service.getContributionsByBank(action.getParam()));
        } else if ("add".equals(command)) {
            try {
                Contribution contribution = gson.fromJson(action.getParam(), Contribution.class);
                write(service.add(contribution) ? "OK" : "NOT OK");
            } catch (ServerException e) {
                e.printStackTrace();
                write(e.getMessage());
            }
        } else if ("delete".equals(command)) {
            write(String.valueOf(service.delete(action.getParam())));
        }
    }

    private void write(String message) {
        streamWriter.println(message);
        streamWriter.flush();
    }

    private void writeAll(List<Contribution> contributions) throws IOException {
        write(gson.toJson(contributions));
    }

    private void writeOne(Contribution contribution) {
        write(gson.toJson(contribution));
    }
}
