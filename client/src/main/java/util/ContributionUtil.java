package util;

import model.Action;
import model.Contribution;
import model.ContributionType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ContributionUtil {
    public static Contribution parseContribution(Map<String, String> map) {
        Contribution contribution = new Contribution();
        String value = map.get("name");
        contribution.setName(value);
        value = map.get("country");
        contribution.setCountry(value);
        value = map.get("type");
        contribution.setType(value != null ? ContributionType.get(Integer.parseInt(value)) : ContributionType.get(0));
        contribution.setDepositor(map.get("depositor"));
        value = map.get("accountId");
        if (value == null) {
            return null;
        }
        contribution.setAccountId(value);
        value = map.get("amountOnDeposit");
        contribution.setAmountOnDeposit(value != null ? Integer.parseInt(value) : 0);
        value = map.get("profitability");
        contribution.setProfitability(value != null ? Double.parseDouble(value) : 0);
        value = map.get("timeConstraints");
        contribution.setTimeConstraints(value != null ? Integer.parseInt(value) : 0);
        return contribution;
    }

    public static Action getAction(String command, String actionType) {
        Action action = new Action();
        action.setAction(actionType);
        if (command != null) {
            action.setParam(getParam(command));
        }
        return action;
    }

    private static String getParam(String command) {
        List<String> words = ContributionUtil.splitWords(command);
        if (words.size() < 3) {
            return null;
        }
        return words.get(2);
    }


    public static List<String> splitWords(String str) {
        return Arrays.stream(str.trim().split("\\s"))
                .map(String::trim)
                .filter(word -> word.length() > 0)
                .collect(Collectors.toList());
    }
}
