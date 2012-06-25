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

import dk.deck.remoteconsole.userinfo.CachingUserInfo;
import com.jcraft.jsch.UserInfo;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author Jesper Terkelsen
 */
public class CachingUserInfoTest {
    
    public CachingUserInfoTest() {
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
     * Test of getPassphrase method, of class CachingUserInfo.
     */
    @Test
    public void testGetPassphrase() {
        System.out.println("getPassphrase");
        CachingUserInfo instance = new CachingUserInfo(new UserInfo() {

            @Override
            public String getPassphrase() {
                System.out.println("getPassphrase()");
                return "1234";
            }

            @Override
            public String getPassword() {
                return "12345";
            }

            @Override
            public boolean promptPassword(String string) {
                System.out.println(string);
                return true;
            }

            @Override
            public boolean promptPassphrase(String string) {
                System.out.println(string);
                return true;
            }

            @Override
            public boolean promptYesNo(String string) {
                System.out.println(string);
                return true;
            }

            @Override
            public void showMessage(String string) {
                System.out.println(string);
            }
        });
        String expResult = "1234";
        instance.promptPassphrase("emu");
        String result = instance.getPassphrase();
        assertEquals(expResult, result);
        instance.promptPassphrase("emu");
        result = instance.getPassphrase();
        assertEquals(expResult, result);
        instance.promptPassphrase("hat");
        result = instance.getPassphrase();
        assertEquals(expResult, result);
        instance.promptPassphrase("hat");
        result = instance.getPassphrase();
        assertEquals(expResult, result);
    }

}
