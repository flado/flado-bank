package au.com.suncorp.fladobank.service.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Simple response bean to be used as response object in external services. This way we hide the internal model details of `model.Account` entity
 */
public final class OpenAccountResponse {

    private final Long accountNumber;
    private final Long customerId;
    private final BigDecimal balance;
    private final LocalDateTime creationDate;

    public OpenAccountResponse(Long accountNumber, BigDecimal balance, LocalDateTime creationDate, Long customerId) {
        if (Objects.isNull(accountNumber) || Objects.isNull(balance) || Objects.isNull(creationDate) || Objects.isNull(customerId)) {
            throw new IllegalArgumentException();
        }
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.creationDate = creationDate;
        this.customerId = customerId;
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

    public Long getCustomerId() {
        return customerId;
    }
}
