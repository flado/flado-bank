package au.com.suncorp.fladobank.service.error;

/**
 * Checked exception to be thrown when an account does not have enough finds for the requested operation
 */
public class InsufficientFundsException extends BankException {

    public InsufficientFundsException() {
        super();
    }

}
