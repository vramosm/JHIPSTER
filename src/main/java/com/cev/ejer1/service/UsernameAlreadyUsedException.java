package com.cev.ejer1.service;

public class UsernameAlreadyUsedException extends RuntimeException { // mensaje de usuario ya creado

    private static final long serialVersionUID = 1L;

    public UsernameAlreadyUsedException() {
        super("Login name already used!");
    }
}
