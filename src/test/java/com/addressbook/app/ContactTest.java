package com.addressbook.app;

import com.addressbook.app.exceptions.ValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


public class ContactTest {
    private PrintStream SystemOutSpy;
    private PrintStream originalSystemOut;
    @BeforeEach
   void setUp() {
        originalSystemOut = System.out;
        SystemOutSpy = spy(System.out);
        System.setOut(SystemOutSpy);
        doNothing().when(SystemOutSpy).println(anyString());   // prevents messages being printed
    }
    @Test
    @DisplayName("Contact constructor should reject an entry if the name is empty")
    void testNameIsValid() {
        // Test custom error message is thrown
        assertThrows(ValidationException.class, () -> new  Contact("", "jsmith@email.com", "01234567890"), "contact was added despite empty name");
    }

    @Test
    @DisplayName("Contact constructor should reject an entry if the email format is invalid")
    void testEmailIsValid() {
        // Test custom error message is thrown
        assertThrows(ValidationException.class, () -> new Contact("John", "jsmithemail.com", "1234567890"), "contact was added despite invalid email format");
    }

    @Test
    @DisplayName("Contact constructor should reject an entry if the phone number format is invalid")
    void testPhoneNumberIsValid() {;
        assertThrows(ValidationException.class, () -> new Contact("John", "jsmith@email.net", "abcdefghij"), "contact was added despite invalid phone number format");
    }

    @Test
    @DisplayName("Contact constructor should reject null values in any of the arguments")
    void testContactConstructorRejectsNullValues(){
        assertThrows(NullPointerException.class, () -> new Contact(null, "jsmith@email.com", "1234567890"), "null value was accepted");
        assertThrows(NullPointerException.class, () -> new Contact("John", null, "1234567890"), "null value was accepted");
        assertThrows(NullPointerException.class, () -> new Contact("John", "jsmith@email.com", null), "null value was accepted");
    }

    @Test
    void testContactConstructorHandlesNullValues() {
        assertThrows(ValidationException.class, () -> new Contact(null, null, null), "null value was accepted");
    }

    @AfterEach
    void reset(){
        System.setOut(originalSystemOut);
    }
}
