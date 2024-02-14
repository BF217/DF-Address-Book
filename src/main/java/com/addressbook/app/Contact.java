package com.addressbook.app;

import com.addressbook.app.exceptions.ValidationException;

public class Contact {
    private String name;
    private String email;
    private String phoneNumber;
    private static int nextId = 1;
    private int id = 0;

    public Contact(String name, String email, String phoneNumber) {
        if (checkIsNotNull(name, email, phoneNumber) && validateInput(name, email, phoneNumber) ) {
            this.name = name;
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.id = nextId;
            nextId++;
        } else {
            throw new ValidationException("Cannot add null values to address book");
        }
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        validateName(name);
        this.name = name;
    }

    public void setEmail(String email) {
        validateEmail(email);
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        validatePhoneNumber(phoneNumber);
        this.phoneNumber = phoneNumber;
    }

    private boolean validateInput(String name, String email, String phoneNumber) {     // TODO: possibly move validation to a separate class
        validateName(name);
        validateEmail(email);
        validatePhoneNumber(phoneNumber);
        return true;
    }

    private void validateName(String name) {
        if (name.trim().isEmpty()){
            throw new ValidationException("Name must not be empty");
        }
    }

    private void validateEmail(String email) {
        // regular expression checks to see if email contains @, and a "." followed by any top level domain (com, org, net, etc)
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        if (!email.matches(emailRegex)){
            throw new ValidationException("Invalid email format");
        }
    }

    private void validatePhoneNumber(String phoneNumber) {
        // regular expression checks to see if phone number meets most common phone number formats globally
        String phoneRegex = "^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$";
        if (!phoneNumber.matches(phoneRegex)){
            throw new ValidationException("Invalid phone number format");
        }
    }

    private boolean checkIsNotNull(String name, String email, String phoneNumber) {
        return name != null || email != null || phoneNumber != null;
    }
}

