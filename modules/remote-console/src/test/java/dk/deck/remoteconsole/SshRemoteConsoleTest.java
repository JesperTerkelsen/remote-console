/*
 * Copyright 2011 Jesper Terkelsen.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 */
package dk.deck.remoteconsole;

import dk.deck.remoteconsole.SshRemoteConsole;
import dk.deck.remoteconsole.CommandResult;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import static org.junit.Assert.*;

/**
 *
 * @author Jesper Terkelsen
 */
public class SshRemoteConsoleTest {

    public SshRemoteConsoleTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    private SshRemoteConsole instance;
    private ChannelExec exec;

    @Before
    public void setUp() throws JSchException {
        JSch jsch = Mockito.mock(JSch.class);
        Session session = Mockito.mock(Session.class);
        ChannelShell shell = Mockito.mock(ChannelShell.class);
        exec = Mockito.mock(ChannelExec.class);
        instance = new SshRemoteConsole(jsch);
        instance.setIdentityFile(new File(""));
        Mockito.when(jsch.getSession(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt())).thenReturn(session);
        Mockito.when(session.openChannel("shell")).thenReturn(shell);
        Mockito.when(session.openChannel("exec")).thenReturn(exec);
        Mockito.when(session.isConnected()).thenReturn(true);
    }

    @After
    public void tearDown() {
        instance = null;
        exec = null;
    }

    /**
     * Test of setUser method, of class SshRemoteConsole.
     */
    @Test
    public void testSetUser() {
        System.out.println("setUser");
        String user = "";
        instance.setUser(user);
    }

    /**
     * Test of getUser method, of class SshRemoteConsole.
     */
    @Test
    public void testGetUser() {
        System.out.println("getUser");
        String expResult = "jt";
        instance.setUser(expResult);
        String result = instance.getUser();
        assertEquals(expResult, result);
    }

    /**
     * Test of setHost method, of class SshRemoteConsole.
     */
    @Test
    public void testSetHost() {
        System.out.println("setHost");
        String host = "";
        instance.setHost(host);
    }

    /**
     * Test of getHost method, of class SshRemoteConsole.
     */
    @Test
    public void testGetHost() {
        System.out.println("getHost");
        String expResult = "emu.deck.dk";
        instance.setHost(expResult);
        String result = instance.getHost();
        assertEquals(expResult, result);
    }

    /**
     * Test of setPort method, of class SshRemoteConsole.
     */
    @Test
    public void testSetPort() {
        System.out.println("setPort");
        int port = 0;
        instance.setPort(port);
    }

    /**
     * Test of getPort method, of class SshRemoteConsole.
     */
    @Test
    public void testGetPort() {
        System.out.println("getPort");
        int expResult = 0;
        int result = instance.getPort();
        assertEquals(expResult, result);
    }

    /**
     * Test of setIdentityFile method, of class SshRemoteConsole.
     */
    @Test
    public void testSetIdentityFile() {
        System.out.println("setIdentityFile");
        File identityFile = null;
        instance.setIdentityFile(identityFile);
    }

    /**
     * Test of getIdentityFile method, of class SshRemoteConsole.
     */
    @Test
    public void testGetIdentityFile() {
        System.out.println("getIdentityFile");
        File expResult = new File("");
        instance.setIdentityFile(expResult);
        File result = instance.getIdentityFile();
        assertEquals(expResult, result);
    }

    /**
     * Test of executeCommandResult method, of class SshRemoteConsole.
     */
    @Test
    public void testExecuteCommandResult() throws Exception {
        System.out.println("executeCommandResult");
        String commandOutput = "executed";
        int outputcode = 0;
        ByteArrayInputStream input = new ByteArrayInputStream(commandOutput.getBytes());
        Mockito.when(exec.getInputStream()).thenReturn(input);
        Mockito.when(exec.getErrStream()).thenReturn(new ByteArrayInputStream("".getBytes()));
        Mockito.when(exec.getExitStatus()).thenReturn(outputcode);
        // This might cause the output not to be completely read
        // Exactely 1024 bytes are read before this method is called.
        Mockito.when(exec.isClosed()).thenReturn(Boolean.TRUE);

        String command = "ls -lah";
        boolean failOnExitNotZero = false;
        long disconnectAfterMillis = 0L;
        String afterContent = null;
        CommandResult result = instance.executeCommandResult(command, failOnExitNotZero, disconnectAfterMillis, afterContent);
        assertEquals(commandOutput, result.getOutput());
        assertEquals(outputcode, result.getExitCode());
    }

    /**
     * Test of openShell method, of class SshRemoteConsole.
     */
    @Test
    public void testOpenShell() throws Exception {
        System.out.println("openShell");
        instance.connect();
        ChannelShell result = instance.openShell();
        instance.disconnect();
        assertTrue(result != null);
    }

    private void initMockForFileUpload() throws IOException{
        byte[] acknoglements = new byte[]{0,0,0};
        ByteArrayInputStream input = new ByteArrayInputStream(acknoglements);
        Mockito.when(exec.getInputStream()).thenReturn(input);
    }

    /**
     * Test of uploadFile method, of class SshRemoteConsole.
     */
    @Test
    public void testUploadFile_URL_String() throws Exception {
        System.out.println("uploadFile");
        initMockForFileUpload();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Mockito.when(exec.getOutputStream()).thenReturn(out);
        URL lfile = getClass().getResource("testfile.txt");
        String rfile = "text.txt";
        instance.uploadFile(lfile, rfile);
        assertTrue(out.toString().contains("C0644 28 testfile.txt"));
    }

    /**
     * Test of uploadFile method, of class SshRemoteConsole.
     */
    @Test
    public void testUploadFile_3args() throws Exception {
        System.out.println("uploadFile");
        initMockForFileUpload();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Mockito.when(exec.getOutputStream()).thenReturn(out);
        URL lfile = getClass().getResource("testfile.txt");;
        String rfile = "text.txt";
        String command = "sudo scp -p -t " + rfile;
        instance.uploadFile(lfile, rfile, command);
        assertTrue(out.toString().contains("C0644 28 testfile.txt"));
    }

    /**
     * Test of connect method, of class SshRemoteConsole.
     */
    @Test
    public void testConnect() throws Exception {
        System.out.println("connect");
        instance.connect();
    }

    /**
     * Test of disconnect method, of class SshRemoteConsole.
     */
    @Test
    public void testDisconnect() throws IOException {
        System.out.println("disconnect");
        instance.connect(); // You have to connecto to disconnect
        instance.disconnect();
    }

}