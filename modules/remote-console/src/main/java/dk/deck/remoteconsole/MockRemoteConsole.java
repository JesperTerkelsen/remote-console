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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jesper Terkelsen
 */
public class MockRemoteConsole extends AbstractRemoteConsole {

    private List<CommandResult> testOutputQueue = new ArrayList<CommandResult>();

    public void addCommandResult(String output) {
        CommandResult next = new CommandResult(output, 0);
        testOutputQueue.add(next);
    }

    public void addCommandResult(String output, int exitValue) {
        CommandResult next = new CommandResult(output, exitValue);
        testOutputQueue.add(next);
    }

    public CommandResult getNextResult() {
        if (testOutputQueue.isEmpty()) {
            return new CommandResult("", 0);
        } else {
            return testOutputQueue.remove(0);
        }
    }

    public CommandResult executeCommandResult(String command, boolean failOnExitNotZero, long disconnectAfterMillis, String afterContent) throws IOException {
        System.out.println("> " + command);
        return getNextResult();
    }

    public void uploadFile(URL lfile, String rfile) throws IOException {
        System.out.println("uploading file > " + lfile + " to " + rfile);
    }

    public void uploadFile(URL lfile, String rfile, String command) throws IOException {
        System.out.println("uploading file > " + lfile + " to " + rfile + " using command " + command);
    }

    public void downloadFile(String rfile, OutputStream finalOutput) throws IOException {
        System.out.println("downloading file > " + rfile );
    }
    
    public String getHost() {
        return "";
    }

    public File getIdentityFile() {
        return null;
    }

    public int getPort() {
        return 0;
    }

    public String getUser() {
        return "";
    }

    public void setHost(String host) {
    }

    public void setIdentityFile(String identityFile) {
    }

    public void setPort(int port) {
    }

    public void setUser(String user) {
    }

    public void connect() throws IOException {
    }

    public void disconnect() {
    }

    public void setIdentityFile(File identityFile) {
    }

    public ChannelShell openShell() throws IOException {
        return null;
    }

    @Override
    public boolean isConnected() {
        return false;
    }
    
    

}
