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

import com.jcraft.jsch.ChannelShell;
import dk.deck.console.CommandResult;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Jesper Terkelsen
 */
public class AdminRemoteConsole implements RemoteConsole{

    private String sudo = "sudo ";
    private AbstractRemoteConsole wrapped;

    public AdminRemoteConsole(AbstractRemoteConsole wrapped) {
        this.wrapped = wrapped;
    }

    public String executeCommandNoSudo(String command) throws IOException {
        return wrapped.executeCommand(command);
    }

    public String executeCommand(String command) throws IOException {
        return wrapped.executeCommand(sudo + command);
    }

    public String executeCommand(String command, boolean failOnExitNotZero) throws IOException {
        return wrapped.executeCommand(sudo + command, failOnExitNotZero);
    }

    public CommandResult executeCommandResult(String command) throws IOException {
        return wrapped.executeCommandResult(sudo + command);
    }

    public CommandResult executeCommandResult(String command, boolean failOnExitNotZero) throws IOException {
        return wrapped.executeCommandResult(sudo + command, failOnExitNotZero);
    }

    public void executeCommandAndDisconnect(String command, long disconnectAfterMillis) throws IOException {
        wrapped.executeCommandAndDisconnect(sudo + command, disconnectAfterMillis);
    }

    public CommandResult executeCommandResult(String command, boolean failOnExitNotZero, long disconnectAfterMillis) throws IOException {
        return wrapped.executeCommandResult(sudo + command, failOnExitNotZero, disconnectAfterMillis);
    }

    public CommandResult executeCommandResult(String command, boolean failOnExitNotZero, long disconnectAfterMillis, String afterContent) throws IOException {
        return wrapped.executeCommandResult(sudo + command, failOnExitNotZero, disconnectAfterMillis, afterContent);
    }

    public void executeCommandAndDisconnect(String command, long disconnectAfterMillis, String afterContent) throws IOException {
        wrapped.executeCommandAndDisconnect(sudo + command, disconnectAfterMillis, afterContent);
    }


    public void uploadFile(URL lfile, String rfile) throws IOException {
        wrapped.uploadFile(lfile, rfile, sudo + "scp -p -t " + rfile);
    }

    public void uploadFile(URL lfile, String rfile, String command) throws IOException {
        wrapped.uploadFile(lfile, rfile, sudo + command);
    }

    public void downloadFile(String rfile, OutputStream finalOutput) throws IOException {
        wrapped.downloadFile(rfile, finalOutput);
    }
    
    public String getHost() {
        return wrapped.getHost();
    }

    public File getIdentityFile() {
        return wrapped.getIdentityFile();
    }

    public int getPort() {
        return wrapped.getPort();
    }

    public String getUser() {
        return wrapped.getUser();
    }

    public void setHost(String host) {
        wrapped.setHost(host);
    }

    public void setIdentityFile(File identityFile) {
        wrapped.setIdentityFile(identityFile);
    }

    public void setPort(int port) {
        wrapped.setPort(port);
    }

    public void setUser(String user) {
        wrapped.setUser(user);
    }

    public void connect() throws IOException {
        wrapped.connect();
    }

    public void disconnect() {
        wrapped.disconnect();
    }

    public ChannelShell openShell() throws IOException {
        return wrapped.openShell();
    }

    @Override
    public boolean isConnected() {
        return wrapped.isConnected();
    }
    
    public static String wrapCommand(String command){
        command = command.replaceAll(Pattern.quote("\""), Matcher.quoteReplacement("\\\""));
        return "sudo su - root -c \""+command+"\"";
    }



}
