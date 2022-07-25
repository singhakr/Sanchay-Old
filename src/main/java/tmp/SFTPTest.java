/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tmp;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 *
 * @author User
 */
public class SFTPTest {
//    @Test
    public void sftpTest() {
        String username = "xxx";
        String host = "xxx";
        String privateKeyFile = "id_dsa";
        String knownHostsFile = "known_hosts";
        String fileName = "/remote/file/name.txt";
        int port = 22;

        JSch jsch = new JSch();
        Session session = null;
        Channel channel = null;
        try {
            jsch.addIdentity(privateKeyFile);
            jsch.setKnownHosts(knownHostsFile);
            session = jsch.getSession(username, host, port);
            session.setTimeout(20 * 1000);
            session.connect();

            channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp channelSftp = (ChannelSftp) channel;

            System.out.println("Starting File download");
            InputStream is = channelSftp.get(fileName);
            IOUtils.copy(is, System.out);
            is.close();

        } catch (JSchException | SftpException exception) {
            exception.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }

    @Test
    public void lsTest() {
        String username = "xxx";
        String host = "xxx";
        String privateKeyFile = "id_dsa";
        String knownHostsFile = "known_hosts";
        String dirName = "/remote/dir/name";
        int port = 22;

        JSch jsch = new JSch();
        Session session = null;
        Channel channel = null;
        try {
            jsch.addIdentity(privateKeyFile);
            jsch.setKnownHosts(knownHostsFile);
            session = jsch.getSession(username, host, port);
            session.setTimeout(20 * 1000);
            session.connect();

            channel = session.openChannel("exec");
            ChannelExec channelExec = (ChannelExec) channel;

            System.out.println("Executing ls");
//            channelExec.setCommand("ls " + dirName);
            channelExec.setCommand("find " + dirName + " -maxdepth 1 -type f -printf '%f\\n'");
            channel.connect();

            InputStream is = channelExec.getInputStream();
            IOUtils.copy(is, System.out);
            is.close();

            System.out.println("Exit status: " + channel.getExitStatus());


        } catch (JSchException exception) {
            exception.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }
}