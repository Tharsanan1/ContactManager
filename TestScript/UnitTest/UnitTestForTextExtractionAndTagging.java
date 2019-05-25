package com.ktharsanan.contactmangerversion1;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UnitTestForTextExtractionAndTagging {
    @Test
    public void checkPhoneNumber() throws Exception {
        assertEquals(true, ResultProducer.isPhoneNumber("0777323423"));
        assertEquals(true, ResultProducer.isPhoneNumber("777323423"));
        assertEquals(true, ResultProducer.isPhoneNumber("+94777323423"));
        assertEquals(false, ResultProducer.isPhoneNumber("7323423"));


    }
    @Test
    public void checkName() throws Exception {
        assertEquals(true, ResultProducer.isName("Mr.tharsaan"));
    }
    @Test
    public void checkAddress() throws Exception {
        assertEquals(true, ResultProducer.isAddress("62/13, kandy road"));
    }
    @Test
    public void checkMail() throws Exception {
        assertEquals(true, ResultProducer.isEmail("ktharsanan@gmail.com"));
    }
    @Test
    public void checkWeb() throws Exception {
        assertEquals(true, ResultProducer.isWeb("google.lk"));
    }
    @Test
    public void checkNumberExtracted() throws Exception {
        ;
    }
}