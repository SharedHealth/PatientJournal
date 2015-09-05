package org.freeshr.journal.tags;

import org.hl7.fhir.instance.model.*;
import org.junit.Test;

import java.math.BigDecimal;

import static org.freeshr.journal.tags.TypeConverter.convertToText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TypeConverterTest {

    @Test
    public void shouldConvertANullTypeToEmptyString() throws Exception {
        assertTrue(convertToText(null).isEmpty());
    }

    @Test
    public void shouldConvertAQuantityType() throws Exception {
        Quantity quantity = new Quantity();

        Decimal value = new Decimal();
        value.setValue(new BigDecimal(12));
        String_ units = new String_();
        units.setValue("mg");

        quantity.setValue(value);
        quantity.setUnits(units);

//        assertEquals("12 mg", convertToText(quantity));
    }

    @Test
    public void shouldConvertACodeableConcept() throws Exception {
        CodeableConcept codeableConcept = new CodeableConcept();
//        assertTrue(convertToText(codeableConcept).isEmpty());

        Coding coding = codeableConcept.addCoding();
        String fever = "Fever";
        String_ display = new String_();
        display.setValue(fever);

        coding.setDisplay(display);

//        assertEquals(fever, convertToText(codeableConcept));
    }

    @Test
    public void shouldConvertADate() throws Exception {
        Date date = new Date();
        date.setValue(new DateAndTime("2015-06-17T00:00:00+05:30"));

        String expectedDate = "17 Jun 2015 00:00";
//        assertEquals(expectedDate, convertToText(date));
    }
    
    @Test
    public void shouldConvertADateTime() throws Exception {
        DateTime dateTime = new DateTime();
        dateTime.setValue(new DateAndTime("2015-06-17T00:00:00+05:30"));

        String expectedDate = "17 Jun 2015 00:00";
//        assertEquals(expectedDate, convertToText(dateTime));
    }
}