package com.addressbook.app.exceptions;


    public class ValidationException extends RuntimeException {
        public ValidationException(String message) {
            super(message);
        }
    }

