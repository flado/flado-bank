package au.com.suncorp.fladobank.service;

import au.com.suncorp.AppConfig;
import au.com.suncorp.fladobank.data.model.Account;

import static org.junit.Assert.*;

import au.com.suncorp.fladobank.service.error.AccountNotFoundException;
import au.com.suncorp.fladobank.service.error.CustomerNotFoundException;
import au.com.suncorp.fladobank.service.error.InsufficientFundsException;
import au.com.suncorp.fladobank.service.model.request.OpenAccountRequest;
import au.com.suncorp.fladobank.service.model.response.AccountBalanceResponse;
import au.com.suncorp.fladobank.service.model.response.AccountTransferResponse;
import au.com.suncorp.fladobank.service.model.response.OpenAccountResponse;
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

    private OpenAccountRequest invalidOpenAccountRequest() {
        return new OpenAccountRequest("Florin", "Adochiei", LocalDate.of(1978, 10, 7), "UNKNOWN");
    }

    //open account

    @Test
    public void testOpenAccount() throws CustomerNotFoundException {
        //open account - new customer
        OpenAccountResponse accountResponse = fladoService.openAccount(newOpenAccountRequest());
        assertEquals(BigDecimal.ZERO, accountResponse.getBalance());
        assertNotNull(accountResponse.getAccountNumber());
        assertNotNull(accountResponse.getCustomerId());
        assertNotNull(accountResponse.getCreationDate());

        //open account - existing customer
        Long customerId = accountResponse.getCustomerId();
        accountResponse = fladoService.openAccount(customerId, "DEPOSIT");
        assertEquals(BigDecimal.ZERO, accountResponse.getBalance());
        assertNotNull(accountResponse.getAccountNumber());
        assertEquals(customerId, accountResponse.getCustomerId());
        assertNotNull(accountResponse.getCreationDate());
    }

    @Test(expected = CustomerNotFoundException.class)
    public void testCustomerNotFoundWhenOpenAccount() throws CustomerNotFoundException {
        fladoService.openAccount(Long.MAX_VALUE, "SAVINGS");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalArgumentExceptionWhenOpenAccount() {
        fladoService.openAccount(invalidOpenAccountRequest());
    }

    // deposit

    @Test
    public void testDesposit() throws AccountNotFoundException {
        OpenAccountResponse accountResponse = fladoService.openAccount(newOpenAccountRequest());
        fladoService.deposit(accountResponse.getAccountNumber(), BigDecimal.valueOf(1500));
        fladoService.deposit(accountResponse.getAccountNumber(), BigDecimal.valueOf(200));
        AccountBalanceResponse balanceResponse;
        balanceResponse = fladoService.deposit(accountResponse.getAccountNumber(), BigDecimal.valueOf(300));
        assertNotNull(balanceResponse);
        assertEquals(BigDecimal.valueOf(2000), balanceResponse.getBalance());
        assertEquals(accountResponse.getAccountNumber(), balanceResponse.getAccountNumber());
    }

    @Test(expected = AccountNotFoundException.class)
    public void testAccountNotFoundWhenDeposit() throws AccountNotFoundException {
        fladoService.deposit(Long.MAX_VALUE, BigDecimal.valueOf(123));
    }

    // transfer

    @Test(expected = AccountNotFoundException.class)
    public void testAccountNotFoundWhenTransfer() throws AccountNotFoundException, InsufficientFundsException {
        OpenAccountResponse fromAccount = fladoService.openAccount(newOpenAccountRequest());
        fladoService.transfer(fromAccount.getAccountNumber(), Long.MAX_VALUE, BigDecimal.valueOf(500));
    }

    @Test(expected = InsufficientFundsException.class)
    public void testInsufficientFundsExceptionWhenTransfer() throws AccountNotFoundException, InsufficientFundsException {
        OpenAccountResponse fromAccount = fladoService.openAccount(newOpenAccountRequest());
        OpenAccountResponse toAccount = fladoService.openAccount(newOpenAccountRequest());
        fladoService.transfer(fromAccount.getAccountNumber(), toAccount.getAccountNumber(), BigDecimal.valueOf(1));
    }

    @Test
    public void testTransfer() throws AccountNotFoundException, InsufficientFundsException {
        OpenAccountResponse fromAccount = fladoService.openAccount(newOpenAccountRequest());
        fladoService.deposit(fromAccount.getAccountNumber(), BigDecimal.valueOf(1500));
        OpenAccountResponse toAccount = fladoService.openAccount(newOpenAccountRequest());
        AccountTransferResponse transferResponse = fladoService.transfer(fromAccount.getAccountNumber(), toAccount.getAccountNumber(), BigDecimal.valueOf(500));

        assertEquals(fromAccount.getAccountNumber(), transferResponse.getFromAccount().getAccountNumber());
        assertEquals(toAccount.getAccountNumber(), transferResponse.getToAccount().getAccountNumber());
        //check balance in transfer result
        assertEquals(BigDecimal.valueOf(1000), transferResponse.getFromAccount().getBalance());
        assertEquals(BigDecimal.valueOf(500), transferResponse.getToAccount().getBalance());
        //check balance in real account
        assertEquals(BigDecimal.valueOf(1000), fladoService.getBalance(fromAccount.getAccountNumber()).getBalance());
    }

    // withdraw

    @Test(expected = AccountNotFoundException.class)
    public void testAccountNotFoundWhenWidthdraw() throws AccountNotFoundException, InsufficientFundsException {
        fladoService.withdraw(Long.MAX_VALUE, BigDecimal.valueOf(123));
    }

    @Test(expected = InsufficientFundsException.class)
    public void testInsufficientFundsExceptionWhenWidthdraw() throws AccountNotFoundException, InsufficientFundsException {
        OpenAccountResponse accountResponse = fladoService.openAccount(newOpenAccountRequest());
        fladoService.withdraw(accountResponse.getAccountNumber(), BigDecimal.valueOf(123));
    }

    @Test
    public void testWithdraw() throws AccountNotFoundException, InsufficientFundsException {
        OpenAccountResponse accountResponse = fladoService.openAccount(newOpenAccountRequest());
        fladoService.deposit(accountResponse.getAccountNumber(), BigDecimal.valueOf(1500));
        fladoService.withdraw(accountResponse.getAccountNumber(), BigDecimal.valueOf(200));
        AccountBalanceResponse balanceResponse = fladoService.withdraw(accountResponse.getAccountNumber(), BigDecimal.valueOf(300));
        assertEquals(BigDecimal.valueOf(1000), balanceResponse.getBalance());
    }

    // transactions

    @Test(expected = AccountNotFoundException.class)
    public void testAccountNotFoundExceptionWhenRetrievingTransactions() throws AccountNotFoundException {
        fladoService.getTransations(Long.MAX_VALUE);
    }

    @Test
    public void testTransactions() throws AccountNotFoundException, InsufficientFundsException {
        OpenAccountResponse sourceAccount = fladoService.openAccount(newOpenAccountRequest());
        fladoService.deposit(sourceAccount.getAccountNumber(), BigDecimal.valueOf(1500));
        fladoService.withdraw(sourceAccount.getAccountNumber(), BigDecimal.valueOf(200));

        List<TransactionResponse> transactionResponses = fladoService.getTransations(sourceAccount.getAccountNumber());
        assertEquals(2, transactionResponses.size());

        // credit transaction (deposit)
        assertEquals("CREDIT", transactionResponses.get(0).getTxnType());
        assertEquals(BigDecimal.valueOf(1500), transactionResponses.get(0).getAmount());
        assertNotNull(transactionResponses.get(0).getTxnId());
        assertNotNull(transactionResponses.get(0).getTxnDate());
        assertEquals(sourceAccount.getAccountNumber(), transactionResponses.get(0).getToAccountId());
        assertNull(transactionResponses.get(0).getFromAccountId());

        //debit transaction (withdraw)
        assertEquals("DEBIT", transactionResponses.get(1).getTxnType());
        assertEquals(BigDecimal.valueOf(200), transactionResponses.get(1).getAmount());
        assertNotNull(transactionResponses.get(1).getTxnId());
        assertNotNull(transactionResponses.get(1).getTxnDate());
        assertEquals(sourceAccount.getAccountNumber(), transactionResponses.get(1).getFromAccountId());
        assertNull(transactionResponses.get(1).getToAccountId());

        OpenAccountResponse destinationAccount = fladoService.openAccount(newOpenAccountRequest());
        fladoService.transfer(sourceAccount.getAccountNumber(), destinationAccount.getAccountNumber(), BigDecimal.valueOf(300));

        List<TransactionResponse> sourceTxns = fladoService.getTransations(sourceAccount.getAccountNumber());
        assertEquals(3, sourceTxns.size());
        assertEquals("DEBIT", sourceTxns.get(2).getTxnType());
        assertEquals(BigDecimal.valueOf(300), sourceTxns.get(2).getAmount());
        assertNotNull(sourceTxns.get(2).getTxnId());
        assertNotNull(sourceTxns.get(2).getTxnDate());
        assertEquals(sourceAccount.getAccountNumber(), sourceTxns.get(2).getFromAccountId());
        assertEquals(destinationAccount.getAccountNumber(), sourceTxns.get(2).getToAccountId());

        List<TransactionResponse> destTxns = fladoService.getTransations(destinationAccount.getAccountNumber());
        assertEquals(1, destTxns.size());
        assertEquals("CREDIT", destTxns.get(0).getTxnType());
        assertEquals(BigDecimal.valueOf(300), destTxns.get(0).getAmount());
        assertNotNull(destTxns.get(0).getTxnId());
        assertNotNull(destTxns.get(0).getTxnDate());
        assertEquals(sourceAccount.getAccountNumber(), destTxns.get(0).getFromAccountId());
        assertEquals(destinationAccount.getAccountNumber(), destTxns.get(0).getToAccountId());
    }
}
