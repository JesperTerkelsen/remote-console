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
package dk.deck.remoteconsole.proxy;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import dk.deck.remoteconsole.AbstractRemoteConsole;
import dk.deck.remoteconsole.CommandResult;
import dk.deck.remoteconsole.SshRemoteConsole;

/**
 * A proxy remote console, uses SSH to upload itself to the .remoteconsole
 * folder. Then it starts itself, and takes input from the client via the
 * standard in.
 *
 * This gives a performance gain because we do not create a ssh channel for each
 * command.
 *
 * The alternative to this is to use a shell directly from java, but in that
 * case we do not have 100% control over the protocol
 *
 *
 * @author Jesper Terkelsen
 */
public class ProxyRemoteConsole extends AbstractRemoteConsole {

    private static final String INSTALL_DIR = ".remoteconsole";
    private SshRemoteConsole client;

    public ProxyRemoteConsole(SshRemoteConsole client) {
        this.client = client;
    }

    @Override
    public String getHost() {
        return client.getHost();
    }

    @Override
    public File getIdentityFile() {
        return client.getIdentityFile();
    }

    @Override
    public int getPort() {
        return client.getPort();
    }

    @Override
    public String getUser() {
        return client.getUser();
    }

    @Override
    public void setHost(String host) {
        client.setHost(host);
    }

    @Override
    public void setIdentityFile(File identityFile) {
        client.setIdentityFile(identityFile);
    }

    @Override
    public void setPort(int port) {
        client.setPort(port);
    }

    @Override
    public void setUser(String user) {
        client.setUser(user);
    }

    @Override
    public void connect() throws IOException {
        client.connect();
        // Test that proxy is installed
        // Make sure to install it if not
        // Start the running service
        client.executeCommand("mkdir -p .remoteconsole");
        // client.uploadFile(, INSTALL_DIR);
    }

    @Override
    public void disconnect() {
        // Stop the running service
        client.disconnect();
    }

    @Override
    public ChannelShell openShell() throws IOException {
        return client.openShell();
    }

    @Override
    public void downloadFile(String rfile, OutputStream finalOutput) throws IOException {
        client.downloadFile(rfile, finalOutput);
    }

    @Override
    public void uploadFile(URL lfile, String rfile, String command) throws IOException {
        client.uploadFile(lfile, rfile, command);
    }

    @Override
    public void uploadFile(URL lfile, String rfile) throws IOException {
        client.uploadFile(lfile, rfile);
    }

    @Override
    public CommandResult executeCommandResult(String command, boolean failOnExitNotZero, long disconnectAfterMillis, String afterContent) throws IOException {
        // client.executeCommand();
        // Call the running serivce to execute a command
        System.out.println("Should execute " + command);
        return null;
    }

    @Override
    public boolean isConnected() {
        return client.isConnected();
    }
}
