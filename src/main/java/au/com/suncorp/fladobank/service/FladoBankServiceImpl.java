package au.com.suncorp.fladobank.service;

import au.com.suncorp.fladobank.data.BankDataSource;
import au.com.suncorp.fladobank.data.model.Account;
import au.com.suncorp.fladobank.data.model.Customer;
import au.com.suncorp.fladobank.service.error.AccountNotFoundException;
import au.com.suncorp.fladobank.service.error.CustomerNotFoundException;
import au.com.suncorp.fladobank.service.error.InsufficientFundsException;
import au.com.suncorp.fladobank.service.model.request.OpenAccountRequest;
import au.com.suncorp.fladobank.service.model.response.AccountBalanceResponse;
import au.com.suncorp.fladobank.service.model.response.AccountTransferResponse;
import au.com.suncorp.fladobank.service.model.response.OpenAccountResponse;
import au.com.suncorp.fladobank.service.model.response.TransactionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * FladoBank basic services implementation.<br>
 *
 * <b>Assumptions:</b>
 *     <ul>
 *          <li> all accounts are internal bank accounts </li>
 *          <li> all customers are individuals with basic properties </li>
 *          <li> default balance on a newly opened account is ZERO </li>
 *     </ul>
 */
@Service
public class FladoBankServiceImpl implements FladoBankService {

    @Autowired
    private BankDataSource bankDataSource;

    /**
     * Open a new account for an existing customer
     *
     * @param customer
     * @param type
     * @return
     */
    private OpenAccountResponse openAccount(Customer customer, Account.AccountType type) {
        Account acc = bankDataSource.createAccount(customer, type);
        return
                new OpenAccountResponse(acc.getId(), acc.getBalance(), acc.getCreationDate(), acc.getCustomer().getId());
    }

    @Override
    public OpenAccountResponse openAccount(OpenAccountRequest openAccountRequest) {
        if (Objects.isNull(openAccountRequest)) {
            throw new IllegalArgumentException();
        }
        Customer customer = new Customer(openAccountRequest.getFirstName(), openAccountRequest.getLastName(), openAccountRequest.getDob());
        return
                openAccount(customer, Account.AccountType.valueOf(openAccountRequest.getAccountType()));
    }

    @Override
    public OpenAccountResponse openAccount(Long customerId, String accountType) throws CustomerNotFoundException {
        if (Objects.isNull(customerId) || Objects.isNull(accountType)) {
            throw new IllegalArgumentException();
        }
        Customer customer = bankDataSource.getCustomer(customerId);
        if (customer == null) {
            throw new CustomerNotFoundException();
        }
        return
                openAccount(customer, Account.AccountType.valueOf(accountType));
    }

    @Override
    public AccountBalanceResponse getBalance(Long accountNumber) throws AccountNotFoundException {
        if (Objects.isNull(accountNumber)) {
            throw new IllegalArgumentException();
        }
        Account account = bankDataSource.getAccount(accountNumber);
        if (Objects.isNull(account)) {
            throw new AccountNotFoundException();
        }
        return
                new AccountBalanceResponse(account.getId(), account.getBalance());
    }

    @Override
    public AccountBalanceResponse deposit(Long accountNumber, BigDecimal amount) throws AccountNotFoundException {
        if (Objects.isNull(accountNumber) || Objects.isNull(amount)) {
            throw new IllegalArgumentException();
        }
        Account account = bankDataSource.getAccount(accountNumber);
        if (Objects.isNull(account)) {
            throw new AccountNotFoundException();
        }
        account.deposit(amount, Optional.empty());
        return
                new AccountBalanceResponse(account.getId(), account.getBalance());
    }

    @Override
    public AccountBalanceResponse withdraw(Long accountNumber, BigDecimal amount) throws AccountNotFoundException, InsufficientFundsException {
        if (Objects.isNull(accountNumber) || Objects.isNull(amount)) {
            throw new IllegalArgumentException();
        }
        Account account = bankDataSource.getAccount(accountNumber);
        if (Objects.isNull(account)) {
            throw new AccountNotFoundException();
        }
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException();
        }
        account.widthdraw(amount, Optional.empty());
        return
                new AccountBalanceResponse(account.getId(), account.getBalance());
    }

    @Override
    public AccountTransferResponse transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) throws AccountNotFoundException, InsufficientFundsException {
        if (Objects.isNull(fromAccountId) || Objects.isNull(toAccountId) || Objects.isNull(amount)) {
            throw new IllegalArgumentException();
        }
        Account fromAccount = bankDataSource.getAccount(fromAccountId);
        Account toAccount = bankDataSource.getAccount(toAccountId);
        if (Objects.isNull(fromAccount) || Objects.isNull(toAccount)) {
            throw new AccountNotFoundException();
        }
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException();
        }
        fromAccount.transfer(amount, toAccount);
        return
                new AccountTransferResponse(
                        new AccountBalanceResponse(fromAccount.getId(), fromAccount.getBalance()),
                        new AccountBalanceResponse(toAccount.getId(), toAccount.getBalance())
                );
    }

    @Override
    public List<TransactionResponse> getTransations(Long accountNumber) throws AccountNotFoundException {
        if (Objects.isNull(accountNumber)) {
            throw new IllegalArgumentException();
        }
        Account account = bankDataSource.getAccount(accountNumber);
        if (Objects.isNull(account)) {
            throw new AccountNotFoundException();
        }
        return
                account.getTransactions().stream()
                        .map(txn -> new TransactionResponse(txn))
                        .collect(Collectors.toList());
    }
}
