package au.com.suncorp.fladobank.service.model.request;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Simple bean to be used on bank services requests.
 */
public final class OpenAccountRequest {

    private final String firstName;

    private final String lastName;

    private final LocalDate dob;

    private final String accountType;

    public OpenAccountRequest(String firstName, String lastName, LocalDate dob, String accountType) {
        if (Objects.isNull(firstName) || Objects.isNull(lastName) || Objects.isNull(dob) || Objects.isNull(accountType)) {
            throw new IllegalArgumentException();
        }
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.accountType = accountType;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDate getDob() {
        return dob;
    }

    public String getAccountType() {
        return accountType;
    }
}
