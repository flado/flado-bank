package au.com.suncorp.fladobank.data.model;

import au.com.suncorp.fladobank.data.InsufficientFundsAccountException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public class AccountTest {

    private Account account;
    private Customer customer;

    @Before
    public void init() {
        customer = new Customer("florin", "adochiei", LocalDate.of(1977, 4, 4));
        account = new Account(customer, Account.AccountType.DEPOSIT);
    }

    @Test
    public void testAccountDefaultValues() {
        assertEquals(BigDecimal.ZERO, account.getBalance());
        assertTrue(account.getTransactions().isEmpty());
        assertEquals(Account.AccountType.DEPOSIT, account.getType());
        assertEquals(customer, account.getCustomer());
        assertEquals(LocalDate.now(), account.getCreationDate().toLocalDate());
        assertNotNull(account.getId());
        assertTrue(account.getId() > 0);
    }

    @Test
    public void testDeposit() {
        Long txnId = account.deposit(BigDecimal.valueOf(100), Optional.empty());
        assertNotNull(txnId);
        assertTrue(txnId > 0);
        assertEquals(BigDecimal.valueOf(100), account.getBalance());
        assertEquals(1, account.getTransactions().size());
        assertEquals(BigDecimal.valueOf(100), account.getTransactions().get(0).getAmount());
        assertEquals(Transaction.TransactionType.CREDIT, account.getTransactions().get(0).getType());
        assertTrue(account.getTransactions().get(0).getToAccount().isPresent());
        assertTrue(account == account.getTransactions().get(0).getToAccount().get());
        assertFalse(account.getTransactions().get(0).getFromAccount().isPresent());
        assertTrue(account.getTransactions().get(0).getId() > 0);
        assertEquals(LocalDate.now(), account.getTransactions().get(0).getDate().toLocalDate());
    }

    @Test(expected = InsufficientFundsAccountException.class)
    public void testWithdrawalWhenInsufficientFunds() {
        account.widthdraw(BigDecimal.valueOf(200), Optional.empty());
    }

    @Test
    public void testWithdrawal() {
        Long depositTxnId = account.deposit(BigDecimal.valueOf(500), Optional.empty());
        assertTrue(depositTxnId > 0);

        Long withdrawTxnId = account.widthdraw(BigDecimal.valueOf(200), Optional.empty());
        assertTrue(withdrawTxnId > 0);

        assertEquals(BigDecimal.valueOf(300), account.getBalance());
        assertEquals(2, account.getTransactions().size());

        Transaction creditTxn = account.getTransactions().get(0);
        assertEquals(BigDecimal.valueOf(500), creditTxn.getAmount());
        assertEquals(Transaction.TransactionType.CREDIT, creditTxn.getType());

        Transaction debitTxn = account.getTransactions().get(1);
        assertEquals(BigDecimal.valueOf(200), debitTxn.getAmount());
        assertEquals(Transaction.TransactionType.DEBIT, debitTxn.getType());

    }

    @Test
    public void testTransfer() {
        account.deposit(BigDecimal.valueOf(15), Optional.empty());

        Account toAccount = new Account(new Customer("first", "last", LocalDate.of(1977, 4, 4)), Account.AccountType.DEPOSIT);
        account.transfer(BigDecimal.valueOf(5), toAccount);

        assertEquals(BigDecimal.valueOf(10), account.getBalance());
        assertEquals(2, account.getTransactions().size());
        Transaction txn = account.getTransactions().get(1);
        assertTrue(txn.getToAccount().isPresent());
        assertEquals(toAccount.getId(), txn.getToAccount().get().getId());
        assertEquals(BigDecimal.valueOf(5), txn.getAmount());
        assertEquals(Transaction.TransactionType.DEBIT, txn.getType());

        assertEquals(BigDecimal.valueOf(5), toAccount.getBalance());
        assertEquals(1, toAccount.getTransactions().size());
        txn = toAccount.getTransactions().get(0);
        assertTrue(txn.getFromAccount().isPresent());
        assertEquals(account.getId(), txn.getFromAccount().get().getId());
        assertEquals(BigDecimal.valueOf(5), txn.getAmount());
        assertEquals(Transaction.TransactionType.CREDIT, txn.getType());
    }
}
