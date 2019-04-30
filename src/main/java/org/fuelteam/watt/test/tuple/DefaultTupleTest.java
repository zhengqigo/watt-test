package org.fuelteam.watt.test.tuple;

import static org.testng.AssertJUnit.assertEquals;

import org.fuelteam.watt.lucky.tuple.DefaultTupleType;
import org.fuelteam.watt.lucky.tuple.Tuple;
import org.fuelteam.watt.lucky.tuple.TupleType;
import org.fuelteam.watt.lucky.utils.Vardump;
import org.junit.Test;

public class DefaultTupleTest {

    public static TupleType tripletTupleType = TupleType.DefaultFactory.create(Number.class, String.class, Character.class);

    public static TupleType quadruple = TupleType.DefaultFactory.create(4);

    public static TupleType emptyTupleType = TupleType.DefaultFactory.create();

    public static TupleType xuple = TupleType.DefaultFactory.create(10);
    
    public static DefaultTupleType default1 = new DefaultTupleType(3);
    
    @Test
    public void de1() {
        final Tuple t1 = default1.of(1, "one", 'a');
        System.out.println("x1==" + (Integer)t1.getNthValue(0));
        Vardump.print("t11 = " + t1);
    }

    @Test
    public void one() {
        final Tuple t1 = tripletTupleType.of(1, "one", 'a');
        System.out.println("x==" + (Integer)t1.getNthValue(0));
        Vardump.print("t1 = " + t1);
    }

    @Test
    public void x() {
        final Tuple t1 = xuple.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "0");
        Vardump.print("t1 = " + t1);
    }

    @Test
    public void two() {
        final Tuple t2 = tripletTupleType.of(2l, "two", 'b');
        Vardump.print("t2 = " + t2);
        System.out.println(t2.toString());
    }

    @Test
    public void three() {
        final Tuple t3 = tripletTupleType.of(3f, "three", 'c');
        Vardump.print("t3 = " + t3);
    }

    @Test
    public void four() {
        final Tuple t4 = quadruple.of(3f, "three", 'c', 1d);
        Vardump.print("t4 = " + t4);
    }

    @Test
    public void nullTuple() {
        final Tuple tnull = tripletTupleType.of(null, "(null)", null);
        Vardump.print("tnull = " + tnull);
    }

    @Test
    public void empty() {
        final Tuple tempty = emptyTupleType.of();
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
        final Tuple t9 = tripletTupleType.of(9, "nine", 'i');
        assertEquals(Integer.class, t9.getNthValue(0).getClass());
    }
}