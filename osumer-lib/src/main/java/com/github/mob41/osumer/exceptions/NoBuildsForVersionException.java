/*******************************************************************************
 * Any modification, copies of sections of this file must be attached with this
 * license and shown clearly in the developer's project. The code can be used
 * as long as you state clearly you do not own it. Any violation might result in
 *  a take-down.
 *
 * MIT License
 *
 * Copyright (c) 2016, 2017 Anthony Law
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
package com.github.mob41.osumer.exceptions;

import com.github.mob41.organdebug.exceptions.DebuggableException;

public class NoBuildsForVersionException extends DebuggableException {

    /**
     * 
     */
    private static final long serialVersionUID = 4508589028039107867L;
    private static final String MSG = "No builds are defined in the version JSON.";

    public NoBuildsForVersionException(String json, String last_op, String this_op, String next_op) {
        super(json, last_op, this_op, next_op, MSG, false);
    }

    public NoBuildsForVersionException(String json, String last_op, String this_op, String next_op, String arg0) {
        super(json, last_op, this_op, next_op, MSG, false, arg0);
    }

    public NoBuildsForVersionException(String json, String last_op, String this_op, String next_op, Throwable arg0) {
        super(json, last_op, this_op, next_op, MSG, false, arg0);
    }

    public NoBuildsForVersionException(String json, String last_op, String this_op, String next_op, String arg0,
            Throwable arg1) {
        super(json, last_op, this_op, next_op, MSG, false, arg0, arg1);
    }

    public NoBuildsForVersionException(String json, String last_op, String this_op, String next_op, String arg0,
            Throwable arg1, boolean arg2, boolean arg3) {
        super(json, last_op, this_op, next_op, MSG, false, arg0, arg1, arg2, arg3);
    }

}
