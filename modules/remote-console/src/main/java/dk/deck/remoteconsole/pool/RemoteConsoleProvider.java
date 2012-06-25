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
package dk.deck.remoteconsole.pool;

import java.io.IOException;
import dk.deck.remoteconsole.RemoteConsole;

/**
 * A provider for a remote console, which can be shared among services.
 * 
 * The connection will be deplayed until someone calls get getConnection();
 * 
 * Disconnect will disconnect the remote console, if it was ever handed out
 * 
 * @author Jesper Terkelsen
 */
public interface RemoteConsoleProvider {
    public RemoteConsole getConnection() throws IOException;
    public void disconnect() throws IOException;
}
