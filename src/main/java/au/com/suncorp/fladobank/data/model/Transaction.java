package au.com.suncorp.fladobank.data.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * A transaction is a money transfer operation on a single bank account or between two accounts.<br>
 * This entity is immutable so that transactions can not be modified after creation.
 */
public final class Transaction extends BaseEntity{

    public enum TransactionType {
        CREDIT, DEBIT
    }

    private final BigDecimal amount;

    private final Optional<Account> fromAccount;

    private final Optional<Account> toAccount;

    private final LocalDateTime date;

    private final Transaction.TransactionType type;

    public Transaction(BigDecimal amount, Transaction.TransactionType type, Optional<Account> fromAccount, Optional<Account> toAccount) {
        if (Objects.isNull(amount) || Objects.isNull(type) || ( !fromAccount.isPresent() && !toAccount.isPresent())) {
            throw new IllegalArgumentException();
        }
        this.amount = amount;
        this.type = type;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.date = LocalDateTime.now();
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Optional<Account> getFromAccount() {
        return fromAccount;
    }

    public Optional<Account> getToAccount() {
        return toAccount;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Transaction.TransactionType getType() {
        return type;
    }
}