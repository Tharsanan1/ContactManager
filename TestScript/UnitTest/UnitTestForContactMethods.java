package com.ktharsanan.contactmangerversion1;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by kthar on 13/05/2018.
 */

public class UnitTestForContactMethods {
    @Test
    public void checkReturnContactIsCorrect() throws Exception {
        Contact contact = new Contact();
        contact.setName("Tharsanan");
        contact.setMail("tharsanan.15@cse.mrt.ac.lk");
        contact.setAddressFinal("62/15,Kandy Road, Trincomalee");
        contact.setCompanyName("UOM");
        contact.setOccupation("Student");
        contact.addNumber("0773139405", "mobile");
        contact.addNumber("0773134405", "home");
        contact.addOthers("other data");
        contact.addOthers("other data");
        contact.addOthers("other data");
        String contactString = contact.toString();
        Contact contactReturned = Contact.createContactFromString(contactString);
        assertEquals("Tharsanan", contactReturned.getName());
        assertEquals("tharsanan.15@cse.mrt.ac.lk", contactReturned.getMail());
        assertEquals("62/15,Kandy Road, Trincomalee", contactReturned.getAddressFinal());
        assertEquals("Student", contactReturned.getOccupation());
        assertEquals("UOM", contactReturned.getCompanyName());
        for(int i = 0; i<contact.getNumbers().size(); i++){
            assertEquals(contactReturned.getNumbers().get(i), contact.getNumbers().get(i));
        }
        for(int i = 0; i<contact.getOthers().size(); i++){
            assertEquals(contactReturned.getOthers().get(i), contact.getOthers().get(i));
        }

    }
}
