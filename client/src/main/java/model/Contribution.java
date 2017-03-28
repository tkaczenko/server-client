package model;

import java.math.BigDecimal;

/**
 * Created by tkaczenko on 26.03.17.
 */
public class Contribution {
    private String name;
    private String country;
    private ContributionType type;
    private String depositor;
    private String accountId;
    private int amountOnDeposit;
    private double profitability;
    private int timeConstraints;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public ContributionType getType() {
        return type;
    }

    public void setType(ContributionType type) {
        this.type = type;
    }

    public String getDepositor() {
        return depositor;
    }

    public void setDepositor(String depositor) {
        this.depositor = depositor;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public int getAmountOnDeposit() {
        return amountOnDeposit;
    }

    public void setAmountOnDeposit(int amountOnDeposit) {
        this.amountOnDeposit = amountOnDeposit;
    }

    public double getProfitability() {
        return profitability;
    }

    public void setProfitability(double profitability) {
        this.profitability = profitability;
    }

    public int getTimeConstraints() {
        return timeConstraints;
    }

    public void setTimeConstraints(int timeConstraints) {
        this.timeConstraints = timeConstraints;
    }

    @Override
    public String toString() {
        return "[@" + super.toString() + "]" + "{" +
                "name=" + name + ", " +
                "country=" + country + ", " +
                "type=" + type.getCode() + ", " +
                "depositor=" + depositor + ", " +
                "accountId=" + accountId + ", " +
                "amountOnDeposit=" + amountOnDeposit + ", " +
                "profitability=" + profitability + ", " +
                "timeConstraints=" + timeConstraints + "}";
    }
}
