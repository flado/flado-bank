package au.com.suncorp.fladobank.service.model.response;

import java.util.Objects;

/**
 * Simple bean to be used as a money transfer response object in external services
 */
public final class AccountTransferResponse {

    private final AccountResponse fromAccount;
    private final AccountResponse toAccount;


    public AccountTransferResponse(AccountResponse fromAccount, AccountResponse toAccount) {
        if (Objects.isNull(fromAccount) || Objects.isNull(toAccount)) {
            throw new IllegalArgumentException();
        }
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
    }

    public AccountResponse getToAccount() {
        return toAccount;
    }

    public AccountResponse getFromAccount() {
        return fromAccount;
    }
}
