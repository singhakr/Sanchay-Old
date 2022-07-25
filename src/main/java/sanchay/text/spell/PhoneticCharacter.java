/*
 * PhoneticCharacter.java
 *
 * Created on April 4, 2006, 4:45 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.text.spell;

import java.io.*;

/**
 *
 * @author anil
 */
public class PhoneticCharacter {
    
    protected Character character;
    protected PhoneticModelOfScripts charPhoneticFeatures;
    protected PrintStream logPS;
    
    /** Creates a new instance of PhoneticCharacter */
    public PhoneticCharacter(Character c, PhoneticModelOfScripts cpf, PrintStream ps)
    {
	character = c;
	charPhoneticFeatures = cpf;
	logPS = ps;
    }
    
    public PhoneticModelOfScripts getCharPhoneticFeatures()
    {
	return charPhoneticFeatures;
    }
    
    public Character getCharacter()
    {
	return character;
    }
    
    public PrintStream getLogPrintStream()
    {
        return logPS;
    }
}
