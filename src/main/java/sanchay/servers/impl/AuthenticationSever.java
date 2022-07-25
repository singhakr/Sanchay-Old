/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sanchay.servers.impl;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Collection;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.env.BasicIniEnvironment;
import org.apache.shiro.env.Environment;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.text.TextConfigurationRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import sanchay.servers.AuthenticationSeverRI;

/**
 *
 * @author User
 */
//public class AuthenticationSever extends UnicastRemoteObject implements AuthenticationSeverRI, Serializable {
//public class AuthenticationSever implements AuthenticationSeverRI, Serializable {
public class AuthenticationSever implements AuthenticationSeverRI, Runnable {

//        Environment env = new BasicIniEnvironment("classpath:shiro.ini");
        private final Environment env = new BasicIniEnvironment("shiro.ini");
        private final org.apache.shiro.mgt.SecurityManager securityManager = env.getSecurityManager();        
        private final TextConfigurationRealm configurationRealm  = new TextConfigurationRealm();
        
        private Subject currentUser;
                
//        Factory<SecurityManager> factory;
//        factory = new IniSecurityManagerFactory("classpath:shiro.ini");
        
//        SecurityManager securityManager = factory.getInstance();

//    private final SanchayMainServer sanchayMainServer;

//    public AuthenticationSever(SanchayMainServer sanchayMainServer) throws RemoteException
    public AuthenticationSever() throws RemoteException
    {
//        this.sanchayMainServer = sanchayMainServer;
    
        SecurityUtils.setSecurityManager(securityManager);
        
        // get the currently executing user:
        currentUser = SecurityUtils.getSubject();

        // Do some stuff with a Session (no need for a web or EJB container!!!)
        Session session = currentUser.getSession();
        session.setAttribute("someKey", "aValue");
        

        String value = (String) session.getAttribute("someKey");

        if (value.equals("aValue")) {
            SanchayMainServer.getLogger().info("Retrieved the correct value! [" + value + "]");
        }
    }
    
    /** Returns session id **/
    public Serializable authenticateUser(String userName, String password)  throws RemoteException
    {
        org.slf4j.Logger log = SanchayMainServer.getLogger();
        log.info("User name:"+ userName);
        log.info("Current user:"+ currentUser.getPrincipal());

        Serializable result = isSubjectLoggedIn(userName);
        
        if(result == null)
        {        
            // let's login the current user so we can check against roles and permissions:
            if (!currentUser.isAuthenticated()) {
    //            UsernamePasswordToken token = new UsernamePasswordToken("lonestarr", "vespa");
                UsernamePasswordToken token = new UsernamePasswordToken(userName, password);
                
                token.setRememberMe(true);
                
                try {
                    currentUser.login(token);
                    
                    result = currentUser.getSession().getId();
                    
                } catch (UnknownAccountException uae) {
                    log.info("There is no user with username of " + token.getPrincipal());
                } catch (IncorrectCredentialsException ice) {
                    log.info("Password for account " + token.getPrincipal() + " was incorrect!");
                } catch (LockedAccountException lae) {
                    log.info("The account for username " + token.getPrincipal() + " is locked.  " +
                            "Please contact your administrator to unlock it.");
                }
                // ... catch more exceptions here (maybe custom ones specific to your application?
                catch (AuthenticationException ae) {
                    //unexpected condition?  error?
                }

                return result;
            }
        }

        return result;
    }
    
    public Collection<Session> listCurrentSessions()
    {
        DefaultSessionManager sm = (DefaultSessionManager) ((DefaultSecurityManager) securityManager).getSessionManager();

        Collection<Session> sessions = sm.getSessionDAO().getActiveSessions();

        return sessions;
    }
    
    /** Returns session id **/
    public Serializable isSubjectLoggedIn(String username) throws RemoteException
    {
        DefaultSecurityManager secm = (DefaultSecurityManager) this.securityManager;
        DefaultSessionManager sm = (DefaultSessionManager) (secm.getSessionManager());
//        Collection<Session> currentSessions = listCurrentSessions();
        
        for (Session session : sm.getSessionDAO().getActiveSessions()) {            
            SimplePrincipalCollection p = (SimplePrincipalCollection) session
                    .getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);

            if (p != null && username.equals(p.getPrimaryPrincipal())) {
                return session.getId();
            }
        }
        
        return null;
    }

    @Override
    public void run() {
        
        System.out.println("Running authentication task.");
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
