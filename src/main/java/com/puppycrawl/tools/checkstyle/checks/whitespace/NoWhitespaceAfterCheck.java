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

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * Checks that there is no whitespace after a token.
 * More specifically, it checks that it is not followed by whitespace,
 * or (if linebreaks are allowed) all characters on the line after are
 * whitespace. To forbid linebreaks afer a token, set property
 * allowLineBreaks to false.
 * </p>
  * <p> By default the check will check the following operators:
 *  {@link TokenTypes#ARRAY_INIT ARRAY_INIT},
 *  {@link TokenTypes#BNOT BNOT},
 *  {@link TokenTypes#DEC DEC},
 *  {@link TokenTypes#DOT DOT},
 *  {@link TokenTypes#INC INC},
 *  {@link TokenTypes#LNOT LNOT},
 *  {@link TokenTypes#UNARY_MINUS UNARY_MINUS},
 *  {@link TokenTypes#ARRAY_DECLARATOR ARRAY_DECLARATOR},
 *  {@link TokenTypes#INDEX_OP INDEX_OP}
 *  {@link TokenTypes#UNARY_PLUS UNARY_PLUS}. It also supports the operator
 *  {@link TokenTypes#TYPECAST TYPECAST}.
 * </p>
 * <p>
 * An example of how to configure the check is:
 * </p>
 * <pre>
 * &lt;module name="NoWhitespaceAfter"/&gt;
 * </pre>
 * <p> An example of how to configure the check to forbid linebreaks after
 * a {@link TokenTypes#DOT DOT} token is:
 * </p>
 * <pre>
 * &lt;module name="NoWhitespaceAfter"&gt;
 *     &lt;property name="tokens" value="DOT"/&gt;
 *     &lt;property name="allowLineBreaks" value="false"/&gt;
 * &lt;/module&gt;
 * </pre>
 * @author Rick Giles
 * @author lkuehne
 * @author <a href="mailto:nesterenko-aleksey@list.ru">Aleksey Nesterenko</a>
 * @author attatrol
 */
public class NoWhitespaceAfterCheck extends Check {

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY = "ws.followed";

    /** Whether whitespace is allowed if the AST is at a linebreak */
    private boolean allowLineBreaks = true;

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.ARRAY_INIT,
            TokenTypes.INC,
            TokenTypes.DEC,
            TokenTypes.UNARY_MINUS,
            TokenTypes.UNARY_PLUS,
            TokenTypes.BNOT,
            TokenTypes.LNOT,
            TokenTypes.DOT,
            TokenTypes.ARRAY_DECLARATOR,
            TokenTypes.INDEX_OP,
        };
    }

    @Override
    public int[] getAcceptableTokens() {
        return new int[] {
            TokenTypes.ARRAY_INIT,
            TokenTypes.INC,
            TokenTypes.DEC,
            TokenTypes.UNARY_MINUS,
            TokenTypes.UNARY_PLUS,
            TokenTypes.BNOT,
            TokenTypes.LNOT,
            TokenTypes.DOT,
            TokenTypes.TYPECAST,
            TokenTypes.ARRAY_DECLARATOR,
            TokenTypes.INDEX_OP,
        };
    }

    @Override
    public void visitToken(DetailAST ast) {
        final DetailAST astNode;
        switch (ast.getType()) {
            case TokenTypes.TYPECAST:
                astNode = ast.findFirstToken(TokenTypes.RPAREN);
                break;
            case TokenTypes.ARRAY_DECLARATOR:
                astNode = getArrayDeclaratorPreviousElement(ast);
                break;
            case TokenTypes.INDEX_OP:
                astNode = getIndexOpPreviousElement(ast);
                break;
            default:
                astNode = ast;
        }

        final String line = getLine(ast.getLineNo() - 1);
        final int after = getPositionAfter(astNode);

        if ((after >= line.length() || Character.isWhitespace(line.charAt(after)))
                 && hasRedundantWhitespace(line, after)) {
            log(astNode.getLineNo(), after,
                MSG_KEY, astNode.getText());
        }
    }

    /**
     * Gets position after token (place of possible redundant whitespace).
     * @param ast Node representing token.
     * @return position after token.
     */
    private static int getPositionAfter(DetailAST ast) {
        int after;
        //If target of possible redundant whitespace is in method definition
        if (ast.getType() == TokenTypes.IDENT
                && ast.getNextSibling() != null
                && ast.getNextSibling().getType() == TokenTypes.LPAREN) {
            final DetailAST methodDef = ast.getParent();
            final DetailAST endOfParams = methodDef.findFirstToken(TokenTypes.RPAREN);
            after = endOfParams.getColumnNo() + 1;
        }
        else {
            after = ast.getColumnNo() + ast.getText().length();
        }
        return after;
    }

    /**
     * Control whether whitespace is flagged at linebreaks.
     * @param allowLineBreaks whether whitespace should be
     * flagged at linebreaks.
     */
    public void setAllowLineBreaks(boolean allowLineBreaks) {
        this.allowLineBreaks = allowLineBreaks;
    }

    /**
     * Checks if current line has redundant whitespace after specified index.
     * @param line line of java source.
     * @param after specified index.
     * @return true if line contains redundant whitespace.
     */
    private boolean hasRedundantWhitespace(String line, int after) {
        boolean result = !allowLineBreaks;
        for (int i = after + 1; !result && i < line.length(); i++) {
            if (!Character.isWhitespace(line.charAt(i))) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Gets previous node for {@link TokenTypes#INDEX_OP INDEX_OP} token
     * for usage in getPositionAfter method, it is a simplified copy of
     * getArrayDeclaratorPreviousElement method.
     * @param ast
     *        , {@link TokenTypes#INDEX_OP INDEX_OP} node.
     * @return previous node by text order.
     */
    private static DetailAST getIndexOpPreviousElement(DetailAST ast) {
        final DetailAST firstChild = ast.getFirstChild();
        if (firstChild.getType() == TokenTypes.INDEX_OP) {
            // second or higher array index
            return firstChild.findFirstToken(TokenTypes.RBRACK);
        }
        else {
            // always has an ident token
            return getIdentLastToken(ast);
        }
    }

    /**
     * Returns proper argument for getPositionAfter method, it is a token after
     * {@link TokenTypes#ARRAY_DECLARATOR ARRAY_DECLARATOR}, in can be {@link TokenTypes#RBRACK
     * RBRACK}, {@link TokenTypes#INDENT INDENT} or an array type definition (literal).
     * @param ast
     *        , {@link TokenTypes#ARRAY_DECLARATOR ARRAY_DECLARATOR} node.
     * @return previous node by text order.
     */
    private static DetailAST getArrayDeclaratorPreviousElement(DetailAST ast) {
        final DetailAST firstChild = ast.getFirstChild();
        if (firstChild.getType() == TokenTypes.ARRAY_DECLARATOR) {
            // second or higher array index
            return firstChild.findFirstToken(TokenTypes.RBRACK);
        }
        else {
            // first array index, is preceded with identifier or type
            final DetailAST parent = getArrayDeclaratorParent(ast);
            final int parentType = parent.getType();
            // generics
            if (parentType == TokenTypes.TYPE_ARGUMENT) {
                final DetailAST wildcard = parent.findFirstToken(TokenTypes.WILDCARD_TYPE);
                if (wildcard == null) {
                    // usual generic type argument like <char[]>
                    return getTypeLastNode(ast);
                }
                else {
                    // constructions with wildcard like <? extends String[]>
                    return getTypeLastNode(ast.getFirstChild());
                }
            }
            // 'new' is a special case with its own subtree structure
            else if (parentType == TokenTypes.LITERAL_NEW) {
                return getTypeLastNode(parent);
            }
            // mundane array declaration, can be either java style or C style
            else if (parentType == TokenTypes.TYPE) {
                return parentTypeTypeProcessing(ast, parent);
            }
            // i.e. boolean[].class
            else if (parentType == TokenTypes.DOT) {
                return getTypeLastNode(ast);
            }
            // java 8 method reference
            else if (parentType == TokenTypes.METHOD_REF) {
                return getIdentLastToken(ast);
            }
            else {
                throw new IllegalStateException("unexpected ast syntax" + parent.toString());
            }
        }
    }

    /**
     * Finds previous node by text order for an array declarator,
     * which parent type is {@link TokenTypes#TYPE TYPE}.
     * @param ast
     *        , array declarator node.
     * @param parent
     *        , its parent node.
     * @return previous node by text order.
     */
    private static DetailAST parentTypeTypeProcessing(DetailAST ast, DetailAST parent) {
        final DetailAST ident = getIdentLastToken(parent.getParent());
        final DetailAST lastTypeNode = getTypeLastNode(ast);
        if (ident == null) {
            // sometimes there are ident-less sentences
            // i.e. (Object[]) null
            return lastTypeNode;
        }
        // checks whether ident or lastTypeNode has preceding position
        // determining if it is java style or C style
        if (ident.getLineNo() != ast.getLineNo()
                ? ident.getLineNo() > ast.getLineNo()
                : ident.getColumnNo() > ast.getColumnNo()) {
            return lastTypeNode;
        }
        else {
            return ident;
        }
    }

    /**
     * Searches parameter node for a type node.
     * Returns it or its last node if it has an extended structure.
     * @param ast
     *        , subject node.
     * @return type node.
     */
    private static DetailAST getTypeLastNode(DetailAST ast) {
        DetailAST candidate = ast.findFirstToken(TokenTypes.TYPE_ARGUMENTS);
        if (candidate != null) {
            candidate = candidate.findFirstToken(TokenTypes.GENERIC_END);
        }
        else {
            candidate = getIdentLastToken(ast);
            if (candidate == null) {
                //primitive literal expected
                candidate = ast.getFirstChild();
            }
        }
        return candidate;
    }

    /**
     * Gets leftmost token of identifier.
     * @param ast
     *        , token possibly possessing an identifier.
     * @return leftmost token of identifier.
     */
    private static DetailAST getIdentLastToken(DetailAST ast) {
        // single identifier token as a name is the most common case
        DetailAST candidate = ast.findFirstToken(TokenTypes.IDENT);
        if (candidate == null) {
            final DetailAST dot = ast.findFirstToken(TokenTypes.DOT);
            // qualified name case
            if (dot != null) {
                if (dot.findFirstToken(TokenTypes.DOT) != null) {
                    candidate = dot.findFirstToken(TokenTypes.IDENT);
                }
                else {
                    candidate = dot.getFirstChild().getNextSibling();
                }
            }
            // method call case
            else {
                final DetailAST methodCall = ast.findFirstToken(TokenTypes.METHOD_CALL);
                if (methodCall != null) {
                    candidate = methodCall.findFirstToken(TokenTypes.RPAREN);
                }
            }
        }
        return candidate;
    }

    /**
     * Get node that owns {@link TokenTypes#ARRAY_DECLARATOR ARRAY_DECLARATOR} sequence.
     * @param ast
     *        , {@link TokenTypes#ARRAY_DECLARATOR ARRAY_DECLARATOR} node.
     * @return owner node.
     */
    private static DetailAST getArrayDeclaratorParent(DetailAST ast) {
        DetailAST parent = ast.getParent();
        while (parent.getType() == TokenTypes.ARRAY_DECLARATOR) {
            parent = parent.getParent();
        }
        return parent;
    }
}
