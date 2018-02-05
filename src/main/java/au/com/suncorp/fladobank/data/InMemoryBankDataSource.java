package au.com.suncorp.fladobank.data;

import au.com.suncorp.fladobank.data.model.Account;
import au.com.suncorp.fladobank.data.model.Customer;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An in-memory datastore implementation for the bank. <br>
 * This implementation has a kind of `eventual consistency` between accounts & customers because `persistence` is done in two individual steps:
 * <ul><li>save customer</li>.
 * <li>save account</li>. <br>
 *
 * It is possible that during account creation, some threads could retrieve the customer details
 * before the account creation is finalized. This is not an issue for the basic requirements of FladoBank.
 */
@Component
public class InMemoryBankDataSource implements BankDataSource {

    /**
     * The accounts store to keep account details available for fast retrieval
     */
    private final Map<Long, Account> accountsStore = new ConcurrentHashMap<>();

    /**
     * The customers storage is used to fast retrieve individual customer details
     */
    private final Map<Long, Customer> customersStore = new ConcurrentHashMap<>();

    //TODO: chek this impl if thread-safe with 2 x steps for storage

    @Override
    public Account createAccount(Customer customer, Account.AccountType type) {
        Account account = new Account(customer, type);
        //1. save customer
        customersStore.put(customer.getId(), customer);
        //2. save account
        accountsStore.put(account.getId(), account);
        return account;
    }

    @Override
    public Account getAccount(Long accountId) {
        return accountsStore.get(accountId);
    }

    @Override
    public Customer getCustomer(Long customerId) {
        return customersStore.get(customerId);
    }

    /**
     * To be used in unit tests only
     * @return
     */
    int getNoOfAccounts() {
        return this.accountsStore.size();
    }

    /**
     * To be used in unit tests only
     * @return
     */
    int getNoOfCustomers() {
        return this.customersStore.size();
    }

    /**
     * Clears te datastore. To be used in testing only.
     */
    void reset() {
        this.accountsStore.clear();
        this.customersStore.clear();
    }
}
