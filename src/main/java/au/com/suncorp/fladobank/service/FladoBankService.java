package au.com.suncorp.fladobank.service;

import au.com.suncorp.fladobank.service.error.AccountNotFoundException;
import au.com.suncorp.fladobank.service.error.CustomerNotFoundException;
import au.com.suncorp.fladobank.service.error.InsufficientFundsException;
import au.com.suncorp.fladobank.service.model.request.OpenAccountRequest;
import au.com.suncorp.fladobank.service.model.response.AccountBalanceResponse;
import au.com.suncorp.fladobank.service.model.response.AccountTransferResponse;
import au.com.suncorp.fladobank.service.model.response.OpenAccountResponse;
import au.com.suncorp.fladobank.service.model.response.TransactionResponse;
import java.math.BigDecimal;
import java.util.List;

/**
 * Operations available @ FladoBank.
 * This interface is a facade for external parties to interact with the bank.
 */
public interface FladoBankService {

    /**
     * Open a new account for a new customer
     * @param customer customer & account details
     * @return newly opened bank account details, including the account number
     */
    OpenAccountResponse openAccount(OpenAccountRequest customer);

    /**
     * Open a new account for an existing customer
     * @param customerId existing customer number
     * @param accountType the account type
     * @return newly opened bank account, including the account number
     * @throws CustomerNotFoundException if customer is not found
     */
    OpenAccountResponse openAccount(Long customerId, String accountType) throws CustomerNotFoundException;

    /**
     * Get balance of a bank account
     *
     * @param accountNumber bank account number
     * @return account balance details
     * @throws AccountNotFoundException if account is not found
     */
    AccountBalanceResponse getBalance(Long accountNumber) throws AccountNotFoundException;

    /**
     * Deposit into a bank account
     *
     * @param accountNumber bank account number to be credited
     * @param amount amount to be credited into account
     * @return updated account balance details
     * @throws AccountNotFoundException if account is not found
     */
    AccountBalanceResponse deposit(Long accountNumber, BigDecimal amount) throws AccountNotFoundException;

    /**
     * Withdraw an account
     *
     * @param accountNumber bank account number to debit
     * @param amount amount to be debited from account
     * @return updated account balance details
     * @throws AccountNotFoundException if account is not found
     * @throws InsufficientFundsException if account does not have sufficient funds to withdraw
     */
    AccountBalanceResponse withdraw(Long accountNumber, BigDecimal amount) throws AccountNotFoundException, InsufficientFundsException;

    /**
     * Transfer money between two internal bank accounts
     * @param fromAccount source bank account to be debited
     * @param toAccount destination bank account to be credited
     * @return fromAccount balance details
     * @throws AccountNotFoundException if account is not found
     * @throws InsufficientFundsException if account does not have sufficient funds for transfer
     */
    AccountTransferResponse transfer(Long fromAccount, Long toAccount, BigDecimal amount) throws AccountNotFoundException, InsufficientFundsException;

    /**
     * List of a bank account transactions
     *
     * @param accountNumber the bank account details
     * @return list of registered transactions for {accountNumber}
     * @throws AccountNotFoundException if account is not found
     */
    List<TransactionResponse> getTransations(Long accountNumber) throws AccountNotFoundException;
}
