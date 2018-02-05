package au.com.suncorp.fladobank.service.model.response;

import au.com.suncorp.fladobank.service.model.response.AccountBalanceResponse;

import java.util.Objects;

/**
 * Simple bean to be used as a money transfer response object in external services
 */
public final class AccountTransferResponse {

    private final AccountBalanceResponse fromAccount;
    private final AccountBalanceResponse toAccount;


    public AccountTransferResponse(AccountBalanceResponse fromAccount, AccountBalanceResponse toAccount) {
        if (Objects.isNull(fromAccount) || Objects.isNull(toAccount)) {
            throw new IllegalArgumentException();
        }
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
    }

    public AccountBalanceResponse getToAccount() {
        return toAccount;
    }

    public AccountBalanceResponse getFromAccount() {
        return fromAccount;
    }
}
