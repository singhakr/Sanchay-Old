/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sanchay.servers.impl;

/**
 *
 * @author User
 */
public class SanchayServerThread extends Thread {
    
    
    SanchayServerThread(Runnable runnable)
    {
        super(runnable);
        System.out.println("New thread: " + this);
    }    
    @Override
    public void run() {
         while (!Thread.interrupted()) {
            System.out.println("Thread is running");
        }
        System.out.println("Thread has stopped.");
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
