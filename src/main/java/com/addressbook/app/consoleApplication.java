package com.addressbook.app;

import com.addressbook.app.exceptions.ValidationException;

import java.util.Scanner;

public class consoleApplication {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AddressBook addressBook = new AddressBook();
        boolean running = true;


        while (running) {
            menu();
            System.out.println("Enter your selection: ");
            String command = scanner.nextLine();
            switch (command) {
                case "1":
                    System.out.println("Enter name: ");
                    String name = scanner.nextLine();
                    System.out.println("Enter email: ");
                    String email = scanner.nextLine();
                    System.out.println("Enter phone number: ");
                    String phoneNumber = scanner.nextLine();
                    try{
                    addressBook.addContact(new Contact(name, email, phoneNumber));
                        System.out.println("Contact successfully added.");
                    } catch (ValidationException e) {
                        System.err.println("Failed to add contact: " + e.getMessage());
                        continue;
                    }
                    System.out.println("----------------------------------------");
                    break;
                case "2":
                    System.out.println("Enter id: ");
                    int id = scanner.nextInt();
                    addressBook.removeContact(id);
                    System.out.println("Contact successfully removed.");
                    System.out.println("----------------------------------------");
                    break;
                case "3":
                    System.out.println("Enter id: ");
                    int idToEdit = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Enter name: ");
                    String nameToEdit = scanner.nextLine();
                    System.out.println("Enter email: ");
                    String emailToEdit = scanner.nextLine();
                    System.out.println("Enter phone number: ");
                    String phoneNumberToEdit = scanner.nextLine();
                    try{
                    addressBook.editContact(idToEdit, nameToEdit, emailToEdit, phoneNumberToEdit);
                        System.out.println("Contact successfully changed.");
                    } catch (ValidationException e) {
                        System.err.println("Failed to edit contact: " + e.getMessage());
                        continue;
                    }
                    System.out.println("----------------------------------------");
                    break;
                case "4":
                    System.out.println("Enter search term: ");
                    String searchTerm = scanner.nextLine();
                    System.out.println("Search results: ");
                    addressBook.searchContacts(searchTerm);
                    System.out.println("----------------------------------------");
                    break;
                case "5":
                    System.out.println("All contacts: ");
                    System.out.println(addressBook.getAllContacts());
                    System.out.println("----------------------------------------");
                    break;
                case "6":
                    addressBook.deleteAll();
                    System.out.println("All contacts successfully deleted.");
                    System.out.println("----------------------------------------");
                    break;
                case "7":
                    System.out.println("Thank you for using the Address Book Application. Goodbye!");
                    System.out.println("----------------------------------------");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid command, please try again.");
                    System.out.println("----------------------------------------");
            }
        }
        scanner.close();
    }
    public static void menu() {
        System.out.println("----------------------------------------");
        System.out.println("Welcome to the Address Book Application");
        System.out.println("----------------------------------------");
        System.out.println("Please select an option from the menu below:");
        System.out.println("----------------------------------------");
        System.out.println("1. Add a contact");
        System.out.println("2. Remove a contact");
        System.out.println("3. Edit a contact");
        System.out.println("4. Search for a contact");
        System.out.println("5. List all contacts");
        System.out.println("6. Delete all contacts");
        System.out.println("7. Exit");
        System.out.println("----------------------------------------");
    };
}
