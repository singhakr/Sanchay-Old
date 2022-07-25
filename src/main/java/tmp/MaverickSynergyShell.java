/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tmp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

//import com.sshtools.client.SessionChannelNG;
//import com.sshtools.client.SshClient;
//import com.sshtools.client.shell.ExpectShell;
//import com.sshtools.client.shell.ShellTimeoutException;
//import com.sshtools.client.tasks.ShellTask;
//import com.sshtools.common.logger.Log;
//import com.sshtools.common.logger.Log.Level;
//import com.sshtools.common.ssh.SshException;
//import com.sshtools.common.util.Utils;

/**
 *
 * @author User
 */
public class MaverickSynergyShell {

   public static void main(String[] args) throws IOException {
       
////    Log.getDefaultContext().enableConsole(Level.DEBUG);
//
//      try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
//			
//         String hostname = Utils.prompt(reader, "Hostname", "test.rebex.net");
//         int port = 22;
//         if (Utils.hasPort(hostname)) {
//            port = Utils.getPort(hostname);
//         }
//
////         String username = Utils.prompt(reader, "Username", System.getProperty("user.name"));
//         String username = Utils.prompt(reader, "Username", "demo");
//         String password = Utils.prompt(reader, "Password", "password");
//
//         try (SshClient ssh = new SshClient(hostname, port, username, password.toCharArray())) {
//
//            ssh.runTask(new ShellTask(ssh) {
////               @Override
//               protected void onOpenSession(SessionChannelNG session)
//                      throws IOException, SshException, 
//                                   ShellTimeoutException {
//
//                  ExpectShell shell = new ExpectShell(this);
//                  
////                  System.out.println(shell.getOsDescription());
//
//                  shell.execute("ls -l");
////                  shell.execute("mkdir tmp");
////                  shell.execute("cd tmp");
////                  shell.execute("echo Synergy was here > readme.txt");
////                  shell.execute("tar czf package.tar.gz readme.txt");
////                  shell.execute("chmod 600 package.tar.gz");
//               }
//            });
//            
//            System.out.println(ssh.getAuthenticationMethods());            
//            
//            if(ssh.isAuthenticated()) { 
//               System.out.println("Authenticated"); 
//            } else { 
//               System.out.println("Authentication failed"); 
//            }
//            
//            ssh.disconnect();
//         }
//
//      } catch (IOException e) {
//          e.printStackTrace();
//      }
//      catch (Exception e)
//      {
//          e.printStackTrace();
//      }
   }
}