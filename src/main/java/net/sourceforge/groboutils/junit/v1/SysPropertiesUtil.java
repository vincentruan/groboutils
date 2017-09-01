/*
 * @(#)SysPropertiesUtil.java
 *
 * Copyright (C) 2002-2003 Matt Albrecht
 * groboclown@users.sourceforge.net
 * http://groboutils.sourceforge.net
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the 
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in 
 *  all copies or substantial portions of the Software. 
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL 
 *  THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
 *  DEALINGS IN THE SOFTWARE.
 */

package net.sourceforge.groboutils.junit.v1;

import java.util.Properties;

//import java.lang.reflect.Method;
//import java.lang.reflect.InvocationTargetException;


/**
 * Utility that allows for easy setting and reseting of System properties.
 * Some classes that need testing may depend upon a System property setting,
 * and this class will help testing that.  This is JDK 1.1 and above
 * compatible.
 * <p>
 * Each instance of this utility should be created in the <tt>setUp()</tt>
 * method of the test case, then the utility should have its <tt>reset()</tt>
 * method called in the <tt>tearDown()</tt> method.  This ensures that the
 * standard system properties have been set correctly.
 *
 * @author Matt Albrecht <a href="mailto:groboclown@users.sourceforge.net">groboclown@users.sourceforge.net</a>
 * @version $Date: 2003/07/21 13:57:54 $
 * @since December 26, 2002
 */
public class SysPropertiesUtil {
    private Properties originals;
    private Properties newProps;


    /**
     * Each instance should be an instance variable for a test class, and
     * it should be created in the <tt>setUp()</tt> method.
     */
    public SysPropertiesUtil() {
        this.originals = System.getProperties();
        restoreProps();
    }


    /**
     * Sets the system property.  If the new value is <tt>null</tt>, then
     * the property, if it exists, will be removed.  If the key is
     * <tt>null</tt>, then an <tt>IllegalArgumentException</tt> will be thrown.
     */
    public void setValue(String key, String newVal) {
        if (key == null) {
            throw new IllegalArgumentException("No null keys.");
        }
        synchronized (this) {
            if (newVal == null) {
                this.newProps.remove(key);
            } else {
                this.newProps.setProperty(key, newVal);
            }
            setSystemProperties(this.newProps);
        }
    }


    /**
     * Resets the system properties to the way they were when this class was
     * created.
     */
    public synchronized void reset() {
        setSystemProperties(this.originals);
        restoreProps();
    }


    private void setSystemProperties(Properties props) {
        System.setProperties(props);
    }


    private void restoreProps() {
        this.newProps = (Properties) this.originals.clone();
    }
}

