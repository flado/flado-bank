package au.com.suncorp.fladobank.data.model;


import java.time.LocalDate;

/**
 * A bank customer entity. Stores private details about a person. <br>
 * This is a thread-safe entity in case future bank operations would allow updating customer details. <br>
 *
 * For simplicity I consider FladoBank deals only with individuals as customers (I exclude companies, SMSFs, etc).
 * Also the list of associated accounts for this customer is maintained in the datastore only.
 */

public final class Customer extends BaseEntity{

    private String firstName;

    private String lastName;

    private LocalDate dob;

    public Customer(String firstName, String lastName, LocalDate dob) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
    }

    public synchronized String getFirstName() {
        return firstName;
    }

    public synchronized String getLastName() {
        return lastName;
    }

    public synchronized LocalDate getDob() {
        return dob;
    }

    public synchronized void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public synchronized void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public synchronized void setDob(LocalDate dob) {
        this.dob = dob;
    }
}
