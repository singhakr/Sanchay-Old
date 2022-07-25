/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sanchay.servers.impl;

/**
 *
 * @author User
 */
public class SessionException extends Exception {
    
    public static final int SESSION_ID_REQUIRED = 1;
    public static final int SESSION_ID_DOES_NOT_EXIST = 2;
    private int errorCode;

    public SessionException(String cause, int newErrorCode) {
        super(cause);
        errorCode = newErrorCode;
    }

    public SessionException(String cause) {
        super(cause);
    }

    public int getErrorCode() {
        return errorCode;
    }

}
