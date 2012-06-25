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

import dk.deck.remoteconsole.QuestionAnswerer;
import com.jcraft.jsch.ChannelShell;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import static org.junit.Assert.*;

/**
 *
 * @author Jesper Terkelsen
 */
public class QuestionAnswererTest {

    public QuestionAnswererTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    private QuestionAnswerer instance;
    private ByteArrayOutputStream outputStream;

    @Before
    public void setUp() throws IOException {
        instance = new QuestionAnswerer();
        instance.init(getShell());
        instance.start();
    }

    private ChannelShell getShell() throws IOException{
        ChannelShell shell = Mockito.mock(ChannelShell.class);
        String data = "data";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data.getBytes());
        outputStream = new ByteArrayOutputStream();
        Mockito.when(shell.getInputStream()).thenReturn(inputStream);
        Mockito.when(shell.getOutputStream()).thenReturn(outputStream);
        return shell;
    }

    @After
    public void tearDown() throws IOException {
        instance.stop();
        instance = null;
        outputStream = null;
    }

    /**
     * Test of init method, of class QuestionAnswerer.
     */
    @Test
    public void testInit() throws Exception {
        System.out.println("init");
        instance.init(getShell());
    }

    /**
     * Test of start method, of class QuestionAnswerer.
     */
    @Test
    public void testStart() throws Exception {
        System.out.println("start");
        // Start was already called in setUp();
    }

    /**
     * Test of stop method, of class QuestionAnswerer.
     */
    @Test
    public void testStop() throws Exception {
        System.out.println("stop");
        instance.stop(); // It is ok to call stop multible times
    }

    /**
     * Test of waitFor method, of class QuestionAnswerer.
     */
    @Test
    public void testWaitFor_String() {
        System.out.println("waitFor");
        String waitfor = "data";
        instance.waitFor(waitfor);
    }

    /**
     * Test of waitFor method, of class QuestionAnswerer.
     */
    @Test
    public void testWaitFor_String_String() {
        System.out.println("waitFor");
        String prefered = "data";
        String alternate = "emu";
        boolean expResult = true;
        boolean result = instance.waitFor(prefered, alternate);
        assertEquals(expResult, result);
    }

    /**
     * Test of write method, of class QuestionAnswerer.
     */
    @Test
    public void testWrite() throws Exception {
        System.out.println("write");
        String data = "emu";
        instance.write(data);
        assertTrue(new String(outputStream.toByteArray()).equals(data));
    }

    /**
     * Test of sleep method, of class QuestionAnswerer.
     */
    @Test
    public void testSleep() {
        System.out.println("sleep");
        long millis = 0L;
        instance.sleep(millis);
    }

}