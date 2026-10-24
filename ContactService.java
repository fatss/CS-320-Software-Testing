package contactservice;

import java.util.HashMap;
import java.util.Map;

public class ContactService {
    private final Map<String, Contact> contacts = new HashMap<>();

    public void addContact(Contact contact) {
        if (contact == null) {
            throw new IllegalArgumentException("Contact must not be null.");
        }
        if (contacts.putIfAbsent(contact.getContactId(), contact) != null) {
            throw new IllegalArgumentException("Contact ID already exists.");
        }
    }

    public Contact getContact(String contactId) {
        Contact contact = contacts.get(contactId);
        if (contact == null) {
            throw new IllegalArgumentException("Contact ID does not exist.");
        }
        return contact;
    }

    public void deleteContact(String contactId) {
        if (contacts.remove(contactId) == null) {
            throw new IllegalArgumentException("Contact ID does not exist.");
        }
    }

    public void updateFirstName(String contactId, String firstName) {
        getContact(contactId).setFirstName(firstName);
    }

    public void updateLastName(String contactId, String lastName) {
        getContact(contactId).setLastName(lastName);
    }

    public void updatePhone(String contactId, String phone) {
        getContact(contactId).setPhone(phone);
    }

    public void updateAddress(String contactId, String address) {
        getContact(contactId).setAddress(address);
    }
}
