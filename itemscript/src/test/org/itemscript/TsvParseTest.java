
package test.org.itemscript;

import org.itemscript.core.util.TsvParser;
import org.itemscript.core.values.JsonArray;

public class TsvParseTest extends ItemscriptTestBase {
    public void testTsv() {
        JsonArray array = TsvParser.parse(system(), system().getString("classpath:test/org/itemscript/test.tsv"));
        assertEquals("X", array.getArray(0)
                .getString(0));
        assertEquals("a", array.getArray(1)
                .getString(0));
        assertEquals("1", array.getArray(1)
                .getString(1));
        assertEquals("$1.00", array.getArray(1)
                .getString(2));
        assertEquals("", array.getArray(4)
                .getString(0));
        assertEquals("4", array.getArray(4)
                .getString(1));
        assertEquals("$4.00", array.getArray(4)
                .getString(2));
        assertEquals("", array.getArray(5)
                .getString(1));
        assertEquals("$5.00", array.getArray(5)
                .getString(2));
        assertEquals("", array.getArray(6)
                .getString(2));
    }

    public void testTsvWithoutFinalLineEnding() {
        JsonArray array = TsvParser.parse(system(), "foo\tbar\nx\ty");
        assertEquals("foo", array.getArray(0)
                .getString(0));
        assertEquals("y", array.getArray(1)
                .getString(1));
    }

    public void testTsvWithDosLineEndings() {
        JsonArray array = TsvParser.parse(system(), "foo\tbar\r\nx\ty\r\n");
        assertEquals("foo", array.getArray(0)
                .getString(0));
        assertEquals("y", array.getArray(1)
                .getString(1));
    }

    public void testTsvWithHeaders() {
        JsonArray array =
                TsvParser.parseWithHeaderLine(system(), system().getString(
                        "classpath:test/org/itemscript/test.tsv"));
        assertEquals("a", array.getObject(0)
                .getString("X"));
        assertEquals("1", array.getObject(0)
                .getString("Y"));
        assertEquals("$1.00", array.getObject(0)
                .getString("Z"));
        assertEquals("", array.getObject(3)
                .getString("X"));
        assertEquals("4", array.getObject(3)
                .getString("Y"));
        assertEquals("$4.00", array.getObject(3)
                .getString("Z"));
        assertEquals("", array.getObject(4)
                .getString("Y"));
        assertEquals("$5.00", array.getObject(4)
                .getString("Z"));
        assertEquals("", array.getObject(5)
                .getString("Z"));
    }
}