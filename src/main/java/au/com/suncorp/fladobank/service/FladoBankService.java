package au.com.suncorp.fladobank.service;

import au.com.suncorp.fladobank.service.error.AccountNotFoundException;
import au.com.suncorp.fladobank.service.error.InsufficientFundsException;
import au.com.suncorp.fladobank.service.model.request.OpenAccountRequest;
import au.com.suncorp.fladobank.service.model.response.AccountResponse;
import au.com.suncorp.fladobank.service.model.response.AccountTransferResponse;
import au.com.suncorp.fladobank.service.model.response.TransactionResponse;
import java.math.BigDecimal;
import java.util.List;

/**
 * Operations available @ FladoBank.
 * This interface is a facade for external parties to interact with the bank.
 */
public interface FladoBankService {

    /**
     * Open a new bank account
     *
     * @param openAccountRequest account details
     * @return account number
     */
    Long openAccount(OpenAccountRequest openAccountRequest);

    /**
     * Get details of a bank account
     *
     * @param accountNumber bank account number
     * @return account details
     * @throws AccountNotFoundException if account is not found
     */
    AccountResponse getAccount(Long accountNumber) throws AccountNotFoundException;

    /**
     * Deposit into a bank account
     *
     * @param accountNumber bank account number to be credited
     * @param amount amount to be credited into account
     * @return transaction Id
     * @throws AccountNotFoundException if account is not found
     */
    Long deposit(Long accountNumber, BigDecimal amount) throws AccountNotFoundException;

    /**
     * Withdraw an account
     *
     * @param accountNumber bank account number to debit
     * @param amount amount to be debited from account
     * @return transaction id
     * @throws AccountNotFoundException if account is not found
     * @throws InsufficientFundsException if account does not have sufficient funds to withdraw
     */
    Long withdraw(Long accountNumber, BigDecimal amount) throws AccountNotFoundException, InsufficientFundsException;

    /**
     * Transfer money between two internal bank accounts
     * @param fromAccount source bank account to be debited
     * @param toAccount destination bank account to be credited
     * @return fromAccount transaction id
     * @throws AccountNotFoundException if account is not found
     * @throws InsufficientFundsException if account does not have sufficient funds for transfer
     */
    Long transfer(Long fromAccount, Long toAccount, BigDecimal amount) throws AccountNotFoundException, InsufficientFundsException;

    /**
     * List of a bank account transactions
     *
     * @param accountNumber the bank account details
     * @return list of registered transactions for {accountNumber}
     * @throws AccountNotFoundException if account is not found
     */
    List<TransactionResponse> getTransactions(Long accountNumber) throws AccountNotFoundException;
}
