/*
 * @(#)AssertConstructor.java
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

import org.junit.Assert;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;


/**
 * A set of assert methods that test a class for implementation of specific
 * constructor types.
 *
 * @author Matt Albrecht <a href="mailto:groboclown@users.sourceforge.net">groboclown@users.sourceforge.net</a>
 * @version $Date: 2003/02/10 22:52:19 $
 * @since July 21, 2002
 */
public class AssertConstructor {
    /**
     * Bit-mask that allows for the constructor to have public access.
     */
    public static final int PUBLIC = 0x01;

    /**
     * Bit-mask that allows for the constructor to have protected access.
     */
    public static final int PROTECTED = 0x02;

    /**
     * Bit-mask that allows for the constructor to have package-private access.
     */
    public static final int PACKAGE = 0x04;

    /**
     * Bit-mask that allows for the constructor to have private access.
     */
    public static final int PRIVATE = 0x08;

    /**
     * Bit-mask that allows the constructor to have any protection.
     */
    public static final int ANY_PROTECTION =
            PUBLIC | PROTECTED | PACKAGE | PRIVATE;


    /**
     * Ensures that the class <tt>c</tt> has a default (no-arg), public
     * constructor.
     *
     * @param message message that describes what failed if the assertion
     *                fails.
     * @param c       the class check for a constructor.
     */
    public static void assertHasDefaultConstructor(String message, Class c) {
        assertHasSameConstructor(message, c, new Class[0]);
    }


    /**
     * Ensures that the class <tt>c</tt> has a default (no-arg), public
     * constructor.
     *
     * @param c the class check for a constructor.
     */
    public static void assertHasDefaultConstructor(Class c) {
        assertHasSameConstructor(c, new Class[0]);
    }


    /**
     * Ensures that the class for object <tt>o</tt> has a default (no-arg),
     * public constructor.
     *
     * @param message message that describes what failed if the assertion
     *                fails.
     * @param o       the object to check the class's constructor.
     */
    public static void assertHasDefaultConstructor(String message, Object o) {
        assertHasSameConstructor(message, o, new Class[0]);
    }


    /**
     * Ensures that the class for object <tt>o</tt> has a default (no-arg),
     * public constructor.
     *
     * @param o the object to check the class's constructor.
     */
    public static void assertHasDefaultConstructor(Object o) {
        assertHasSameConstructor(o, new Class[0]);
    }


    /**
     * Ensures that there exists a constructor in class <tt>c</tt> that
     * accepts the same number of arguments given in <tt>arguments</tt>,
     * and that the constructor has the same class or an instance of the
     * class in each position of the argument list.
     *
     * @param message    message that describes what failed if the assertion
     *                   fails.
     * @param c          the class check for a constructor.
     * @param arguments  list of classes which the constructor must have
     *                   as parameters, or subclasses of the arguments.  A <tt>null</tt>
     *                   argument index indicates that any type is allowed (equivalent
     *                   to specifying <tt>Object.class</tt> in the argument).  If
     *                   the array is <tt>null</tt>, then the argument list will only
     *                   match the default (no-argument) constructor (which is the
     *                   same as setting <tt>arguments</tt> to <tt>new Class[0]</tt>).
     * @param protection a bitwise ORed value containing one or more of the
     *                   constants <tt>PUBLIC</tt>, <tt>PROTECTED</tt>, <tt>PACKAGE</tt>,
     *                   or <tt>PRIVATE</tt>.
     * @see #assertHasConstructor(String, Class, Class[])
     * @see #assertHasConstructor(String, Object, Class[], int)
     * @see #assertHasSameConstructor(String, Class, Class[], int)
     */
    public static void assertHasConstructor(String message,
                                            Class c, Class[] arguments, int protection) {
        Assert.assertNotNull("Null class argument.", c);

        Assert.assertNotNull(message,
                getConstructor(c, arguments, protection));
    }


    /**
     * Ensures that there exists a constructor in class <tt>c</tt> that
     * accepts the same number of arguments given in <tt>arguments</tt>,
     * and that the constructor has the same class or an instance of the
     * class in each position of the argument list.
     *
     * @param arguments  list of classes which the constructor must have
     *                   as parameters, or subclasses of the arguments.  A <tt>null</tt>
     *                   argument index indicates that any type is allowed (equivalent
     *                   to specifying <tt>Object.class</tt> in the argument).  If
     *                   the array is <tt>null</tt>, then the argument list will only
     *                   match the default (no-argument) constructor (which is the
     *                   same as setting <tt>arguments</tt> to <tt>new Class[0]</tt>).
     * @param protection a bitwise ORed value containing one or more of the
     *                   constants <tt>PUBLIC</tt>, <tt>PROTECTED</tt>, <tt>PACKAGE</tt>,
     *                   or <tt>PRIVATE</tt>.
     * @see #assertHasConstructor(Class, Class[])
     * @see #assertHasConstructor(Object, Class[], int)
     * @see #assertHasSameConstructor(Class, Class[], int)
     */
    public static void assertHasConstructor(
            Class c, Class[] arguments, int protection) {
        Assert.assertNotNull("Null class argument.", c);

        Assert.assertNotNull(getConstructor(c, arguments, protection));
    }


    /**
     * Ensures that there exists a public constructor in class <tt>c</tt> that
     * accepts the same number of arguments given in <tt>arguments</tt>,
     * and that the constructor has the same class or an instance of the
     * class in each position of the argument list.
     *
     * @param arguments list of classes which the constructor must have
     *                  as parameters, or subclasses of the arguments.  A <tt>null</tt>
     *                  argument index indicates that any type is allowed (equivalent
     *                  to specifying <tt>Object.class</tt> in the argument).  If
     *                  the array is <tt>null</tt>, then the argument list will only
     *                  match the default (no-argument) constructor (which is the
     *                  same as setting <tt>arguments</tt> to <tt>new Class[0]</tt>).
     * @see #assertHasConstructor(String, Class, Class[], int)
     * @see #assertHasConstructor(String, Object, Class[])
     * @see #assertHasSameConstructor(String, Class, Class[])
     */
    public static void assertHasConstructor(String message,
                                            Class c, Class[] arguments) {
        assertHasConstructor(message, c, arguments, PUBLIC);
    }


    /**
     * Ensures that there exists a public constructor in class <tt>c</tt> that
     * accepts the same number of arguments given in <tt>arguments</tt>,
     * and that the constructor has the same class or an instance of the
     * class in each position of the argument list.
     *
     * @param arguments list of classes which the constructor must have
     *                  as parameters, or subclasses of the arguments.  A <tt>null</tt>
     *                  argument index indicates that any type is allowed (equivalent
     *                  to specifying <tt>Object.class</tt> in the argument).  If
     *                  the array is <tt>null</tt>, then the argument list will only
     *                  match the default (no-argument) constructor (which is the
     *                  same as setting <tt>arguments</tt> to <tt>new Class[0]</tt>).
     * @see #assertHasConstructor(Class, Class[], int)
     * @see #assertHasConstructor(Object, Class[])
     * @see #assertHasSameConstructor(Class, Class[])
     */
    public static void assertHasConstructor(
            Class c, Class[] arguments) {
        assertHasConstructor(c, arguments, PUBLIC);
    }


    /**
     * Ensures that there exists a constructor in class <tt>c</tt> that
     * accepts the same number of arguments given in <tt>arguments</tt>,
     * and that the constructor has the same class or an instance of the
     * class in each position of the argument list.
     *
     * @param arguments  list of classes which the constructor must have
     *                   as parameters, or subclasses of the arguments.  A <tt>null</tt>
     *                   argument index indicates that any type is allowed (equivalent
     *                   to specifying <tt>Object.class</tt> in the argument).  If
     *                   the array is <tt>null</tt>, then the argument list will only
     *                   match the default (no-argument) constructor (which is the
     *                   same as setting <tt>arguments</tt> to <tt>new Class[0]</tt>).
     * @param protection a bitwise ORed value containing one or more of the
     *                   constants <tt>PUBLIC</tt>, <tt>PROTECTED</tt>, <tt>PACKAGE</tt>,
     *                   or <tt>PRIVATE</tt>.
     * @see #assertHasConstructor(String, Object, Class[])
     * @see #assertHasConstructor(String, Class, Class[], int)
     * @see #assertHasSameConstructor(String, Object, Class[], int)
     */
    public static void assertHasConstructor(String message,
                                            Object o, Class[] arguments, int protection) {
        Assert.assertNotNull("Null object arguments.", o);
        assertHasConstructor(message, o.getClass(), arguments, protection);
    }


    /**
     * Ensures that there exists a constructor in class <tt>c</tt> that
     * accepts the same number of arguments given in <tt>arguments</tt>,
     * and that the constructor has the same class or an instance of the
     * class in each position of the argument list.
     *
     * @param arguments  list of classes which the constructor must have
     *                   as parameters, or subclasses of the arguments.  A <tt>null</tt>
     *                   argument index indicates that any type is allowed (equivalent
     *                   to specifying <tt>Object.class</tt> in the argument).  If
     *                   the array is <tt>null</tt>, then the argument list will only
     *                   match the default (no-argument) constructor (which is the
     *                   same as setting <tt>arguments</tt> to <tt>new Class[0]</tt>).
     * @param protection a bitwise ORed value containing one or more of the
     *                   constants <tt>PUBLIC</tt>, <tt>PROTECTED</tt>, <tt>PACKAGE</tt>,
     *                   or <tt>PRIVATE</tt>.
     * @see #assertHasConstructor(Object, Class[])
     * @see #assertHasConstructor(Class, Class[], int)
     * @see #assertHasSameConstructor(Object, Class[], int)
     */
    public static void assertHasConstructor(
            Object o, Class[] arguments, int protection) {
        Assert.assertNotNull("Null object arguments.", o);
        assertHasConstructor(o.getClass(), arguments, protection);
    }


    /**
     * Ensures that there exists a public constructor in class <tt>c</tt> that
     * accepts the same number of arguments given in <tt>arguments</tt>,
     * and that the constructor has the same class or an instance of the
     * class in each position of the argument list.
     *
     * @param arguments list of classes which the constructor must have
     *                  as parameters, or subclasses of the arguments.  A <tt>null</tt>
     *                  argument index indicates that any type is allowed (equivalent
     *                  to specifying <tt>Object.class</tt> in the argument).  If
     *                  the array is <tt>null</tt>, then the argument list will only
     *                  match the default (no-argument) constructor (which is the
     *                  same as setting <tt>arguments</tt> to <tt>new Class[0]</tt>).
     * @see #assertHasConstructor(String, Object, Class[], int)
     * @see #assertHasConstructor(String, Class, Class[])
     * @see #assertHasSameConstructor(String, Object, Class[])
     */
    public static void assertHasConstructor(String message,
                                            Object o, Class[] arguments) {
        Assert.assertNotNull("Null object arguments.", o);
        assertHasConstructor(message, o.getClass(), arguments);
    }


    /**
     * Ensures that there exists a public constructor in class <tt>c</tt> that
     * accepts the same number of arguments given in <tt>arguments</tt>,
     * and that the constructor has the same class or an instance of the
     * class in each position of the argument list.
     *
     * @param arguments list of classes which the constructor must have
     *                  as parameters, or subclasses of the arguments.  A <tt>null</tt>
     *                  argument index indicates that any type is allowed (equivalent
     *                  to specifying <tt>Object.class</tt> in the argument).  If
     *                  the array is <tt>null</tt>, then the argument list will only
     *                  match the default (no-argument) constructor (which is the
     *                  same as setting <tt>arguments</tt> to <tt>new Class[0]</tt>).
     * @see #assertHasConstructor(Object, Class[], int)
     * @see #assertHasConstructor(Class, Class[])
     * @see #assertHasSameConstructor(Object, Class[])
     */
    public static void assertHasConstructor(
            Object o, Class[] arguments) {
        Assert.assertNotNull("Null object arguments.", o);
        assertHasConstructor(o.getClass(), arguments);
    }


    //-------------------------------------------------------------------------


    /**
     * Ensures that there exists a constructor in class <tt>c</tt> that
     * accepts the same number of arguments given in <tt>arguments</tt>,
     * and that the constructor has the same class or an instance of the
     * class in each position of the argument list.
     *
     * @param arguments  list of classes which the constructor must have
     *                   as parameters, or subclasses of the arguments.  A <tt>null</tt>
     *                   argument index indicates that any type is allowed (equivalent
     *                   to specifying <tt>Object.class</tt> in the argument).  If
     *                   the array is <tt>null</tt>, then the argument list will only
     *                   match the default (no-argument) constructor (which is the
     *                   same as setting <tt>arguments</tt> to <tt>new Class[0]</tt>).
     * @param protection a bitwise ORed value containing one or more of the
     *                   constants <tt>PUBLIC</tt>, <tt>PROTECTED</tt>, <tt>PACKAGE</tt>,
     *                   or <tt>PRIVATE</tt>.
     * @see #assertHasSameConstructor(String, Class, Class[])
     * @see #assertHasSameConstructor(String, Object, Class[], int)
     * @see #assertHasConstructor(String, Class, Class[], int)
     */
    public static void assertHasSameConstructor(String message,
                                                Class c, Class[] arguments, int protection) {
        Assert.assertNotNull("Null class argument.", c);

        Assert.assertNotNull(message,
                getSameConstructor(c, arguments, protection));
    }


    /**
     * Ensures that there exists a constructor in class <tt>c</tt> that
     * accepts the same number of arguments given in <tt>arguments</tt>,
     * and that the constructor has the same class or an instance of the
     * class in each position of the argument list.
     *
     * @param arguments  list of classes which the constructor must have
     *                   as parameters, or subclasses of the arguments.  A <tt>null</tt>
     *                   argument index indicates that any type is allowed (equivalent
     *                   to specifying <tt>Object.class</tt> in the argument).  If
     *                   the array is <tt>null</tt>, then the argument list will only
     *                   match the default (no-argument) constructor (which is the
     *                   same as setting <tt>arguments</tt> to <tt>new Class[0]</tt>).
     * @param protection a bitwise ORed value containing one or more of the
     *                   constants <tt>PUBLIC</tt>, <tt>PROTECTED</tt>, <tt>PACKAGE</tt>,
     *                   or <tt>PRIVATE</tt>.
     * @see #assertHasSameConstructor(Class, Class[])
     * @see #assertHasSameConstructor(Object, Class[], int)
     * @see #assertHasConstructor(Class, Class[], int)
     */
    public static void assertHasSameConstructor(
            Class c, Class[] arguments, int protection) {
        Assert.assertNotNull("Null class argument.", c);

        Assert.assertNotNull(getSameConstructor(c, arguments, protection));
    }


    /**
     * Ensures that there exists a public constructor in class <tt>c</tt> that
     * accepts the same number of arguments given in <tt>arguments</tt>,
     * and that the constructor has the same class or an instance of the
     * class in each position of the argument list.
     *
     * @param arguments list of classes which the constructor must have
     *                  as parameters, or subclasses of the arguments.  A <tt>null</tt>
     *                  argument index indicates that any type is allowed (equivalent
     *                  to specifying <tt>Object.class</tt> in the argument).  If
     *                  the array is <tt>null</tt>, then the argument list will only
     *                  match the default (no-argument) constructor (which is the
     *                  same as setting <tt>arguments</tt> to <tt>new Class[0]</tt>).
     * @see #assertHasSameConstructor(String, Class, Class[], int)
     * @see #assertHasSameConstructor(String, Object, Class[])
     * @see #assertHasConstructor(String, Class, Class[])
     */
    public static void assertHasSameConstructor(String message,
                                                Class c, Class[] arguments) {
        assertHasSameConstructor(message, c, arguments, PUBLIC);
    }


    /**
     * Ensures that there exists a public constructor in class <tt>c</tt> that
     * accepts the same number of arguments given in <tt>arguments</tt>,
     * and that the constructor has the same class or an instance of the
     * class in each position of the argument list.
     *
     * @param arguments list of classes which the constructor must have
     *                  as parameters, or subclasses of the arguments.  A <tt>null</tt>
     *                  argument index indicates that any type is allowed (equivalent
     *                  to specifying <tt>Object.class</tt> in the argument).  If
     *                  the array is <tt>null</tt>, then the argument list will only
     *                  match the default (no-argument) constructor (which is the
     *                  same as setting <tt>arguments</tt> to <tt>new Class[0]</tt>).
     * @see #assertHasSameConstructor(Class, Class[], int)
     * @see #assertHasSameConstructor(Object, Class[])
     * @see #assertHasConstructor(Class, Class[])
     */
    public static void assertHasSameConstructor(
            Class c, Class[] arguments) {
        assertHasSameConstructor(c, arguments, PUBLIC);
    }


    /**
     * Ensures that there exists a constructor in class <tt>c</tt> that
     * accepts the same number of arguments given in <tt>arguments</tt>,
     * and that the constructor has the same class or an instance of the
     * class in each position of the argument list.
     *
     * @param arguments  list of classes which the constructor must have
     *                   as parameters, or subclasses of the arguments.  A <tt>null</tt>
     *                   argument index indicates that any type is allowed (equivalent
     *                   to specifying <tt>Object.class</tt> in the argument).  If
     *                   the array is <tt>null</tt>, then the argument list will only
     *                   match the default (no-argument) constructor (which is the
     *                   same as setting <tt>arguments</tt> to <tt>new Class[0]</tt>).
     * @param protection a bitwise ORed value containing one or more of the
     *                   constants <tt>PUBLIC</tt>, <tt>PROTECTED</tt>, <tt>PACKAGE</tt>,
     *                   or <tt>PRIVATE</tt>.
     * @see #assertHasSameConstructor(String, Object, Class[])
     * @see #assertHasSameConstructor(String, Class, Class[], int)
     * @see #assertHasConstructor(String, Object, Class[], int)
     */
    public static void assertHasSameConstructor(String message,
                                                Object o, Class[] arguments, int protection) {
        Assert.assertNotNull("Null object arguments.", o);
        assertHasSameConstructor(message, o.getClass(), arguments,
                protection);
    }


    /**
     * Ensures that there exists a constructor in class <tt>c</tt> that
     * accepts the same number of arguments given in <tt>arguments</tt>,
     * and that the constructor has the same class or an instance of the
     * class in each position of the argument list.
     *
     * @param arguments  list of classes which the constructor must have
     *                   as parameters, or subclasses of the arguments.  A <tt>null</tt>
     *                   argument index indicates that any type is allowed (equivalent
     *                   to specifying <tt>Object.class</tt> in the argument).  If
     *                   the array is <tt>null</tt>, then the argument list will only
     *                   match the default (no-argument) constructor (which is the
     *                   same as setting <tt>arguments</tt> to <tt>new Class[0]</tt>).
     * @param protection a bitwise ORed value containing one or more of the
     *                   constants <tt>PUBLIC</tt>, <tt>PROTECTED</tt>, <tt>PACKAGE</tt>,
     *                   or <tt>PRIVATE</tt>.
     * @see #assertHasSameConstructor(Object, Class[])
     * @see #assertHasSameConstructor(Class, Class[], int)
     * @see #assertHasConstructor(Object, Class[], int)
     */
    public static void assertHasSameConstructor(
            Object o, Class[] arguments, int protection) {
        Assert.assertNotNull("Null object arguments.", o);
        assertHasSameConstructor(o.getClass(), arguments, protection);
    }


    /**
     * Ensures that there exists a public constructor in class <tt>c</tt> that
     * accepts the same number of arguments given in <tt>arguments</tt>,
     * and that the constructor has the same class or an instance of the
     * class in each position of the argument list.
     *
     * @param arguments list of classes which the constructor must have
     *                  as parameters, or subclasses of the arguments.  A <tt>null</tt>
     *                  argument index indicates that any type is allowed (equivalent
     *                  to specifying <tt>Object.class</tt> in the argument).  If
     *                  the array is <tt>null</tt>, then the argument list will only
     *                  match the default (no-argument) constructor (which is the
     *                  same as setting <tt>arguments</tt> to <tt>new Class[0]</tt>).
     * @see #assertHasSameConstructor(String, Object, Class[], int)
     * @see #assertHasSameConstructor(String, Class, Class[])
     * @see #assertHasConstructor(String, Object, Class[])
     */
    public static void assertHasSameConstructor(String message,
                                                Object o, Class[] arguments) {
        Assert.assertNotNull("Null object arguments.", o);
        assertHasSameConstructor(message, o.getClass(), arguments);
    }


    /**
     * Ensures that there exists a public constructor in class <tt>c</tt> that
     * accepts the same number of arguments given in <tt>arguments</tt>,
     * and that the constructor has the same class or an instance of the
     * class in each position of the argument list.
     *
     * @param arguments list of classes which the constructor must have
     *                  as parameters, or subclasses of the arguments.  A <tt>null</tt>
     *                  argument index indicates that any type is allowed (equivalent
     *                  to specifying <tt>Object.class</tt> in the argument).  If
     *                  the array is <tt>null</tt>, then the argument list will only
     *                  match the default (no-argument) constructor (which is the
     *                  same as setting <tt>arguments</tt> to <tt>new Class[0]</tt>).
     * @see #assertHasSameConstructor(Object, Class[], int)
     * @see #assertHasSameConstructor(Class, Class[])
     * @see #assertHasConstructor(Object, Class[])
     */
    public static void assertHasSameConstructor(
            Object o, Class[] arguments) {
        Assert.assertNotNull("Null object arguments.", o);
        assertHasSameConstructor(o.getClass(), arguments);
    }


    /**
     * Retrieves the first constructor in class <tt>c</tt> that
     * accepts the same number of arguments given in <tt>arguments</tt>,
     * and that the constructor has the same class or an instance of the
     * class in each position of the argument list, or <tt>null</tt> if
     * there is no such constructor.
     *
     * @param arguments  list of classes which the constructor must have
     *                   as parameters, or subclasses of the arguments.  A <tt>null</tt>
     *                   argument index indicates that any type is allowed (equivalent
     *                   to specifying <tt>Object.class</tt> in the argument).  If
     *                   the array is <tt>null</tt>, then the argument list will only
     *                   match the default (no-argument) constructor (which is the
     *                   same as setting <tt>arguments</tt> to <tt>new Class[0]</tt>).
     * @param protection a bitwise ORed value containing one or more of the
     *                   constants <tt>PUBLIC</tt>, <tt>PROTECTED</tt>, <tt>PACKAGE</tt>,
     *                   or <tt>PRIVATE</tt>.
     * @see #getSameConstructor(Class, Class[], int)
     */
    public static Constructor getConstructor(Class c, Class[] arguments,
                                             int protection) {
        Assert.assertNotNull("Null class argument.", c);
        Constructor[] cntrs = c.getConstructors();

        for (int i = 0; i < cntrs.length; ++i) {
            if (hasCorrectProtection(cntrs[i], protection)
                    && isInheritedParameters(cntrs[i], arguments)) {
                return cntrs[i];
            }
        }

        return null;
    }


    /**
     * Retrieves the constructor in class <tt>c</tt> that
     * accepts the exact argument list given in <tt>arguments</tt>, or
     * <tt>null</tt> if there is no such constructor.
     *
     * @param protection a bitwise ORed value containing one or more of the
     *                   constants <tt>PUBLIC</tt>, <tt>PROTECTED</tt>, <tt>PACKAGE</tt>,
     *                   or <tt>PRIVATE</tt>.
     * @see Class#getConstructor(Class[])
     */
    public static Constructor getSameConstructor(Class c, Class[] arguments,
                                                 int protection) {
        Assert.assertNotNull("Null class argument.", c);
        try {
            Constructor cntr = c.getConstructor(arguments);
            if (cntr != null && hasCorrectProtection(cntr, protection)) {
                return cntr;
            }
        } catch (NoSuchMethodException nsme) {
            // ignore
        }
        return null;
    }


    protected static boolean isInheritedParameters(Constructor cntr,
                                                   Class[] arguments) {
        Class[] params = cntr.getParameterTypes();
        if (arguments == null) {
            // Avoid NPEs later by returning immediately
            return (params.length == 0);
        }
        if (params.length != arguments.length) {
            return false;
        }

        for (int i = 0; i < params.length; ++i) {
            // null argument == Object.class == anything
            if (arguments[i] != null) {
                if (!arguments[i].isAssignableFrom(params[i])) {
                    return false;
                }
            }
        }

        // every argument passed.
        return true;
    }


    protected static boolean hasCorrectProtection(Constructor cntr,
                                                  int protection) {
        int modifiers = cntr.getModifiers();
        boolean isPublic = Modifier.isPublic(modifiers);
        if ((protection & PUBLIC) != 0 && isPublic) {
            return true;
        }

        boolean isProtected = Modifier.isProtected(modifiers);
        if ((protection & PROTECTED) != 0 && isProtected) {
            return true;
        }

        boolean isPrivate = Modifier.isPrivate(modifiers);
        if ((protection & PRIVATE) != 0 && isPrivate) {
            return true;
        }

        if ((protection & PACKAGE) != 0
                && !isPublic
                && !isProtected
                && !isPrivate) {
            return true;
        }

        return false;
    }
}

