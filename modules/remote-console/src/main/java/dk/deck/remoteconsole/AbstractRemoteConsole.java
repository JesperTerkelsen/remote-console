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

import dk.deck.console.CommandResult;
import java.io.IOException;
import java.net.URL;

/**
 * This is a marker that indicates that the implementation is a real or mock but not a wrapper
 * @author Jesper Terkelsen
 */
public abstract class AbstractRemoteConsole implements RemoteConsole {

    @Override
    public String executeCommandNoSudo(String command) throws IOException {
        return executeCommand(command);
    }

    @Override
    public String executeCommand(String command) throws IOException {
        return executeCommand(command, true);
    }

    @Override
    public CommandResult executeCommandResult(String command) throws IOException {
        return executeCommandResult(command, false, 0);
    }

    @Override
    public String executeCommand(String command, boolean failOnExitNotZero) throws IOException {
        CommandResult result = executeCommandResult(command, failOnExitNotZero, 0);
        return result.getOutput();
    }

    @Override
    public void executeCommandAndDisconnect(String command, long disconnectAfterMillis) throws IOException {
        executeCommandResult(command, true, disconnectAfterMillis);
    }

    @Override
    public void executeCommandAndDisconnect(String command, long disconnectAfterMillis, String afterContent) throws IOException {
        executeCommandResult(command, true, disconnectAfterMillis, afterContent);
    }

    @Override
    public CommandResult executeCommandResult(String command, boolean failOnExitNotZero) throws IOException {
        return executeCommandResult(command, failOnExitNotZero, 0);
    }

    @Override
    public CommandResult executeCommandResult(String command, boolean failOnExitNotZero, long disconnectAfterMillis) throws IOException {
        return executeCommandResult(command, failOnExitNotZero, disconnectAfterMillis, null);
    }

    public abstract void uploadFile(URL lfile, String rfile, String command) throws IOException;
}
