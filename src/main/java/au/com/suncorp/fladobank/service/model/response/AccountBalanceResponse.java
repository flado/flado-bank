package au.com.suncorp.fladobank.service.model.response;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Simple bean to be used as a response object in external services. This way we hide the internal model details of `model.Account` internal entity.
 */
public final class AccountBalanceResponse {

    private final Long accountNumber;
    private final BigDecimal balance;

    public AccountBalanceResponse(Long accountNumber, BigDecimal balance) {
        if (Objects.isNull(accountNumber) || Objects.isNull(balance)) {
            throw new IllegalArgumentException();
        }
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Long getAccountNumber() {
        return accountNumber;
    }

}
