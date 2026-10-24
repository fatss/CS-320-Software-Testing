package contactservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class ContactTest {
    private Contact contact() {
        return new Contact("C1", "Alice", "Smith", "0123456789", "123 Main Street");
    }

    @Test
    void storesValidFields() {
        Contact contact = contact();
        assertAll(
            () -> assertEquals("C1", contact.getContactId()),
            () -> assertEquals("Alice", contact.getFirstName()),
            () -> assertEquals("Smith", contact.getLastName()),
            () -> assertEquals("0123456789", contact.getPhone()),
            () -> assertEquals("123 Main Street", contact.getAddress())
        );
    }

    @Test
    void acceptsMaximumLengths() {
        Contact contact = new Contact("1234567890", "F".repeat(10), "L".repeat(10),
                                      "0000000000", "A".repeat(30));
        assertAll(
            () -> assertEquals("1234567890", contact.getContactId()),
            () -> assertEquals("F".repeat(10), contact.getFirstName()),
            () -> assertEquals("L".repeat(10), contact.getLastName()),
            () -> assertEquals("0000000000", contact.getPhone()),
            () -> assertEquals("A".repeat(30), contact.getAddress())
        );
    }

    @Test
    void acceptsEmptyStringsWhereOnlyNonNullAndMaximumLengthAreRequired() {
        Contact contact = new Contact("", "", "", "0123456789", "");
        assertAll(
            () -> assertEquals("", contact.getContactId()),
            () -> assertEquals("", contact.getFirstName()),
            () -> assertEquals("", contact.getLastName()),
            () -> assertEquals("", contact.getAddress())
        );
    }

    @Test
    void rejectsNullConstructorFields() {
        assertAll(
            () -> assertThrows(IllegalArgumentException.class,
                () -> new Contact(null, "Alice", "Smith", "0123456789", "Home")),
            () -> assertThrows(IllegalArgumentException.class,
                () -> new Contact("C1", null, "Smith", "0123456789", "Home")),
            () -> assertThrows(IllegalArgumentException.class,
                () -> new Contact("C1", "Alice", null, "0123456789", "Home")),
            () -> assertThrows(IllegalArgumentException.class,
                () -> new Contact("C1", "Alice", "Smith", "0123456789", null))
        );
    }

    @Test
    void rejectsConstructorFieldsOneCharacterOverTheirLimits() {
        assertAll(
            () -> assertThrows(IllegalArgumentException.class,
                () -> new Contact("12345678901", "Alice", "Smith", "0123456789", "Home")),
            () -> assertThrows(IllegalArgumentException.class,
                () -> new Contact("C1", "F".repeat(11), "Smith", "0123456789", "Home")),
            () -> assertThrows(IllegalArgumentException.class,
                () -> new Contact("C1", "Alice", "L".repeat(11), "0123456789", "Home")),
            () -> assertThrows(IllegalArgumentException.class,
                () -> new Contact("C1", "Alice", "Smith", "0123456789", "A".repeat(31)))
        );
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "123456789", "12345678901", "12345abcde", "+123456789",
                            "123456789 ", "123456789\n", "１２３４５６７８９０"})
    void rejectsInvalidPhoneOnConstructionAndUpdate(String phone) {
        assertThrows(IllegalArgumentException.class,
            () -> new Contact("C1", "Alice", "Smith", phone, "Home"));
        Contact contact = contact();
        assertThrows(IllegalArgumentException.class, () -> contact.setPhone(phone));
        assertEquals("0123456789", contact.getPhone());
    }

    @Test
    void updatesFieldsAtTheirMaximumLengthsWithoutChangingId() {
        Contact contact = contact();
        contact.setFirstName("F".repeat(10));
        contact.setLastName("L".repeat(10));
        contact.setPhone("9876543210");
        contact.setAddress("A".repeat(30));
        assertAll(
            () -> assertEquals("C1", contact.getContactId()),
            () -> assertEquals("F".repeat(10), contact.getFirstName()),
            () -> assertEquals("L".repeat(10), contact.getLastName()),
            () -> assertEquals("9876543210", contact.getPhone()),
            () -> assertEquals("A".repeat(30), contact.getAddress())
        );
    }

    @Test
    void invalidUpdatesPreservePreviousFields() {
        Contact contact = contact();
        assertAll(
            () -> assertThrows(IllegalArgumentException.class, () -> contact.setFirstName(null)),
            () -> assertThrows(IllegalArgumentException.class, () -> contact.setFirstName("F".repeat(11))),
            () -> assertThrows(IllegalArgumentException.class, () -> contact.setLastName(null)),
            () -> assertThrows(IllegalArgumentException.class, () -> contact.setLastName("L".repeat(11))),
            () -> assertThrows(IllegalArgumentException.class, () -> contact.setAddress(null)),
            () -> assertThrows(IllegalArgumentException.class, () -> contact.setAddress("A".repeat(31)))
        );
        assertAll(
            () -> assertEquals("Alice", contact.getFirstName()),
            () -> assertEquals("Smith", contact.getLastName()),
            () -> assertEquals("123 Main Street", contact.getAddress())
        );
    }
}
