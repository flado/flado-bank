package au.com.suncorp.fladobank.data.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The bank account entity. <br>
 * This is a thread-safe class using {@code ReentrantLock} to handle synchronization when internal state is mutated. <br>
 */

public final class Account extends BaseEntity {

    public enum AccountType {
        DEPOSIT, SAVINGS
    }

    /**
     * Account lock to be used in synchronizing the account operations
     */
    private final Lock lock = new ReentrantLock();

    /**
     * Default balance on account opening before any transaction has occurred
     */
    private BigDecimal balance = BigDecimal.ZERO;

    /**
     * Account creation date
     */
    private final LocalDateTime creationDate;

    /**
     * Immutable customer reference
     */
    private final Customer customer;

    /**
     * Immutable account type
     */
    private final AccountType type;

    /**
     * Immutable reference for list of transactions performed on this account
     */
    private final List<Transaction> transactions = new ArrayList<>();

    /**
     * Create a new account instance for provided customer
     *
     * @param customer old or existing customer - an existing customer must have the Id value
     * @param type
     */
    public Account(Customer customer, AccountType type) {
        super();
        this.customer = customer;
        this.type = type;
        this.creationDate = LocalDateTime.now();
    }

    /**
     * Retrieve the customer associated with this account
     * @return
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Get a list of transactions for this account.
     *
     * @return
     */
    public List<Transaction> getTransactions() {
        lock.lock();
        try {
            return transactions;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Retrieve  the account type
     *
     * @return
     */
    public AccountType getType() {
        return type;
    }

    /**
     * Retrieve the account balance
     *
     * @return
     */
    public BigDecimal getBalance() {
        lock.lock();
        try {
            return balance;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Deposit money into this account.
     * @param amount
     * @param fromAccount optional bank account is deposit is done from another account
     */
    public void deposit(BigDecimal amount, Optional<Account> fromAccount) {
        if (Objects.isNull(amount) || Objects.isNull(fromAccount)) {
            throw new IllegalArgumentException();
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) { //do not handle negative amounts
            return;
        }
        Transaction creditTxn = new Transaction(amount, Transaction.TransactionType.CREDIT, fromAccount, Optional.of(this));
        lock.lock();
        try {
            this.transactions.add(creditTxn);
            this.balance = balance.add(amount);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Withdraw money from this account.
     * @param amount
     * @param toAccount optional bank account if money are withdrawn in another account
     */
    public void widthdraw(BigDecimal amount, Optional<Account> toAccount) {
        if (Objects.isNull(amount) || Objects.isNull(toAccount)) {
            throw new IllegalArgumentException();
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) { //do not handle negative amounts
            return;
        }
        Transaction debitTxn = new Transaction(amount, Transaction.TransactionType.DEBIT, Optional.of(this), toAccount);
        lock.lock();
        try {
            if (this.balance.compareTo(amount) < 0) { //do not handle if insufficient funds
                return;
            }
            this.transactions.add(debitTxn);
            this.balance = balance.subtract(amount);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Transfer an amount between this account and the a destination account.
     *
     * @param amount amount to be transferred
     * @param toAccount destination account
     */
    public void transfer(BigDecimal amount, Account toAccount) {
        if (Objects.isNull(amount) || Objects.isNull(toAccount)) {
            throw new IllegalArgumentException();
        }
        //lock accounts always in same order to avoid deadlock (order by account.id)
        Lock firstLock = this.getId() < toAccount.getId() ? this.lock : toAccount.lock;
        Lock secondLock = this.getId() > toAccount.getId() ? toAccount.lock : this.lock;

        firstLock.lock();
        secondLock.lock();
        try {
            this.widthdraw(amount, Optional.of(toAccount));
            toAccount.deposit(amount, Optional.of(this));
        } finally {
            secondLock.unlock();
            firstLock.unlock();
        }
    }

    /**
     * get account creation date
     * @return account creation date
     */
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

}
