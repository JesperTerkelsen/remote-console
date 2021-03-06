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
import dk.deck.console.Console;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

/**
 *
 * @author Jesper Terkelsen
 */
public interface RemoteConsole extends Console{

    void executeCommandAndDisconnect(String command, long disconnectAfterMillis) throws IOException;

    CommandResult executeCommandResult(String command, boolean failOnExitNotZero, long disconnectAfterMillis) throws IOException;

    CommandResult executeCommandResult(String command, boolean failOnExitNotZero, long disconnectAfterMillis, String afterContent) throws IOException;


    /**
     * Uploads the file from to the server.
     * Overwrites if the file already exists
     * 
     * @param lfile Local file to upload
     * @param rfile Remote file location
     * @throws java.io.IOException
     */
    void uploadFile(URL lfile, String rfile) throws IOException;

    // Login information, remember to set those 

    String getHost();

    File getIdentityFile();

    int getPort();

    String getUser();

    void setHost(String host);

    void setIdentityFile(File identityFile);

    void setPort(int port);

    void setUser(String user);

    void connect() throws IOException;

    void disconnect();

    String executeCommandNoSudo(String command) throws IOException;

    void executeCommandAndDisconnect(String command, long disconnectAfterMillis, String afterContent) throws IOException;

    ChannelShell openShell() throws IOException;

    void downloadFile(String rfile, OutputStream finalOutput) throws IOException;

    boolean isConnected();

}
