/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sanchay.servers.impl;

/**
 *
 * @author User
 */
public class AuthorizationException extends Exception {

    public static final int USER_PASSWORD_DOES_NOT_EXIST = 1;
    private int errorCode;

    public AuthorizationException(String cause, int newErrorCode) {
        super(cause);
        errorCode = newErrorCode;
    }

    public AuthorizationException(String cause) {
        super(cause);
    }

    public int getErrorCode() {
        return errorCode;
    }
    
}
