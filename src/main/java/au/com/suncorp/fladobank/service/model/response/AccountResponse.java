package au.com.suncorp.fladobank.service.model.response;

import au.com.suncorp.fladobank.data.model.Account;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Simple bean to be used as a response object in external services.
 */
public final class AccountResponse {

    private final Long accountNumber;
    private final BigDecimal balance;
    private final LocalDateTime creationDate;
    private final String accountType;
    private final String firstName;
    private final String lastName;
    private final LocalDate dob;

    public AccountResponse(Account account) {
        if (Objects.isNull(account) ) {
            throw new IllegalArgumentException();
        }
        this.accountNumber = account.getId();
        this.balance = account.getBalance();
        this.creationDate = account.getCreationDate();
        this.accountType = account.getType().toString();
        this.firstName = account.getCustomer().getFirstName();
        this.lastName = account.getCustomer().getLastName();
        this.dob = account.getCustomer().getDob();

    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Long getAccountNumber() {
        return accountNumber;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDate getDob() {
        return dob;
    }
}
