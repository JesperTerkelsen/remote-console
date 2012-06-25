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
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A caching user info will remember answered passphrases in a session, 
 * and send the same answer for the same question again.
 * 
 * This is assuming that JSCH sends the same message for promptPassphrase() 
 * per key, currently it works like this.
 * 
 * Passwords are not cached.
 * 
 * This user info needs an other userinfo which will provide the 
 * communication with the user.
 * 
 * Use the invalidateCache() method to empty saved passphrases.
 * In that way you can created a timed authentication like the sudo command.
 * 
 * @author Jesper Terkelsen
 */
public class CachingUserInfo implements UserInfo{
    private static Log log = LogFactory.getLog(CachingUserInfo.class);

    private final UserInfo proxy;
    // The userinfo is created for each instance, so we need a global cache
    private static Map<String, String> passphraseCache = new HashMap<String, String>();
    private String lastMessage = null;
    
    public CachingUserInfo(UserInfo proxy) {
        this.proxy = proxy;
    }

    @Override
    public String getPassphrase() {
        if (lastMessage != null){
            String cached = passphraseCache.get(lastMessage);
            if (cached != null){
                // log.debug("using cached passphrase");
                return cached;
            }
            else {
                cached = proxy.getPassphrase();
                passphraseCache.put(lastMessage, cached);
                // log.debug("caching passphrase future use");
                return cached;
            }
        }
        return proxy.getPassphrase();
    }

    @Override
    public String getPassword() {
        return proxy.getPassword();
    }

    @Override
    public boolean promptPassword(String message) {
        return proxy.promptPassword(message);
    }

    @Override
    public boolean promptPassphrase(String message) {
        lastMessage = message;
        return proxy.promptPassphrase(message);
    }

    @Override
    public boolean promptYesNo(String string) {
        return proxy.promptYesNo(string);
    }

    @Override
    public void showMessage(String string) {
        proxy.showMessage(string);
    }
    
    /**
     * Empties the cached passphrases, will cause the GUI to pop up again.
     */
    public void invalidateCache(){
        passphraseCache.clear();
    }
}
