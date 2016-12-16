package com.irunninglog.spring.math;

import com.irunninglog.Progress;
import com.irunninglog.Unit;
import com.irunninglog.spring.AbstractTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public class MathServiceTest extends AbstractTest {

    @Autowired
    private MathService mathService;

    @Test
    public void testFormatBigDecimal1() {
        BigDecimal bigDecimal = new BigDecimal(101.4999);
        assertEquals("101.5 mi", mathService.format(bigDecimal, Unit.English));
    }

    @Test
    public void testFormatBigDecimal2() {
        BigDecimal bigDecimal = new BigDecimal(1000);
        assertEquals("1,000 mi", mathService.format(bigDecimal, Unit.English));
    }

    @Test
    public void testFormatBigDecimal3() {
        BigDecimal bigDecimal = new BigDecimal(66.6666);
        assertEquals("66.67 mi", mathService.format(bigDecimal, Unit.English));
    }

    @Test
    public void testFormatDouble1() {
        assertEquals("101.5 mi", mathService.format(new BigDecimal(101.49999), Unit.English));
    }

    @Test
    public void testFormatDouble2() {
        assertEquals("1,000 mi", mathService.format(new BigDecimal(1000), Unit.English));
    }

    @Test
    public void testFormatDouble3() {
        assertEquals("66.67 mi", mathService.format(new BigDecimal(66.6667), Unit.English));
    }

    @Test
    public void testFormatAndConvert1() {
        assertEquals("1.61 km", mathService.format(new BigDecimal(1), Unit.Metric));
    }

    @Test
    public void testFormatAndConvert2() {
        assertEquals("16.09 km", mathService.format(new BigDecimal(10), Unit.Metric));
    }

    @Test
    public void testFormatAndConvert3() {
        assertEquals("160.93 km", mathService.format(new BigDecimal(100), Unit.Metric));
    }

    @Test
    public void testFormatAndConvert4() {
        assertEquals("10 km", mathService.format(new BigDecimal(6.213), Unit.Metric));
    }

    @Test
    public void testIntValue1() {
        assertEquals(101, mathService.intValue(new BigDecimal(101.4999)));
    }

    @Test
    public void testIntValue2() {
        assertEquals(1001, mathService.intValue(new BigDecimal(1000.99)));
    }

    @Test
    public void testIntValue3() {
        assertEquals(101, mathService.intValue(new BigDecimal(100.5)));
    }

    @Test
    public void testDivide1() {
        double result = mathService.divide(new BigDecimal(6236.232), new BigDecimal(5345.34344)).doubleValue();
        assertEquals(1.17, result, 1E-9);
    }

    @Test
    public void testDivide2() {
        double result = mathService.divide(new BigDecimal(1004.999999), new BigDecimal(10)).doubleValue();
        assertEquals(100.5, result, 1E-9);
    }

    @Test
    public void testDivideByZero() {
        try {
            mathService.divide(new BigDecimal(100), new BigDecimal(0));
            fail("Should not have been able to divide by zero");
        } catch (ArithmeticException ex) {
            assertTrue(true);
        }
    }

    @Test
    public void progress() {
        assertEquals(Progress.None, mathService.progress(new BigDecimal(0), new BigDecimal(0)));
        assertEquals(Progress.Bad, mathService.progress(new BigDecimal(10), new BigDecimal(100)));
        assertEquals(Progress.Ok, mathService.progress(new BigDecimal(30), new BigDecimal(100)));
        assertEquals(Progress.Good, mathService.progress(new BigDecimal(90), new BigDecimal(100)));
    }

    @Test
    public void formatProgressTest() {
        assertEquals("No progress to track", mathService.formatProgressText(new BigDecimal(0), new BigDecimal(0), Unit.English));
        assertEquals("50 mi of 100 mi (50%)", mathService.formatProgressText(new BigDecimal(50), new BigDecimal(100), Unit.English));
        assertEquals("101 mi of 100 mi (100%)", mathService.formatProgressText(new BigDecimal(101), new BigDecimal(100), Unit.English));
    }

    @Test
    public void getPercentage() {
        assertEquals(50, mathService.getPercentage(25, 50));
    }

}