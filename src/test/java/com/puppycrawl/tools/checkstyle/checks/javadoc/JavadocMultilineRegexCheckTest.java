////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2015 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////

package com.puppycrawl.tools.checkstyle.checks.javadoc;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;

import static com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocMultilineRegexCheck.MSG_KEY_JAVADOC_REGEX_FOUND;

import java.io.File;

import org.junit.Test;

public class JavadocMultilineRegexCheckTest extends BaseCheckTestSupport {
    @Test
    public void testFingRegex()
            throws Exception {
        final DefaultConfiguration javadocRegexConfig = createCheckConfig(JavadocMultilineRegexCheck.class);
        final String regex = "(T|t)est";
        javadocRegexConfig.addAttribute("format", regex);
        final String[] expected = {
            "9: " + getCheckMessage(MSG_KEY_JAVADOC_REGEX_FOUND, regex),
            "28: " + getCheckMessage(MSG_KEY_JAVADOC_REGEX_FOUND, regex),
            "29: " + getCheckMessage(MSG_KEY_JAVADOC_REGEX_FOUND, regex),
            "30: " + getCheckMessage(MSG_KEY_JAVADOC_REGEX_FOUND, regex),
            "36: " + getCheckMessage(MSG_KEY_JAVADOC_REGEX_FOUND, regex),
            "40: " + getCheckMessage(MSG_KEY_JAVADOC_REGEX_FOUND, regex),
            "46: " + getCheckMessage(MSG_KEY_JAVADOC_REGEX_FOUND, regex),
        };
        verify(createChecker(javadocRegexConfig), getPath("javadoc" + File.separator
                + "InputJavadocMultilineRegexTest.java"), expected);
    }

    @Test
    public void testFingMultilineRegex()
            throws Exception {
        final DefaultConfiguration javadocRegexConfig = createCheckConfig(JavadocMultilineRegexCheck.class);
        final String regex = "([^\\s]+)\\s^(<\\w+>)";
        final String message = "Dead woodpecker";
        javadocRegexConfig.addAttribute("format", regex);
        javadocRegexConfig.addAttribute("message", message);
        final String[] expected = {
            "45: " + message,
        };
        verify(createChecker(javadocRegexConfig), getPath("javadoc" + File.separator
                + "InputJavadocMultilineRegexTest.java"), expected);
    }

    @Test(expected = CheckstyleException.class)
    public void testBadRegexException()
            throws Exception {
        final DefaultConfiguration javadocRegexConfig = createCheckConfig(JavadocMultilineRegexCheck.class);
        javadocRegexConfig.addAttribute("format", "*\\.*");
        final String[] expected = {};
        verify(createChecker(javadocRegexConfig), getPath("javadoc" + File.separator
                + "InputJavadocMultilineRegexTest.java"), expected);
    }

    @Test(expected = CheckstyleException.class)
    public void testDefault()
            throws Exception {
        final DefaultConfiguration javadocRegexConfig = createCheckConfig(JavadocMultilineRegexCheck.class);
        final String[] expected = {};
        verify(createChecker(javadocRegexConfig), getPath("javadoc" + File.separator
                + "InputJavadocMultilineRegexTest.java"), expected);
    }


}
