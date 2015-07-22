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

import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.DetailAST;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.ConversionException;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Simple regex check for content of any javadoc.
 * @author atta_troll
 */
public class JavadocMultilineRegexCheck extends Check
{

    /** Message if regex found. */
    public static final String MSG_KEY_JAVADOC_REGEX_FOUND = "javadoc.regex.found";

    /** Pattern for finding new line positions. */
    private static final Pattern NEWLINE_PATTERN = Pattern.compile("\\r?\\n");

    /** Set of mutiline regular expressions. */
    private String format = "";

    /** The message string. */
    private String message = MSG_KEY_JAVADOC_REGEX_FOUND;

    /** The parsed check regexp, expanded for the text of this tag. */
    private Pattern regex = Pattern.compile("");

    /**
     * Setter for regex.
     * @param input
     *        , contains patterns for non-suppressed checks.
     */
    public void setFormat(String input)
    {
        format = input;
    }

    /**
     * Setter for message string.
     * @param input
     *        , message string.
     */
    public void setMessage(String input)
    {
        message = input;
    }

    @Override
    public boolean isCommentNodesRequired()
    {
        return true;
    }

    /**
     * {@inheritDoc} Creates checkRegexp from checkFormat.
     */
    @Override
    protected void finishLocalSetup()
            throws CheckstyleException
    {
        
        try {
            if (format == "") {
                throw new CheckstyleException("JavadocMultilineRegexCheck - "
                        + "format property should have non-empty value.");
            }
            regex = Pattern.compile(format, Pattern.MULTILINE);
        }
        catch (final PatternSyntaxException e) {
            throw new ConversionException("unable to parse regular expression " + format, e);
        }
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] {
                TokenTypes.BLOCK_COMMENT_BEGIN,
        };
    }

    @Override
    public void visitToken(DetailAST ast)
    {
        final String rawText = ast.getFirstChild().getText();
        if (rawText.charAt(0) == '*') {
            final int startLine = ast.getLineNo();
            final String comment = rawText.replaceAll("(?m)^\\s*\\*[ \\t\\x0B\\f]*", "");
            final Matcher matcher = regex.matcher(comment);
            final List<Integer> lineIndexes = getLineIndexes(comment);
            while (matcher.find()) {
                final int index = matcher.start();
                int offset = getOffset(lineIndexes, index);
                log(startLine + offset, message, format);
            }
        }
    }

    /**
     * Generates a list of newline indexes for calculating an offset of the found matchings.
     * @param comment
     *        , subject comment.
     * @return list of newline indexes.
     */
    private List<Integer> getLineIndexes(String comment)
    {
        final Matcher newline = NEWLINE_PATTERN.matcher(comment);
        final List<Integer> lineIndexes = new ArrayList<>();
        while (newline.find()) {
            lineIndexes.add(newline.start());
        }
        return lineIndexes;
    }

    /**
     * Calculates offset of the found match.
     * @param lineIndexes
     *        , list of line breaks in comment.
     * @param index
     *        , of the found match.
     * @return offset of current check.
     */
    private int getOffset(List<Integer> lineIndexes, int index)
    {
        int offset = 0;
        for (Integer i : lineIndexes) {
            if (index < i) {
                break;
            }
            offset++;
        }
        return offset;
    }
}
