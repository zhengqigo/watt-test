package org.fuelteam.watt.test.tuple;

import static org.testng.AssertJUnit.assertEquals;

import org.fuelteam.watt.lucky.print.Vardump;
import org.fuelteam.watt.lucky.tuple.TupleN;
import org.junit.Test;

public class DefaultTupleTest {

    public static TupleN tripletTupleType = new TupleN(Number.class, String.class, Character.class);

    public static TupleN quadruple = new TupleN(4);

    public static TupleN emptyTupleType = new TupleN();

    public static TupleN xuple = new TupleN(10);
    
    public static TupleN default1 = new TupleN(3);
    
    @Test
    public void de1() {
        final TupleN t1 = default1.of(1, "one", 'a');
        System.out.println("x1==" + (Integer)t1.getNthValue(0));
        Vardump.print("t11 = " + t1);
    }

    @Test
    public void one() {
        final TupleN t1 = tripletTupleType.of(1, "one", 'a');
        System.out.println("x==" + (Integer)t1.getNthValue(0));
        Vardump.print("t1 = " + t1);
    }

    @Test
    public void x() {
        final TupleN t1 = xuple.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "0");
        Vardump.print("t1 = " + t1);
    }

    @Test
    public void two() {
        final TupleN t2 = tripletTupleType.of(2l, "two", 'b');
        Vardump.print("t2 = " + t2);
        System.out.println(t2.toString());
    }

    @Test
    public void three() {
        final TupleN t3 = tripletTupleType.of(3f, "three", 'c');
        Vardump.print("t3 = " + t3);
    }

    @Test
    public void four() {
        final TupleN t4 = quadruple.of(3f, "three", 'c', 1d);
        Vardump.print("t4 = " + t4);
    }

    @Test
    public void nullTuple() {
        final TupleN tnull = tripletTupleType.of(null, "(null)", null);
        Vardump.print("tnull = " + tnull);
    }

    @Test
    public void empty() {
        final TupleN tempty = emptyTupleType.of();
        Vardump.print("tempty = " + tempty);
    }

    @Test(expected = IllegalArgumentException.class)
    public void wrongTypes() {
        tripletTupleType.of(1, 2, 3);

    }

    @Test(expected = IllegalArgumentException.class)
    public void wrongNumberOfArguments() {
        emptyTupleType.of(1);
    }

    @Test
    public void valueType() {
        final TupleN t9 = tripletTupleType.of(9, "nine", 'i');
        assertEquals(Integer.class, t9.getNthValue(0).getClass());
    }
}