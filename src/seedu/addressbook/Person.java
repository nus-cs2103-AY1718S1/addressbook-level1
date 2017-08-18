package seedu.addressbook;

import java.util.HashMap;

/**
 * This class is meant to simplify demonstration for LO-Collections and LO-enum
 */
public class Person {
    /**
     * Key values for the Person property storage. The enum is public for use from other classes.
     */
    public enum PersonProperty {NAME, PHONE, EMAIL};

    /**
     * The HashMap storage system for the properties. If
     */
    private HashMap<PersonProperty, String> fields;

    /**
     * Constructor for Person class.
     * @param name - String value of the person's name.
     * @param phone - String value of the person's phone number
     * @param email - String value of the person's
     */
    public Person(String name, String phone, String email) {
        fields = new HashMap<>(3, 1);
        fields.put(PersonProperty.NAME, name);
        fields.put(PersonProperty.PHONE, phone);
        fields.put(PersonProperty.EMAIL, email);
    }

    /**
     * Accessor method to get value for any enum PersonProperty key.
     * @param key - key of type PersonProperty
     * @return String value of the property or "N/A" if invalid key.
     */
    public String getProperty(PersonProperty key) {
        String tryProperty = fields.get(key);
        if (tryProperty != null) {
            return tryProperty;
        } else {
            return "N/A";
        }
    }

    /**
     * Accessor method to get name of the person.
     * @return String value of the name or "N/A" if invalid.
     */
    public String getName() {
        String tryName = fields.get(PersonProperty.NAME);
        if (tryName != null) {
            return tryName;
        } else {
            return "N/A";
        }
    }

    /**
     * Accessor method to get phone number of the person.
     * @return String value of the phone number or "N/A" if invalid.
     */
    public String getPhoneNumber() {
        String tryPhone = fields.get(PersonProperty.PHONE);
        if (tryPhone != null) {
            return tryPhone;
        } else {
            return "N/A";
        }
    }

    /**
     * Accessor method to get email of the person.
     * @return String value of the email or "N/A" if invalid.
     */
    public String getEmail() {
        String tryEmail = fields.get(PersonProperty.EMAIL);
        if (tryEmail != null) {
            return tryEmail;
        } else {
            return "N/A";
        }
    }
}
