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
package dk.deck.remoteconsole.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author Jesper Terkelsen
 */
public class StreamUtil {


    /**
     * Reads all data from the stream and close the stream.
     * @return The number of bytes read
     */
    public static long messureContentLenth(InputStream in) throws UnsupportedEncodingException, IOException {
        byte[] buffer = new byte[1024];
        long totalLength = 0L;
        while (true) {
            int len = in.read(buffer, 0, buffer.length);
            if (len <= 0) {
                break;
            }
            totalLength += len;
        }
        in.close();
        return totalLength;
    }

}
