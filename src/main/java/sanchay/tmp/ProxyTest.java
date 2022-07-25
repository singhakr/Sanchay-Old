/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.tmp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;

/**
 *
 * @author anil
 */
public class ProxyTest {

    public void setProxy()
    {
        String host = "10.1.1.10";
        String port = "80";
        System.out.println("Using proxy: " + host + ":" + port);
        System.setProperty("http.proxyHost", host);
        System.setProperty("http.proxyPort", port);
        System.setProperty("http.proxySet", "true");
        System.setProperty("http.nonProxyHosts", "localhost|127.0.0.1");        
        
        final String authUser = "";
        final String authPassword = "";

        Authenticator.setDefault(
           new Authenticator() {
              public PasswordAuthentication getPasswordAuthentication() {
                 return new PasswordAuthentication(
                       authUser, authPassword.toCharArray());
              }
           }
        );

        System.setProperty("http.proxyUser", authUser);
        System.setProperty("http.proxyPassword", authPassword);        
        
        
    }
    
    public static void main(String args[])
    {
        System.out.println("Testing proxy...");

        ProxyTest proxyText = new ProxyTest();
        
        proxyText.setProxy();
        
        try {
            URL my_url = new URL("http://www.vimalkumarpatel.blogspot.com/");
            BufferedReader br = new BufferedReader(new InputStreamReader(my_url.openStream()));
            String strTemp = "";
            
            while(null != (strTemp = br.readLine())){
                System.out.println(strTemp);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }        
    }
}
