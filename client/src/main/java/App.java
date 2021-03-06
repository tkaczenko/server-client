import client.Client;
import model.Action;
import model.Contribution;
import model.ContributionType;
import util.ContributionUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static service.Service.gson;

/**
 * Created by tkaczenko on 26.03.17.
 */
public class App {
    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 9000;

    private static String host = DEFAULT_HOST;
    private static int port = DEFAULT_PORT;
    private static Client client;

    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            host = args[0];
            if (args.length >= 2) {
                port = Integer.parseInt(args[1]);
            }
        }
        client = new Client(host, port);
        client.setUpConnection();
        showMenu();
        String command;
        Scanner scanner = new Scanner(System.in);
        boolean flag = true;
        while (flag) {
            System.out.print("Pls, write command: ");
            command = scanner.nextLine();
            if ("exit".equals(command)) {
                flag = false;
            } else {
                check(command);
            }
        }
        client.tearDownConnection();
    }

    private static void check(String command) {
        if ("list".equals(command)) {
            Action action = ContributionUtil.getAction(null, "list");
            List<Contribution> contributions = client.list(action);
            printContributionHeader();
            contributions.forEach(App::printContribution);
            showMenu();
        } else if ("sum".equals(command)) {
            Action action = ContributionUtil.getAction(null, "sum");
            long sum = client.sum(action);
            System.out.println("Общая сумма вкладов: " + sum);
            showMenu();
        } else if ("count".equals(command)) {
            Action action = ContributionUtil.getAction(null, "count");
            int count = client.count(action);
            System.out.println("Количество вкладов: " + count);
            showMenu();
        } else if (command.contains("info account")) {
            Action action = ContributionUtil.getAction(command, "info account");
            Contribution contribution = client.getContributionByAccountId(action);
            if (contribution != null) {
                printContributionHeader();
                printContribution(contribution);
            } else {
                System.out.println("Вклад не найден");
            }
            showMenu();
        } else if (command.contains("info depositor")) {
            Action action = ContributionUtil.getAction(command, "info depositor");
            List<Contribution> contributions = client.list(action);
            printContributionHeader();
            contributions.forEach(App::printContribution);
            showMenu();
        } else if (command.contains("show type")) {
            Action action = ContributionUtil.getAction(command, "show type");
            List<Contribution> contributions = client.list(action);
            printContributionHeader();
            contributions.forEach(App::printContribution);
            showMenu();
        } else if (command.contains("show bank")) {
            Action action = ContributionUtil.getAction(command, "show bank");
            List<Contribution> contributions = client.list(action);
            printContributionHeader();
            contributions.forEach(App::printContribution);
            showMenu();
        } else if (command.contains("add")) {
            Action action = new Action();
            action.setAction("add");
            Map<String, String> map = prepareMapOfContribution(command);
            Contribution contribution = ContributionUtil.parseContribution(map);
            if (contribution == null) {
                System.out.println("Cannot parse contribution");
                return;
            }
            action.setParam(gson.toJson(contribution));
            System.out.println(client.add(action));
            showMenu();
        } else if (command.contains("delete")) {
            Action action = ContributionUtil.getAction(command, "delete");
            System.out.println(client.delete(action));
            showMenu();
        }
    }

    private static Map<String, String> prepareMapOfContribution(String command) {
        List<String> words = ContributionUtil.splitWords(command);
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < words.size(); i++) {
            String key = words.get(i);
            String value = null;
            if (words.size() > i + 1) {
                value = words.get(i + 1);
            }
            map.put(key, value);
        }
        return map;
    }

    private static void printContributionHeader() {
        System.out.format("%20s%10s%10s%15s%10s%17s%15s%20s\n",
                "Name",
                "Country",
                "Type",
                "Depositor",
                "AccountId",
                "AmountOnDeposit",
                "Profitability",
                "TimeConstraints");
    }

    private static void printContribution(Contribution contribution) {
        ContributionType type = contribution.getType();
        System.out.format("%20s%10s%10s%15s%10s%17s%15s%20s\n",
                contribution.getName(),
                contribution.getCountry(),
                type != null ? type.getDescription() : "null",
                contribution.getDepositor(),
                contribution.getAccountId(),
                contribution.getAmountOnDeposit(),
                contribution.getProfitability(),
                contribution.getTimeConstraints()
        );
    }

    private static void showMenu() {
        System.out.println("Menu:");
        StringBuilder sb = new StringBuilder();
        sb.append("list – выдать список всех вкладов\n");
        sb.append("sum – выдать общую сумму вкладов\n");
        sb.append("count – выдать количество вкладов\n");
        sb.append("info account <account id> - информация по счету\n");
        sb.append("info depositor <depositor> - информация по имени вкладчика\n");
        sb.append("show type <type> - список всех вкладов указанного типа\n");
        sb.append("show bank <name> - список всех вкладов в указанном банке\n");
        sb.append("add <deposit info> - добавить информацию о вкладе\t\n");
        sb.append("Usage: add name {} country {} type {} depositor {} accountId {} amountOnDeposit {} profitability {} timeConstraints {}\n");
        sb.append("delete <account id>");
        System.out.println(sb.toString());
    }
}
