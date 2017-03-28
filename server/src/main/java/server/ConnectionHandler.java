package server;

import model.Action;
import model.Contribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static server.Server.contributions;
import static server.Server.gson;

/**
 * Created by tkaczenko on 26.03.17.
 */
class ConnectionHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionHandler.class);

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
                    logger.error("Cannot read command", e);
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
        if ("list".equals(command)) {
            list();
        } else if ("sum".equals(command)) {
            sum();
        } else if ("count".equals(command)) {
            count();
        } else if ("info account".equals(command)) {
            getContributionByAccountId(action.getParam());
        } else if ("info depositor".equals(command)) {
            getContributionsByDepositor(action.getParam());
        } else if ("show type".equals(command)) {
            getContributionsByType(action.getParam());
        } else if ("show bank".equals(command)) {
            getContributionsByBank(action.getParam());
        } else if ("add".equals(command)) {
            add(gson.fromJson(action.getParam(), Contribution.class));
        } else if ("delete".equals(command)) {
            delete(action.getParam());
        }
    }

    private void delete(String accountId) {
        streamWriter.println(contributions.removeIf(contribution -> accountId.equals(contribution.getAccountId())));
        streamWriter.flush();
    }

    private void add(Contribution contribution) {
        List<Contribution> list = Collections.synchronizedList(contributions);
        synchronized (list) {
            Contribution found = list.stream()
                    .filter(contribution1 -> contribution.getAccountId().equals(contribution1.getAccountId()))
                    .findFirst()
                    .orElse(null);
            if (found != null) {
                streamWriter.println("Found that id");
                streamWriter.flush();
                return;
            }
            if (contribution.getAmountOnDeposit() <= 0 ||
                    contribution.getProfitability() <= 0 ||
                    contribution.getTimeConstraints() <= 0) {
                streamWriter.println("Required params cannot be null");
                streamWriter.flush();
                return;
            }
        }
        streamWriter.println(contributions.add(contribution) ? "OK" : "NOT OK");
        streamWriter.flush();
    }

    private void list() throws IOException {
        streamWriter.println(gson.toJson(contributions));
        streamWriter.flush();
    }

    private void sum() throws IOException {
        List<Contribution> list = Collections.synchronizedList(contributions);
        synchronized (list) {
            long sum = list.stream()
                    .mapToInt(Contribution::getAmountOnDeposit)
                    .sum();
            streamWriter.println(sum);
            streamWriter.flush();
        }
    }

    private void count() throws IOException {
        streamWriter.println(contributions.size());
        streamWriter.flush();
    }

    private void getContributionByAccountId(String accountID) throws IOException {
        List<Contribution> list = Collections.synchronizedList(contributions);
        synchronized (list) {
            Contribution res = list.stream()
                    .filter(contribution -> accountID.equals(contribution.getAccountId()))
                    .findFirst()
                    .orElse(null);
            streamWriter.println(gson.toJson(res));
            streamWriter.flush();
        }
    }

    private void getContributionsByDepositor(String depositor) throws IOException {
        List<Contribution> list = Collections.synchronizedList(contributions);
        synchronized (list) {
            List<Contribution> contributions = list.stream()
                    .filter(contribution -> depositor.equals(contribution.getDepositor()))
                    .collect(Collectors.toList());
            streamWriter.println(gson.toJson(contributions));
            streamWriter.flush();
        }
    }

    private void getContributionsByType(String type) throws IOException {
        List<Contribution> list = Collections.synchronizedList(contributions);
        synchronized (list) {
            List<Contribution> contributions = list.stream()
                    .filter(contribution -> type.equals(contribution.getType().getDescription()))
                    .collect(Collectors.toList());

            streamWriter.println(gson.toJson(contributions));
            streamWriter.flush();
        }
    }

    private void getContributionsByBank(String bank) {
        List<Contribution> list = Collections.synchronizedList(contributions);
        synchronized (list) {
            List<Contribution> contributions = list.stream()
                    .filter(contribution -> bank.equals(contribution.getName()))
                    .collect(Collectors.toList());
            streamWriter.println(gson.toJson(contributions));
            streamWriter.flush();
        }
    }
}
