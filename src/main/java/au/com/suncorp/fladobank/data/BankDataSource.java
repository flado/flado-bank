package au.com.suncorp.fladobank.data;

import au.com.suncorp.fladobank.data.model.Account;
import au.com.suncorp.fladobank.data.model.Customer;

/**
 * Basic datastore interface to support the FladoBank operations.
 */
public interface BankDataSource {

    /**
     * Create a new account for the specified customer
     * @param customer the customer
     * @param type the account type
     * @return new account as a reference from the datastore
     */
    Account createAccount(Customer customer, Account.AccountType type);

    /**
     * Retrieve all the account details
     *
     * @param accountId
     * @return existing account or null if account is not found
     */
    Account getAccount(Long accountId);

    /**
     * retrive an existing customer
     * @param customerId
     * @return existing customer or null if customer is not found
     */
    Customer getCustomer(Long customerId);

}
