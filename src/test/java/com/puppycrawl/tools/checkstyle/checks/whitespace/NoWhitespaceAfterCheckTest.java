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

package com.puppycrawl.tools.checkstyle.checks.whitespace;

import static com.puppycrawl.tools.checkstyle.checks.whitespace.NoWhitespaceAfterCheck.MSG_KEY;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import antlr.CommonHiddenStreamToken;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class NoWhitespaceAfterCheckTest
    extends BaseCheckTestSupport {
    private DefaultConfiguration checkConfig;

    @Before
    public void setUp() {
        checkConfig = createCheckConfig(NoWhitespaceAfterCheck.class);
    }

    @Test
    public void testDefault() throws Exception {
        checkConfig.addAttribute("allowLineBreaks", "false");
        final String[] expected = {
            "5:14: " + getCheckMessage(MSG_KEY, "."),
            "6:12: " + getCheckMessage(MSG_KEY, "."),
            "29:14: " + getCheckMessage(MSG_KEY, "-"),
            "29:21: " + getCheckMessage(MSG_KEY, "+"),
            "31:15: " + getCheckMessage(MSG_KEY, "++"),
            "31:22: " + getCheckMessage(MSG_KEY, "--"),
            "111:22: " + getCheckMessage(MSG_KEY, "!"),
            "112:23: " + getCheckMessage(MSG_KEY, "~"),
            "129:24: " + getCheckMessage(MSG_KEY, "."),
            "132:11: " + getCheckMessage(MSG_KEY, "."),
            "136:12: " + getCheckMessage(MSG_KEY, "."),
            "264:2: " + getCheckMessage(MSG_KEY, "."),
        };
        verify(checkConfig, getPath("InputWhitespace.java"), expected);
    }

    @Test
    public void testDotAllowLineBreaks() throws Exception {
        checkConfig.addAttribute("tokens", "DOT");
        final String[] expected = {
            "5:14: " + getCheckMessage(MSG_KEY, "."),
            "129:24: " + getCheckMessage(MSG_KEY, "."),
            "136:12: " + getCheckMessage(MSG_KEY, "."),
        };
        verify(checkConfig, getPath("InputWhitespace.java"), expected);
    }

    @Test
    public void testTypecast() throws Exception {
        checkConfig.addAttribute("tokens", "TYPECAST");
        final String[] expected = {
            "87:28: " + getCheckMessage(MSG_KEY, ")"),
            "89:23: " + getCheckMessage(MSG_KEY, ")"),
            "241:22: " + getCheckMessage(MSG_KEY, ")"),
        };
        verify(checkConfig, getPath("InputWhitespace.java"), expected);
    }

    @Test
    public void testArrayDeclarations() throws Exception {
        checkConfig.addAttribute("tokens", "ARRAY_DECLARATOR");
        checkConfig.addAttribute("tokens", "INDEX_OP");
        final String[] expected = {
            "6:11: " + getCheckMessage(MSG_KEY, "Object"),
            "8:22: " + getCheckMessage(MSG_KEY, "someStuff3"),
            "9:8: " + getCheckMessage(MSG_KEY, "int"),
            "10:13: " + getCheckMessage(MSG_KEY, "s"),
            "11:13: " + getCheckMessage(MSG_KEY, "d"),
            "16:14: " + getCheckMessage(MSG_KEY, "get"),
            "18:8: " + getCheckMessage(MSG_KEY, "int"),
            "19:34: " + getCheckMessage(MSG_KEY, "get1"),
            "28:8: " + getCheckMessage(MSG_KEY, "int"),
            "29:12: " + getCheckMessage(MSG_KEY, "cba"),
            "31:26: " + getCheckMessage(MSG_KEY, "String"),
            "32:27: " + getCheckMessage(MSG_KEY, "String"),
            "39:11: " + getCheckMessage(MSG_KEY, "ar"),
            "39:24: " + getCheckMessage(MSG_KEY, "int"),
            "40:16: " + getCheckMessage(MSG_KEY, "int"),
            "43:63: " + getCheckMessage(MSG_KEY, "getLongMultArray"),
        };
        verify(checkConfig, getPath("whitespace/InputNoWhitespaceAfterArrayDeclarations.java"), expected);
    }

    @Test
    public void testArrayDeclarations2() throws Exception {
        checkConfig.addAttribute("tokens", "ARRAY_DECLARATOR");
        checkConfig.addAttribute("tokens", "INDEX_OP");
        final String[] expected = {
            "12:30: " + getCheckMessage(MSG_KEY, "]"),
            "17:40: " + getCheckMessage(MSG_KEY, "create"),
            "18:27: " + getCheckMessage(MSG_KEY, "int"),
            "29:23: " + getCheckMessage(MSG_KEY, "]"),
            "30:27: " + getCheckMessage(MSG_KEY, "int"),
            "30:38: " + getCheckMessage(MSG_KEY, "]"),
            "30:51: " + getCheckMessage(MSG_KEY, "]"),
            "35:44: " + getCheckMessage(MSG_KEY, "int"),
            "35:56: " + getCheckMessage(MSG_KEY, "]"),
            "36:18: " + getCheckMessage(MSG_KEY, "e"),
            "36:23: " + getCheckMessage(MSG_KEY, "]"),
            "36:43: " + getCheckMessage(MSG_KEY, "]"),
            "37:14: " + getCheckMessage(MSG_KEY, "e"),
            "37:18: " + getCheckMessage(MSG_KEY, "]"),
            "42:30: " + getCheckMessage(MSG_KEY, "Integer"),
            "43:20: " + getCheckMessage(MSG_KEY, "]"),
            "48:28: " + getCheckMessage(MSG_KEY, ">"),
            "48:31: " + getCheckMessage(MSG_KEY, "]"),
            "48:34: " + getCheckMessage(MSG_KEY, "]"),
            "52:34: " + getCheckMessage(MSG_KEY, "int"),
            "54:14: " + getCheckMessage(MSG_KEY, "g"),
            "55:17: " + getCheckMessage(MSG_KEY, "]"),
            "56:14: " + getCheckMessage(MSG_KEY, "g"),
            "56:18: " + getCheckMessage(MSG_KEY, "]"),
            "56:22: " + getCheckMessage(MSG_KEY, "]"),
            "62:50: " + getCheckMessage(MSG_KEY, "create"),
            "62:57: " + getCheckMessage(MSG_KEY, "]"),
            "67:32: " + getCheckMessage(MSG_KEY, "boolean"),
            "69:46: " + getCheckMessage(MSG_KEY, "String"),
            "69:50: " + getCheckMessage(MSG_KEY, "]"),
            "70:36: " + getCheckMessage(MSG_KEY, "String"),
            "81:40: " + getCheckMessage(MSG_KEY, "Integer"),
            "85:14: " + getCheckMessage(MSG_KEY, "char"),
            "86:52: " + getCheckMessage(MSG_KEY, "A"),
            "87:86: " + getCheckMessage(MSG_KEY, "Object"),
            "90:41: " + getCheckMessage(MSG_KEY, ")"),
            "90:49: " + getCheckMessage(MSG_KEY, "]"),
            "92:35: " + getCheckMessage(MSG_KEY, "Object"),
        };
        verify(checkConfig, getPath("whitespace/InputNoWhitespaceAfterArrayDeclarations2.java"), expected);
    }

    @Test
    public void testNpe() throws Exception {
        final String[] expected = {

        };
        verify(checkConfig, getPath("whitespace/InputNoWhiteSpaceAfterCheckFormerNpe.java"),
                 expected);
    }

    @Test
    public void testMethodReference() throws Exception {
        final String[] expected = {

        };
        verify(checkConfig, new File("src/test/resources-noncompilable/com/puppycrawl/tools/"
            + "checkstyle/grammars/java8/InputMethodReferencesTest2.java").getCanonicalPath(),
                 expected);
    }

    @Test(expected = IllegalStateException.class)
    public void testVisitTokenSwitchReflection() throws Exception {

        //unexpected parent for ARRAY_DECLARALOR token
        final DetailAST astImport = mockAST(TokenTypes.IMPORT, "import", "mockfile", 0, 0);
        final DetailAST astArrayDeclarator = mockAST(TokenTypes.ARRAY_DECLARATOR, "[", "mockfile", 0, 10);
        final DetailAST astRBrake = mockAST(TokenTypes.RBRACK, "[", "mockfile", 0, 11);
        astImport.addChild(astArrayDeclarator);
        astArrayDeclarator.addChild(astRBrake);

        NoWhitespaceAfterCheck check = new NoWhitespaceAfterCheck();
        // expecting IllegalStateException
        check.visitToken(astArrayDeclarator);
    }

    /**
     * Creates MOCK lexical token and returns AST node for this token
     * @param tokenType type of token
     * @param tokenText text of token
     * @param tokenFileName file name of token
     * @param tokenRow token position in a file (row)
     * @param tokenColumn token position in a file (column)
     * @return AST node for the token
     */
    private static DetailAST mockAST(final int tokenType, final String tokenText,
            final String tokenFileName, final int tokenRow, final int tokenColumn) {
        CommonHiddenStreamToken tokenImportSemi = new CommonHiddenStreamToken();
        tokenImportSemi.setType(tokenType);
        tokenImportSemi.setText(tokenText);
        tokenImportSemi.setLine(tokenRow);
        tokenImportSemi.setColumn(tokenColumn);
        tokenImportSemi.setFilename(tokenFileName);
        DetailAST astSemi = new DetailAST();
        astSemi.initialize(tokenImportSemi);
        return astSemi;
    }
}
