package au.com.suncorp.fladobank.service.model.response;

import au.com.suncorp.fladobank.data.model.Transaction;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Bean to be used in service facade to store transaction details.
 */
public final class TransactionResponse {

    private final Long txnId;
    private final Long fromAccountId;
    private final Long toAccountId;
    private final BigDecimal amount;
    private final LocalDateTime txnDate;
    private final String txnType;

    public TransactionResponse(Transaction txn) {
        if (Objects.isNull(txn)) {
            throw new IllegalArgumentException();
        }
        this.txnId = txn.getId();
        this.fromAccountId = txn.getFromAccount().isPresent() ? txn.getFromAccount().get().getId() : null;
        this.toAccountId = txn.getToAccount().isPresent() ? txn.getToAccount().get().getId() : null;
        this.amount = txn.getAmount();
        this.txnDate = txn.getDate();
        this.txnType = txn.getType().toString();
    }

    public Long getTxnId() {
        return txnId;
    }

    public Long getFromAccountId() {
        return fromAccountId;
    }

    public Long getToAccountId() {
        return toAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getTxnDate() {
        return txnDate;
    }

    public String getTxnType() {
        return txnType;
    }
}
