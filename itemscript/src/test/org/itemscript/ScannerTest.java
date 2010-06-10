
package test.org.itemscript;

import java.util.ArrayList;
import java.util.List;

import org.itemscript.template.scanner.CloseArgToken;
import org.itemscript.template.scanner.CloseTagToken;
import org.itemscript.template.scanner.CommaToken;
import org.itemscript.template.scanner.OpenArgToken;
import org.itemscript.template.scanner.OpenTagToken;
import org.itemscript.template.scanner.Scanner;
import org.itemscript.template.scanner.Token;
import org.junit.Test;

public class ScannerTest extends ItemscriptTestBase {
    private List<Token> tokens;

    private void printTokens() {
        for (int i = 0; i < tokens.size(); ++i) {
            System.err.println(i + " : " + tokens.get(i));
        }
    }

    private List<Token> scan(String text) {
        List<Token> tokens = new ArrayList<Token>();
        Scanner scanner = new Scanner(text);
        while (true) {
            Token next = scanner.next();
            if (next == null) {
                break;
            }
            tokens.add(next);
        }
        this.tokens = tokens;
        return tokens;
    }

    @Test
    public void testCommasAndStrings() {
        scan("{a('b','c')}");
        assertEquals(OpenTagToken.INSTANCE, tokens.get(0));
        assertEquals("a", tokens.get(1)
                .asExpressionToken()
                .content());
        assertEquals(OpenArgToken.INSTANCE, tokens.get(2));
        assertEquals("b", tokens.get(3)
                .asQuotedStringToken()
                .string());
        assertEquals(CommaToken.INSTANCE, tokens.get(4));
        assertEquals("c", tokens.get(5)
                .asQuotedStringToken()
                .string());
        assertEquals(CloseArgToken.INSTANCE, tokens.get(6));
        assertEquals(CloseTagToken.INSTANCE, tokens.get(7));
    }

    @Test
    public void testQuotedStrings() {
        scan("{'a' '''' '''b' 'c''' '''d''' ''''''}");
        assertEquals(OpenTagToken.INSTANCE, tokens.get(0));
        assertEquals("a", tokens.get(1)
                .asQuotedStringToken()
                .string());
        assertEquals("'", tokens.get(2)
                .asQuotedStringToken()
                .string());
        assertEquals("'b", tokens.get(3)
                .asQuotedStringToken()
                .string());
        assertEquals("c'", tokens.get(4)
                .asQuotedStringToken()
                .string());
        assertEquals("'d'", tokens.get(5)
                .asQuotedStringToken()
                .string());
        assertEquals("''", tokens.get(6)
                .asQuotedStringToken()
                .string());
        assertEquals(CloseTagToken.INSTANCE, tokens.get(7));
    }

    @Test
    public void testScanArgs() {
        scan("a {function(x y z, arg, arg, function(arg, arg))} c");
        assertEquals("function", tokens.get(2)
                .asExpressionToken()
                .content());
        assertEquals(OpenArgToken.INSTANCE, tokens.get(3));
        assertEquals("x", tokens.get(4)
                .asExpressionToken()
                .content());
        // 5 = y, 6 = z
        assertEquals(CommaToken.INSTANCE, tokens.get(7));
        assertEquals("arg", tokens.get(8)
                .asExpressionToken()
                .content());
        assertEquals(CommaToken.INSTANCE, tokens.get(9));
        // 10 = arg, 11 = comma, 12 = function
        assertEquals(OpenArgToken.INSTANCE, tokens.get(13));
        assertEquals(CloseArgToken.INSTANCE, tokens.get(17));
        assertEquals(CloseTagToken.INSTANCE, tokens.get(19));
    }

    @Test
    public void testScanTag() {
        scan("a {b} c");
        assertEquals(5, tokens.size());
        assertEquals("a ", tokens.get(0)
                .asTextToken()
                .text());
        assertEquals(OpenTagToken.INSTANCE, tokens.get(1));
        assertEquals("b", tokens.get(2)
                .asExpressionToken()
                .content());
        assertEquals(CloseTagToken.INSTANCE, tokens.get(3));
        assertEquals(" c", tokens.get(4)
                .asTextToken()
                .text());
    }

    @Test
    public void testScanTagWithMultipleExpressionTokens() {
        scan("a {.directive y z : :1 @url &literal} c");
        assertEquals("a ", tokens.get(0)
                .asTextToken()
                .text());
        assertEquals(OpenTagToken.INSTANCE, tokens.get(1));
        assertEquals(".directive", tokens.get(2)
                .asExpressionToken()
                .content());
        assertEquals("y", tokens.get(3)
                .asExpressionToken()
                .content());
        assertEquals("z", tokens.get(4)
                .asExpressionToken()
                .content());
        assertEquals(":", tokens.get(5)
                .asExpressionToken()
                .content());
        assertEquals(":1", tokens.get(6)
                .asExpressionToken()
                .content());
        assertEquals("@url", tokens.get(7)
                .asExpressionToken()
                .content());
        assertEquals("&literal", tokens.get(8)
                .asExpressionToken()
                .content());
        assertEquals(CloseTagToken.INSTANCE, tokens.get(9));
        assertEquals(" c", tokens.get(10)
                .asTextToken()
                .text());
    }

    @Test
    public void testScanTextOnly() {
        scan("a b c");
        assertEquals(1, tokens.size());
        assertEquals("a b c", tokens.get(0)
                .asTextToken()
                .text());
    }

    @Test
    public void testTagOnly() {
        scan("{a}");
        assertEquals(3, tokens.size());
        assertEquals(OpenTagToken.INSTANCE, tokens.get(0));
        assertEquals("a", tokens.get(1)
                .asExpressionToken()
                .content());
        assertEquals(CloseTagToken.INSTANCE, tokens.get(2));
    }
}