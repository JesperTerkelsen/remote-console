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

import dk.deck.remoteconsole.proxy.ProxyRemoteConsole;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.UserInfo;
import dk.deck.remoteconsole.userinfo.LoggingUserInfo;
import dk.deck.remoteconsole.SshRemoteConsole;
import dk.deck.remoteconsole.UserInfoProvider;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Jesper Terkelsen
 */
public class ProxyRemoteConsoleTest {
    
    public ProxyRemoteConsoleTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }


    /**
     * Test of connect method, of class ProxyRemoteConsole.
     */
    @Test
    public void testConnect() throws Exception {
        System.out.println("connect");
        SshRemoteConsole remote = new SshRemoteConsole(new JSch());
        remote.setUserInfoProvider(new UserInfoProvider() {

            @Override
            public UserInfo getUserInfo() {
                return new LoggingUserInfo(){

                    @Override
                    public String getPassword() {
                        return "deployer";
                    }

                    @Override
                    public boolean promptPassword(String password) {
                        return true;
                    }
                    
                };
            }
        });
        ProxyRemoteConsole instance = new ProxyRemoteConsole(remote);
        instance.setHost("test.deck.dk");
        instance.setUser("deployer");
        instance.setIdentityFile(null);
        // instance.connect();
    }

    /**
     * Test of disconnect method, of class ProxyRemoteConsole.
     */
    @Test
    public void testDisconnect() {
        System.out.println("disconnect");
        // ProxyRemoteConsole instance = null;
        // instance.disconnect();
    }

}
