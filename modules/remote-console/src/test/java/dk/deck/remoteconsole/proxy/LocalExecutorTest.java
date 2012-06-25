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

import dk.deck.remoteconsole.proxy.LocalExecutor;
import java.io.IOException;
import java.io.PrintStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jesper Terkelsen
 */
public class LocalExecutorTest {
    
    public LocalExecutorTest() {
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
     * Test of execute method, of class LocalExecutor.
     */
    @Test
    public void testExecute() throws IOException, InterruptedException {
        System.out.println("execute");
        String command = "date";
        String[] arguments = new String[]{};
        PrintStream out = System.out;
        PrintStream error = System.err;
        LocalExecutor instance = new LocalExecutor();
        instance.execute(command, arguments, out, error);
    }
    
    /**
     * Test of execute method, of class LocalExecutor.
     */
    @Test(expected=IllegalStateException.class)
    public void testExecuteFail() throws IOException, InterruptedException {
        System.out.println("execute");
        String command = "date";
        String[] arguments = new String[]{"-y"};
        PrintStream out = System.out;
        PrintStream error = System.err;
        LocalExecutor instance = new LocalExecutor();
        instance.execute(command, arguments, out, error);
    }    
}
