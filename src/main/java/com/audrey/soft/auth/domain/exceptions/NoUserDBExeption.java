package com.audrey.soft.auth.domain.exceptions;

public class NoUserDBExeption extends RuntimeException{
    public NoUserDBExeption(){
        super("El nombre de usuario no existe");
    }
}
