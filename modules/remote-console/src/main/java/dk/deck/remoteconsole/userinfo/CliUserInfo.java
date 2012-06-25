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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * A userinfo implementation that ask questions on the CLI
 * 
 * @author Jesper Terkelsen
 */
public class CliUserInfo implements UserInfo {
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
      
    public String getPassphrase() {
        try {
            return br.readLine();
        } catch (IOException ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public String getPassword() {
        try {
            return br.readLine();
        } catch (IOException ex) {
            ex.printStackTrace();
            return "";
        }        
    }

    public boolean promptPassword(String message) {
        System.out.println(message);
        return true;
    }

    public boolean promptPassphrase(String message) {
        System.out.println(message);
        return true;
    }

    public boolean promptYesNo(String message) {
        System.out.println(message);        
        try {
            String answer = br.readLine();
            if (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes")){
                return true;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }        
        return false;        
    }

    public void showMessage(String message) {
        System.out.println(message);
    }

}
