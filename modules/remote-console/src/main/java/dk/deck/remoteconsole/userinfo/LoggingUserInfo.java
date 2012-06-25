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
package dk.deck.remoteconsole.userinfo;

import com.jcraft.jsch.UserInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Jesper Terkelsen
 */
public class LoggingUserInfo implements UserInfo {

    private static Log log = LogFactory.getLog(LoggingUserInfo.class);

    public String getPassphrase() {
        log.info("getPassphrase");
        return "";
    }

    public String getPassword() {
        log.info("getPassword");
        return "";
    }

    public boolean promptPassword(String password) {
        log.info("Password: " + password);
        return false;
    }

    public boolean promptPassphrase(String message) {
        log.info("PassPhrase: " + message);
        return false;
    }

    public boolean promptYesNo(String message) {
        log.debug(message + " Yes/No? (returning Yes)");
        return true;
    }

    public void showMessage(String message) {
        log.info("Message " + message);
    }
}
