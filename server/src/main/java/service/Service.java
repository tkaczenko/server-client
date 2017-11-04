package service;

import model.Contribution;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static database.Contributions.contributions;

public class Service {
    public boolean delete(String accountId) {
        return contributions.removeIf(contribution -> accountId.equals(contribution.getAccountId()));
    }

    public boolean add(Contribution contribution) throws ServerException {
        Contribution found = contributions.stream()
                .filter(contribution1 -> contribution.getAccountId().equals(contribution1.getAccountId()))
                .findFirst()
                .orElse(null);
        if (found != null) {
            throw new ServerException("Found that id");
        }
        if (contribution.getAmountOnDeposit() <= 0 ||
                contribution.getProfitability() <= 0 ||
                contribution.getTimeConstraints() <= 0) {
            throw new ServerException("Required params cannot be null");
        }
        return contributions.add(contribution);
    }

    public int sum() throws IOException {
        return contributions.stream()
                .mapToInt(Contribution::getAmountOnDeposit)
                .sum();
    }

    public Contribution getContributionByAccountId(String accountID) throws IOException {
        return contributions.stream()
                .filter(contribution -> accountID.equals(contribution.getAccountId()))
                .findFirst()
                .orElse(null);
    }

    public List<Contribution> getContributionsByDepositor(String depositor) throws IOException {
        return contributions.stream()
                .filter(contribution -> depositor.equals(contribution.getDepositor()))
                .collect(Collectors.toList());
    }

    public List<Contribution> getContributionsByType(String type) throws IOException {
        return contributions.stream()
                .filter(contribution -> type.equals(contribution.getType().getDescription()))
                .collect(Collectors.toList());
    }

    public List<Contribution> getContributionsByBank(String bank) throws IOException {
        return contributions.stream()
                .filter(contribution -> bank.equals(contribution.getName()))
                .collect(Collectors.toList());
    }
}
