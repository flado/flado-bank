package au.com.suncorp.fladobank.data;

import au.com.suncorp.AppConfig;
import au.com.suncorp.fladobank.data.model.Account;
import au.com.suncorp.fladobank.data.model.Customer;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.time.LocalDate;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class})
public class InMemoryBankDataSourceTest {

    @Autowired
    private InMemoryBankDataSource dataSource;

    private Customer firstCustomer;

    @Before
    public void init() {
        if (dataSource.getNoOfAccounts() > 0) {
            dataSource.reset();
        }
        this.firstCustomer = newCustomer();
    }

    private Customer newCustomer() {
        return
                new Customer("Florin", "Adochiei", LocalDate.of(1978, 10, 7));
    }

    @Test
    public void testOpenAccount() {
        //new account - new customer (1)
        Account newAccount = dataSource.createAccount(firstCustomer, Account.AccountType.DEPOSIT);
        Account acc = dataSource.getAccount(newAccount.getId());
        assertTrue(dataSource.getNoOfAccounts() == 1);
        assertTrue(dataSource.getNoOfCustomers() == 1);
        assertNotNull(acc);
        assertEquals(BigDecimal.ZERO, acc.getBalance() );
        assertTrue(acc.getTransactions().size() == 0);
        assertTrue(acc.getCreationDate().toLocalDate().equals(LocalDate.now()));
        assertEquals(Account.AccountType.DEPOSIT, acc.getType());

        //new account - existing customer (1)
        newAccount = dataSource.createAccount(firstCustomer, Account.AccountType.SAVINGS);
        acc = dataSource.getAccount(newAccount.getId());
        assertTrue(dataSource.getNoOfAccounts() == 2);
        assertTrue(dataSource.getNoOfCustomers() == 1);
        assertNotNull(acc);
        assertEquals(BigDecimal.ZERO, acc.getBalance() );
        assertTrue(acc.getTransactions().size() == 0);
        assertTrue(acc.getCreationDate().toLocalDate().equals(LocalDate.now()));
        assertEquals(Account.AccountType.SAVINGS, acc.getType());

        //new account - new customer (2)
        newAccount = dataSource.createAccount(newCustomer(), Account.AccountType.DEPOSIT);
        acc = dataSource.getAccount(newAccount.getId());
        assertTrue(dataSource.getNoOfAccounts() == 3);
        assertTrue(dataSource.getNoOfCustomers() == 2);
        assertNotNull(acc);
        assertEquals(BigDecimal.ZERO, acc.getBalance() );
        assertTrue(acc.getTransactions().size() == 0);
        assertTrue(acc.getCreationDate().toLocalDate().equals(LocalDate.now()));
        assertEquals(Account.AccountType.DEPOSIT, acc.getType());
    }




}
