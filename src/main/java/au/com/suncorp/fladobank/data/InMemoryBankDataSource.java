package au.com.suncorp.fladobank.data;

import au.com.suncorp.fladobank.data.model.Account;
import au.com.suncorp.fladobank.data.model.Customer;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An in-memory datastore implementation for the bank accounts storage. <br>
 */
@Component
public class InMemoryBankDataSource implements BankDataSource {

    /**
     * The accounts store to keep account details available for fast retrieval
     */
    private final Map<Long, Account> accountsStore = new ConcurrentHashMap<>();

    @Override
    public Account createAccount(Customer customer, Account.AccountType type) {
        Account account = new Account(customer, type);
        accountsStore.put(account.getId(), account);
        return account;
    }

    @Override
    public Account getAccount(Long accountId) {
        return accountsStore.get(accountId);
    }

    /**
     * To be used in unit tests only
     * @return total number of accounts
     */
    int getNoOfAccounts() {
        return this.accountsStore.size();
    }

    /**
     * Clears te datastore. To be used in testing only.
     */
    void reset() {
        this.accountsStore.clear();
    }
}
