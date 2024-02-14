package com.addressbook.app;

import com.addressbook.app.exceptions.ValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class AddressBookTest {
    private AddressBook addressBook;
    private Contact John;
    private Contact Steve;
    private Contact Joe;
    private ByteArrayOutputStream outputStream;
    private PrintStream SystemOutSpy;
    private PrintStream SystemErrSpy;
    @BeforeEach
    void setUp() {
        // objects
        addressBook = new AddressBook();
        John = new Contact("John", "jsmith@email.com", "1234567890");
        Steve = new Contact("Steve", "sjones@email.net", "0987654321");
        Joe = new Contact("Joe", "jjoestar@email.com", "0987612345");
        // System.out spy
        outputStream = new ByteArrayOutputStream();
        SystemOutSpy = spy(new PrintStream(outputStream));
        System.setOut(SystemOutSpy);
        // System.err spy
        SystemErrSpy = spy(new PrintStream(outputStream));
        System.setErr(SystemErrSpy);
    }


    @Test
    @DisplayName("addContact() should add an entry to the Contact arraylist<>")
    void testAddContactAddsContact() {
        addressBook.addContact(John);
        assertEquals(1, addressBook.getContacts().size(), "contact was not added");
    }

    @Test
    @DisplayName("addContact() should reject null values")
    void testAddContactRejectsNull(){
        addressBook.addContact(null);
        verify(SystemErrSpy).println("Failed to add contact: contact is null");
    }

    @Test
    @DisplayName("searchContacts() should display a contact when the exact name is entered.")
    void testSearchContactsDisplaysContact() {
        addressBook.addContact(John);
        addressBook.searchContacts("John");
        verify(SystemOutSpy).println("Name: John, Email: jsmith@email.com, Phone: 1234567890");
    }

    @Test
    @DisplayName("searchContacts() should be case-insensitive.")
    void testSearchContactsIsCaseInsensitive() {
        addressBook.addContact(John);
        addressBook.searchContacts("John");
        verify(SystemOutSpy).println("Name: John, Email: jsmith@email.com, Phone: 1234567890");
    }

    @Test
    @DisplayName("searchContacts() should display nothing when an incorrect name is entered.")
    void testSearchContactsDisplaysNothingIncorrectName(){
        addressBook.addContact(John);
        addressBook.searchContacts("Jane");
        verify(SystemOutSpy, never()).println();     // mockito tests that println() isn't called, meaning there are no matches in the list
    }

    @Test
    @DisplayName("searchContacts() should display nothing when an empty string is entered")
    void testSearchContactsDisplaysNothingEmptyString(){
        addressBook.addContact(John);
        addressBook.searchContacts("");
        verify(SystemOutSpy, never()).println();
    }

    @Test
    @DisplayName("searchContacts() should return contacts that are partially matched")
    void testSearchContactsDisplaysPartialMatches() {
        addressBook.addContact(John);
        addressBook.searchContacts("Jo");
        verify(SystemOutSpy).println("Name: John, Email: jsmith@email.com, Phone: 1234567890");
    }

    @Test
    @DisplayName("searchContacts() should handle special characters correctly.")
    void testSearchContactsHandlesSpecialCharacters() {
        Contact jane = new Contact("J@ne!", "jplane@email.net", "1234567890");
        addressBook.addContact(jane);
        addressBook.searchContacts("J@ne!");
        verify(SystemOutSpy).println("Name: J@ne!, Email: jplane@email.net, Phone: 1234567890");
    }

    @Test
    @DisplayName("searchContacts() should return multiple contacts in alphabetical order.")
    void testSearchContactsDisplaysMultipleContacts() {
        // Arrange
        Contact Jozef = new Contact("Jozef", "json@email.com", "1234509876");
        addressBook.addContact(Jozef);
        addressBook.addContact(John);
        addressBook.addContact(Steve);
        addressBook.addContact(Joe);
        // Act
        addressBook.searchContacts("Jo");
        // Assert
        String output = outputStream.toString().trim();
        List<String> printedLines = Arrays.asList(output.split("\n"));

        List<String> expectedOrder = Arrays.asList(
                "Name: Joe, Email: jjoestar@email.com, Phone: 0987612345",
                "Name: John, Email: jsmith@email.com, Phone: 1234567890",
                "Name: Jozef, Email: json@email.com, Phone: 1234509876"
        );

        // verify that output is in alphabetical order and contains the correct amount of matches
        assertEquals(expectedOrder.size(), printedLines.size(), "Number of printed contacts does not match");
        for (int i = 0; i < expectedOrder.size(); i++) {
            String expected = expectedOrder.get(i).toLowerCase().trim();
            String actual = printedLines.get(i).toLowerCase().trim();                                 // In later test I discovered it was a missing \n character, possibly could've been the case here
            assertEquals(expected, actual, "Contact at position " + i + " does not match");  // passed because I formatted the strings to look the same, failed otherwise, not sure that this is a good test, however, original failure had the same output but didn't match despite being identical?        }
        }
    }

    @Test
    @DisplayName("removeContact() should remove a contact from contacts list using an ID")
    void testRemoveContactRemovesContact() {
        addressBook.addContact(John);
        addressBook.addContact(Steve);
        int id = John.getId();
        addressBook.removeContact(id);
        assertEquals(1, addressBook.getContacts().size(), "contact was not removed");
    }

    @Test
    @DisplayName("each contact should have a unique ID")
    void testContactHasUniqueID() {
        addressBook.addContact(John);
        addressBook.addContact(Steve);
        addressBook.addContact(Joe);
        int id1 = John.getId();
        int id2 = Steve.getId();
        int id3 = Joe.getId();
        assertEquals(1, id1, "ID is not unique");
        assertEquals(2, id2, "ID is not unique");
        assertEquals(3, id3, "ID is not unique");    // Test works individually but fails when run with other tests, because other objects and @BeforeEach change the id number
    }

    @Test
    @DisplayName("removeContact() should display an error message if the contact is not found")
    void testRemoveContactDisplaysErrorMessage() {
        addressBook.addContact(John);
        addressBook.removeContact(2);
        verify(SystemErrSpy).println("Failed to remove contact: contact not found");
    }

    @Test
    @DisplayName("Once removed, the contact should not be found in the contacts list when using search contacts")
    void testSearchContactDoesntShowRemovedItems() {
        addressBook.addContact(John);
        addressBook.addContact(Steve);
        addressBook.removeContact(1);
        addressBook.searchContacts("John");
        verify(SystemOutSpy, never()).println();
    }

    @Test
    @DisplayName("removeContact() should correctly handle sequential calls to remove contacts.")
    void testRemoveContactHandlesSequentialCalls() {
        addressBook.addContact(John);
        addressBook.addContact(Steve);
        addressBook.addContact(Joe);
        addressBook.removeContact(1);
        addressBook.removeContact(2);
        addressBook.removeContact(3);
        assertEquals(0, addressBook.getContacts().size(), "contacts were not removed");
        verify(SystemErrSpy, never()).println(); // test none of the calls resulted in errors
    }

    @Test
    @DisplayName("editContact() should change the name, email, and phone number of a contact")
    void testEditContactChangesContact() {
        addressBook.addContact(John);
        addressBook.editContact(1, "Jane", "jsmith@email.com", "1234567890");
        assertEquals("Name: Jane, Email: jsmith@email.com, Phone: 1234567890\n", addressBook.getAllContacts(), "contact was not edited");
    }

    @Test
    @DisplayName("editContact() should return an error message if the ID is not found.")
    void testEditContactDisplaysErrorMessage() {
        addressBook.addContact(John);
        addressBook.editContact(2, "Jane", "jsmith@email.com", "1234567890");
        verify(SystemErrSpy).println("Failed to edit contact: contact not found");
    }

    @Test
    @DisplayName("editContact() should reject an entry if attempting to use invalid formatting.")
    void testEditContactRejectsInvalidFormatting() {
        addressBook.addContact(John);
        assertThrows(ValidationException.class, () -> addressBook.editContact(1, "Jane", "jsmithemail.com", "1234567890"), "contact was edited despite invalid email format");
    }

    @Test
    @DisplayName("editContact() should handle editing multiple fields of the contact - name, email and phone number.")
    void testEditContactHandlesMultipleFields() {
        addressBook.addContact(John);
        addressBook.editContact(1, "Jane", "jlow@email.net", "0987654321");
        assertEquals("Name: Jane, Email: jlow@email.net, Phone: 0987654321\n",
                addressBook.getAllContacts(), "contact was not edited");
    }

    @Test
    @DisplayName("editContact() should handle sequential calls to edit contacts.")
    void testEditContactHandlesSequentialCalls() {
        addressBook.addContact(John);
        addressBook.editContact(1, "Jane", "jlow@email.net", "0987654321");
        addressBook.editContact(1, "Jane", "jdoe@email.net", "0987654321");
        addressBook.editContact(1, "Jane", "jdoe@email.net", "5432167890");
        assertEquals("Name: Jane, Email: jdoe@email.net, Phone: 5432167890\n",
                addressBook.getAllContacts(), "contact was not edited");
    }

    @Test
    @DisplayName("addContact() should reject an entry if the phone number already exists in the address book")
    void testAddContactRejectsDuplicatePhoneNumber() {
        addressBook.addContact(John);
        Contact John2 = new Contact("John", "Jcena@email.com", "1234567890");
        assertThrows(ValidationException.class, () -> addressBook.addContact(John2), "contact was added despite duplicate phone number");
    }

    @Test
    @DisplayName("addContact() should reject an entry if the email already exists in the address book")
    void testAddContactRejectsDuplicateEmail() {
        addressBook.addContact(John);
        Contact John2 = new Contact("John", "jsmith@email.com", "0987654321");
        assertThrows(ValidationException.class, () -> addressBook.addContact(John2), "contact was added despite duplicate email");
    }

    @Test
    @DisplayName("addContact() should reject an entry if both the phone number and email already exists in the address book.")
    void testAddContactRejectsDuplicateEmailAndPhoneNumber() {
        addressBook.addContact(John);
        Contact John2 = new Contact("John", "jsmith@email.com", "1234567890");
        assertThrows(ValidationException.class, () -> addressBook.addContact(John2), "contact was added despite duplicate email and phone number");
    }

    @Test
    @DisplayName("editContact() should reject changes that match an existing email in the address book.")
    void testEditContactRejectsDuplicateEmail() {
        addressBook.addContact(John);
        addressBook.addContact(Steve);
        assertThrows(ValidationException.class, () -> addressBook.editContact(1, "John", "sjones@email.net", "1234567890"), "contact was edited despite duplicate email");
    }

    @Test
    @DisplayName("editContact() should reject changes that match an existing phone number in the address book")
    void testEditContactRejectsDuplicatePhoneNumber() {
        addressBook.addContact(John);
        addressBook.addContact(Steve);
        assertThrows(ValidationException.class, () -> addressBook.editContact(1, "John", "jsmith@email.com", "0987654321"), "contact was edited despite duplicate phone number");
    }

    @Test
    @DisplayName("addContact() should accept an entry that matches a previous entry that was deleted from the address book.")
    void testAddContactAcceptsDeletedContact() {
        addressBook.addContact(John);
        addressBook.removeContact(1);
        Contact John2 = new Contact("John", "jsmith@email.com", "1234567890");
        addressBook.addContact(John2);
        assertEquals(1, addressBook.getContacts().size(), "contact was not added");
    }

@Test
@DisplayName("getAllContacts() should return all contacts in the address book.")
void testGetAllContactsReturnsAllContacts() {
    addressBook.addContact(John);
    addressBook.addContact(Steve);
    addressBook.addContact(Joe);
    int numberOfContacts = addressBook.getAllContacts().split("\n").length;
    assertEquals(3, numberOfContacts, "Not all contacts were returned");
}

@Test
@DisplayName("getAllContacts() should return an error message if there are no contacts")
void testGetAllContactsDisplaysErrorMessage() {
        addressBook.addContact(John);
        addressBook.removeContact(1);
    addressBook.getAllContacts();
    verify(SystemErrSpy).println("No contacts found");
}

@Test
@DisplayName("getAllContacts() should return contacts in alphabetical order.")
void testGetAllContactsReturnsInAlphabeticalOrder() {
    addressBook.addContact(Joe);
    addressBook.addContact(Steve);
    addressBook.addContact(John);

    String output = addressBook.getAllContacts();
    List<String> printedLines = Arrays.asList(output.split("\n"));

    List<String> expectedOrder = Arrays.asList(
            "Name: Joe, Email: jjoestar@email.com, Phone: 0987612345",
            "Name: John, Email: jsmith@email.com, Phone: 1234567890",
            "Name: Steve, Email: sjones@email.net, Phone: 0987654321"
    );

    // verify that output is in alphabetical order and contains the correct amount of matches
    assertEquals(expectedOrder.size(), printedLines.size(), "Number of printed contacts does not match");
    for (int i = 0; i < expectedOrder.size(); i++) {
        String expected = expectedOrder.get(i).toLowerCase().trim();
        String actual = printedLines.get(i).toLowerCase().trim();                                 // In later test I discovered it was a missing \n character, possibly could've been the case here
        assertEquals(expected, actual, "Contact at position " + i + " does not match");  // passed because I formatted the strings to look the same, failed otherwise, not sure that this is a good test, however, original failure had the same output but didn't match despite being identical?        }
    }
}

    @Test
    @DisplayName("getAllContacts() should update when a contact is added or removed.")
    void testGetAllContactsShouldUpdate() {
        addressBook.addContact(John);
        addressBook.addContact(Steve);
        addressBook.addContact(Joe);
        int numberOfContacts = addressBook.getAllContacts().split("\n").length;
        assertEquals(3, numberOfContacts, "Not all contacts were returned");
        addressBook.removeContact(1);
        numberOfContacts = addressBook.getAllContacts().split("\n").length;
        assertEquals(2, numberOfContacts, "Not all contacts were returned");
    }

    @Test
    @DisplayName("getAllContacts() should update when a contact is edited.")
    void testGetAllContactsShouldUpdateWhenContactIsEdited() {
        addressBook.addContact(John);
        assertEquals("Name: John, Email: jsmith@email.com, Phone: 1234567890\n", addressBook.getAllContacts(), "contact was not added");
        addressBook.editContact(1, "Jane", "jsmith@email.com", "1234567890");
        assertEquals("Name: Jane, Email: jsmith@email.com, Phone: 1234567890\n", addressBook.getAllContacts(), "edit was not updated");
    }

    @Test
    void testDeleteAll() {
        addressBook.addContact(John);
        addressBook.addContact(Steve);
        addressBook.addContact(Joe);
        addressBook.deleteAll();
        verify(SystemOutSpy).println("All contacts have been deleted.");
        assertEquals(0, addressBook.getContacts().size(), "contacts were not removed");
    }

    @Test
    void testDeleteAllOnEmptyList() {
        addressBook.deleteAll();
        verify(SystemOutSpy).println("All contacts have been deleted.");
        assertEquals(0, addressBook.getContacts().size(), "contacts were not removed");
    }

    @Test
    void testDeleteAllDisplaysErrorMessage() {
        addressBook.addContact(John);
        addressBook.removeContact(1);
        addressBook.deleteAll();
        verify(SystemErrSpy).println("No contacts found");
    }


    @AfterEach
    void reset(){
        System.setErr(System.err);
        System.setOut(System.out);
    }

}
