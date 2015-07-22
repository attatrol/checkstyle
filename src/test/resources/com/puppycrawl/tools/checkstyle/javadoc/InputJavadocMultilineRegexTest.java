////////////////////////////////////////////////////////////////////////////////
// Test case file for checkstyle.
// Created: 2015
////////////////////////////////////////////////////////////////////////////////

package com.puppycrawl.tools.checkstyle.javadoc;

/**
 * test
 */
@interface TestAnnotation{
  
}

/*
 * not a javadoc. test. 
 */
public class InputJavadocMultilineRegexTest {
    //not a javadoc Test
    public int a;
    
    /**
     *  javadoc without pattern.
     */
    public int b;
    
    /**
     * Test.
     * Test.
     * @param test.
     */
    public void foo(int test) {
        /**
         * 
         * 
         * wrongplaced javadoc. Test.
         */
    }
    
    /** single line javadoc. test.*/
    public int c;
    
    /**
     * This paragraph has no spacing before paragraph.
     * 45 is its index.
     * <p>Test</p>
     */
}
