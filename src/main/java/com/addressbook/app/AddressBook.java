package com.addressbook.app;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.addressbook.app.exceptions.ValidationException   ;

public class AddressBook {

    private ArrayList<Contact> contacts = new ArrayList<>();
    public AddressBook() {}

    public void addContact(Contact contact) {
        if (contact != null && !doesContactAlreadyExist(contact.getId(), contact.getEmail(), contact.getPhoneNumber())) {  // TODO: make less ugly - null check gets invalidated if i define variables before but I want null handled
            try {
                contacts.add(contact);
            } catch (ValidationException e) {
                System.err.println("Failed to add contact: " + e.getMessage());
            }
        } else {
            System.err.println("Failed to add contact: contact is null");
        }
    }

    public void removeContact(int id) {
        for (Contact contact : contacts) {
            if (contact.getId() == id) {
                contacts.remove(contact);
                return;
            }
        }
        System.err.println("Failed to remove contact: contact not found");
    }

    public void editContact(int id, String name, String email, String phoneNumber) {
        for (Contact contact : contacts) {
            if (contact.getId() == id && !doesContactAlreadyExist(id, email, phoneNumber) ) {
                contact.setName(name);
                contact.setEmail(email);
                contact.setPhoneNumber(phoneNumber);
                return;
            }
        }
        System.err.println("Failed to edit contact: contact not found");
    }

    public ArrayList<Contact> getContacts() {
        return contacts; // exists purely for testing so far
    }

    public String getAllContacts() {
        StringBuilder result = new StringBuilder();
        if (contacts.isEmpty()) {
            System.err.println("No contacts found");
        } else {
            contacts.sort(Comparator.comparing(Contact::getName));
            for (Contact contact : contacts) {
                String contactInfo = "ID: " + contact.getId() +  "Name: " + contact.getName() + ", Email: " + contact.getEmail() + ", Phone: " + contact.getPhoneNumber();
                result.append(contactInfo).append("\n");
            }
        }
        return result.toString();
    }

    public void deleteAll() {
        if (contacts.isEmpty()) {
            System.err.println("No contacts found");
            return;
        }
        contacts.clear();
        System.out.println("All contacts have been deleted.");
    }

    public void searchContacts(String name) {
        List<Contact> filteredContacts = filterContactsByName(name);
        printSortedContacts(filteredContacts);
    }

    private List<Contact> filterContactsByName(String name) {  // retrieves search results matching the input, places them in an arraylist to be used by printSortedContacts
        String lowerCaseName = name.toLowerCase();
        List<Contact> filteredContacts = new ArrayList<>();
        for (Contact contact : contacts) {
            boolean containsName = contact.getName().toLowerCase().contains(lowerCaseName);
            if (containsName) {
                filteredContacts.add(contact);
            }
        }
        return filteredContacts;
    }

    private void printSortedContacts(List<Contact> filteredContacts) {  // sorts the arraylist of filtered contacts into alphabetical order, then prints them
        filteredContacts.sort(Comparator.comparing(Contact::getName));
        for (Contact contact : filteredContacts) {
            System.out.println("Name: " + contact.getName() + ", Email: " + contact.getEmail() + ", Phone: " + contact.getPhoneNumber());
        }
    }

    private boolean doesContactAlreadyExist(int id, String email, String phoneNumber) {
        return doesEmailAlreadyExist(id, email) || doesPhoneNumberAlreadyExist(id, phoneNumber);
    }
    private boolean doesEmailAlreadyExist(int id, String email) {
        for (Contact contact : contacts) {
            if (contact.getId() != id && contact.getEmail().equals(email)) {
                throw new ValidationException("Email already exists");
            }
        }
        return  false;
    }

    private boolean doesPhoneNumberAlreadyExist(int id, String phoneNumber) {
        for (Contact contact : contacts) {
            if (contact.getId() != id && contact.getPhoneNumber().equals(phoneNumber)) {
                throw new ValidationException("Phone number already exists");
            }
        }
        return false;
    }

}