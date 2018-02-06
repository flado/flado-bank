package au.com.suncorp.fladobank.service;

import au.com.suncorp.AppConfig;
import au.com.suncorp.fladobank.data.model.Account;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

import au.com.suncorp.fladobank.service.error.AccountNotFoundException;
import au.com.suncorp.fladobank.service.error.InsufficientFundsException;
import au.com.suncorp.fladobank.service.model.request.OpenAccountRequest;
import au.com.suncorp.fladobank.service.model.response.AccountResponse;
import au.com.suncorp.fladobank.service.model.response.AccountTransferResponse;
import au.com.suncorp.fladobank.service.model.response.TransactionResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class})
public class FladoBankServiceImplTest {

    @Autowired
    private FladoBankService fladoService;

    private OpenAccountRequest newOpenAccountRequest() {
        return new OpenAccountRequest("Florin", "Adochiei", LocalDate.of(1978, 10, 7), Account.AccountType.SAVINGS.toString());
    }


    //open account

    @Test
    public void testOpenAccount() {
        //open account - new customer
        Long accountNumber = fladoService.openAccount(newOpenAccountRequest());
        assertNotNull(accountNumber);
        assertTrue(accountNumber > 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalArgumentWhenOpenAccountWithWrongType() {
        OpenAccountRequest request = new OpenAccountRequest("Florin", "Adochiei", LocalDate.of(1978, 10, 7), "UNKNOWN");
        fladoService.openAccount(request);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalArgumentWhenOpenAccountWithMissingDOB() {
        OpenAccountRequest request = new OpenAccountRequest("Florin", "Adochiei", null, "UNKNOWN");
        fladoService.openAccount(request);
    }

    // deposit

    @Test
    public void testDesposit() throws AccountNotFoundException {
        Long accountNumber = fladoService.openAccount(newOpenAccountRequest());
        fladoService.deposit(accountNumber, BigDecimal.valueOf(1500));
        fladoService.deposit(accountNumber, BigDecimal.valueOf(200));
        fladoService.deposit(accountNumber, BigDecimal.valueOf(300));
        assertEquals(BigDecimal.valueOf(2000), fladoService.getAccount(accountNumber).getBalance());
    }

    @Test(expected = AccountNotFoundException.class)
    public void testAccountNotFoundWhenDeposit() throws AccountNotFoundException {
        fladoService.deposit(Long.MAX_VALUE, BigDecimal.valueOf(123));
    }

    // transfer

    @Test(expected = AccountNotFoundException.class)
    public void testAccountNotFoundWhenTransfer() throws AccountNotFoundException, InsufficientFundsException {
        Long accNumber = fladoService.openAccount(newOpenAccountRequest());
        fladoService.transfer(accNumber, Long.MAX_VALUE, BigDecimal.valueOf(500));
    }

    @Test(expected = InsufficientFundsException.class)
    public void testInsufficientFundsExceptionWhenTransfer() throws AccountNotFoundException, InsufficientFundsException {
        Long fromAccountNumber = fladoService.openAccount(newOpenAccountRequest());
        Long toAccountNumber = fladoService.openAccount(newOpenAccountRequest());
        fladoService.transfer(fromAccountNumber, toAccountNumber, BigDecimal.valueOf(1));
    }

    @Test
    public void testTransfer() throws AccountNotFoundException, InsufficientFundsException {
        Long fromAccountNumber = fladoService.openAccount(newOpenAccountRequest());
        fladoService.deposit(fromAccountNumber, BigDecimal.valueOf(1500));
        Long toAccountNumber = fladoService.openAccount(newOpenAccountRequest());
        fladoService.transfer(fromAccountNumber, toAccountNumber, BigDecimal.valueOf(500));

        assertEquals(BigDecimal.valueOf(1000), fladoService.getAccount(fromAccountNumber).getBalance());
        assertEquals(BigDecimal.valueOf(500), fladoService.getAccount(toAccountNumber).getBalance());
    }

    // withdraw

    @Test(expected = AccountNotFoundException.class)
    public void testAccountNotFoundWhenWidthdraw() throws AccountNotFoundException, InsufficientFundsException {
        fladoService.withdraw(Long.MAX_VALUE, BigDecimal.valueOf(123));
    }

    @Test(expected = InsufficientFundsException.class)
    public void testInsufficientFundsExceptionWhenWidthdraw() throws AccountNotFoundException, InsufficientFundsException {
        Long accountNumber = fladoService.openAccount(newOpenAccountRequest());
        fladoService.withdraw(accountNumber, BigDecimal.valueOf(123));
    }

    @Test
    public void testWithdraw() throws AccountNotFoundException, InsufficientFundsException {
        Long accountNumber = fladoService.openAccount(newOpenAccountRequest());
        fladoService.deposit(accountNumber, BigDecimal.valueOf(1500));
        fladoService.withdraw(accountNumber, BigDecimal.valueOf(200));
        fladoService.withdraw(accountNumber, BigDecimal.valueOf(300));
        assertEquals(BigDecimal.valueOf(1000), fladoService.getAccount(accountNumber).getBalance());
    }

    // transactions

    @Test(expected = AccountNotFoundException.class)
    public void testAccountNotFoundExceptionWhenRetrievingTransactions() throws AccountNotFoundException {
        fladoService.getTransactions(Long.MAX_VALUE);
    }

    @Test
    public void testTransactions() throws AccountNotFoundException, InsufficientFundsException {
        Long sourceAccountNumber = fladoService.openAccount(newOpenAccountRequest());
        fladoService.deposit(sourceAccountNumber, BigDecimal.valueOf(1500));
        fladoService.withdraw(sourceAccountNumber, BigDecimal.valueOf(200));

        List<TransactionResponse> transactionResponses = fladoService.getTransactions(sourceAccountNumber);
        assertEquals(2, transactionResponses.size());

        // credit transaction (deposit)
        assertEquals("CREDIT", transactionResponses.get(0).getTxnType());
        assertEquals(BigDecimal.valueOf(1500), transactionResponses.get(0).getAmount());
        assertNotNull(transactionResponses.get(0).getTxnId());
        assertNotNull(transactionResponses.get(0).getTxnDate());
        assertEquals(sourceAccountNumber, transactionResponses.get(0).getToAccountId());
        assertNull(transactionResponses.get(0).getFromAccountId());

        //debit transaction (withdraw)
        assertEquals("DEBIT", transactionResponses.get(1).getTxnType());
        assertEquals(BigDecimal.valueOf(200), transactionResponses.get(1).getAmount());
        assertNotNull(transactionResponses.get(1).getTxnId());
        assertNotNull(transactionResponses.get(1).getTxnDate());
        assertEquals(sourceAccountNumber, transactionResponses.get(1).getFromAccountId());
        assertNull(transactionResponses.get(1).getToAccountId());

        Long destinationAccountNumber = fladoService.openAccount(newOpenAccountRequest());
        fladoService.transfer(sourceAccountNumber, destinationAccountNumber, BigDecimal.valueOf(300));

        List<TransactionResponse> sourceTxns = fladoService.getTransactions(sourceAccountNumber);
        assertEquals(3, sourceTxns.size());
        assertEquals("DEBIT", sourceTxns.get(2).getTxnType());
        assertEquals(BigDecimal.valueOf(300), sourceTxns.get(2).getAmount());
        assertNotNull(sourceTxns.get(2).getTxnId());
        assertNotNull(sourceTxns.get(2).getTxnDate());
        assertEquals(sourceAccountNumber, sourceTxns.get(2).getFromAccountId());
        assertEquals(destinationAccountNumber, sourceTxns.get(2).getToAccountId());

        List<TransactionResponse> destTxns = fladoService.getTransactions(destinationAccountNumber);
        assertEquals(1, destTxns.size());
        assertEquals("CREDIT", destTxns.get(0).getTxnType());
        assertEquals(BigDecimal.valueOf(300), destTxns.get(0).getAmount());
        assertNotNull(destTxns.get(0).getTxnId());
        assertNotNull(destTxns.get(0).getTxnDate());
        assertEquals(sourceAccountNumber, destTxns.get(0).getFromAccountId());
        assertEquals(destinationAccountNumber, destTxns.get(0).getToAccountId());
    }
}
