package com.cev.ejer1.service;

public class InvalidPasswordException extends RuntimeException { // mensaje de password incorrecto

    private static final long serialVersionUID = 1L;

    public InvalidPasswordException() {
        super("Incorrect password");
    }
}
