/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sanchay.auth;

import org.apache.shiro.config.Ini;
import org.apache.shiro.env.BasicIniEnvironment;

/**
 *
 * @author User
 */
public class SanchayIniEnvironment extends BasicIniEnvironment {

    public SanchayIniEnvironment(Ini ini) {
        super(ini);
    }

    public SanchayIniEnvironment(String iniResourcePath) {
        super(iniResourcePath);
    }
    
}
