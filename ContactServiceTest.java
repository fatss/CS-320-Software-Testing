package contactservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class ContactServiceTest {
    private ContactService service;
    private Contact original;

    @BeforeEach
    void setUp() {
        service = new ContactService();
        original = new Contact("C1", "Alice", "Smith", "0123456789", "123 Main Street");
        service.addContact(original);
    }

    @Test
    void addsAndRetrievesMultipleUniqueContacts() {
        Contact second = new Contact("C2", "Bob", "Jones", "9876543210", "456 Oak Street");
        service.addContact(second);
        assertSame(original, service.getContact("C1"));
        assertSame(second, service.getContact("C2"));
    }

    @Test
    void rejectsDuplicateIdWithoutReplacingOriginal() {
        Contact duplicate = new Contact("C1", "Bob", "Jones", "9876543210", "Other Address");
        assertThrows(IllegalArgumentException.class, () -> service.addContact(duplicate));
        assertSame(original, service.getContact("C1"));
    }

    @Test
    void rejectsNullContact() {
        assertThrows(IllegalArgumentException.class, () -> service.addContact(null));
        assertSame(original, service.getContact("C1"));
    }

    @Test
    void deletesOnlySelectedContactAndAllowsIdReuse() {
        Contact second = new Contact("C2", "Bob", "Jones", "9876543210", "Home");
        service.addContact(second);
        service.deleteContact("C1");
        assertThrows(IllegalArgumentException.class, () -> service.getContact("C1"));
        assertSame(second, service.getContact("C2"));
        service.addContact(original);
        assertSame(original, service.getContact("C1"));
    }

    @Test
    void updatesAllAllowedFieldsForSelectedId() {
        Contact second = new Contact("C2", "Bob", "Jones", "9876543210", "Home");
        service.addContact(second);
        service.updateFirstName("C1", "F".repeat(10));
        service.updateLastName("C1", "L".repeat(10));
        service.updatePhone("C1", "1112223333");
        service.updateAddress("C1", "A".repeat(30));
        Contact updated = service.getContact("C1");
        assertAll(
            () -> assertEquals("C1", updated.getContactId()),
            () -> assertEquals("F".repeat(10), updated.getFirstName()),
            () -> assertEquals("L".repeat(10), updated.getLastName()),
            () -> assertEquals("1112223333", updated.getPhone()),
            () -> assertEquals("A".repeat(30), updated.getAddress()),
            () -> assertEquals("Bob", second.getFirstName()),
            () -> assertEquals("Jones", second.getLastName()),
            () -> assertEquals("9876543210", second.getPhone()),
            () -> assertEquals("Home", second.getAddress())
        );
    }

    @Test
    void rejectsInvalidUpdatesAndPreservesStoredValues() {
        assertAll(
            () -> assertThrows(IllegalArgumentException.class, () -> service.updateFirstName("C1", null)),
            () -> assertThrows(IllegalArgumentException.class, () -> service.updateFirstName("C1", "F".repeat(11))),
            () -> assertThrows(IllegalArgumentException.class, () -> service.updateLastName("C1", null)),
            () -> assertThrows(IllegalArgumentException.class, () -> service.updateLastName("C1", "L".repeat(11))),
            () -> assertThrows(IllegalArgumentException.class, () -> service.updatePhone("C1", null)),
            () -> assertThrows(IllegalArgumentException.class, () -> service.updatePhone("C1", "123456789")),
            () -> assertThrows(IllegalArgumentException.class, () -> service.updatePhone("C1", "12345678901")),
            () -> assertThrows(IllegalArgumentException.class, () -> service.updatePhone("C1", "12345abcde")),
            () -> assertThrows(IllegalArgumentException.class, () -> service.updateAddress("C1", null)),
            () -> assertThrows(IllegalArgumentException.class, () -> service.updateAddress("C1", "A".repeat(31)))
        );
        Contact stored = service.getContact("C1");
        assertAll(
            () -> assertEquals("C1", stored.getContactId()),
            () -> assertEquals("Alice", stored.getFirstName()),
            () -> assertEquals("Smith", stored.getLastName()),
            () -> assertEquals("0123456789", stored.getPhone()),
            () -> assertEquals("123 Main Street", stored.getAddress())
        );
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"missing", ""})
    void rejectsLookupDeletionAndUpdatesForUnknownIds(String id) {
        assertAll(
            () -> assertThrows(IllegalArgumentException.class, () -> service.getContact(id)),
            () -> assertThrows(IllegalArgumentException.class, () -> service.deleteContact(id)),
            () -> assertThrows(IllegalArgumentException.class, () -> service.updateFirstName(id, "Bob")),
            () -> assertThrows(IllegalArgumentException.class, () -> service.updateLastName(id, "Jones")),
            () -> assertThrows(IllegalArgumentException.class, () -> service.updatePhone(id, "9876543210")),
            () -> assertThrows(IllegalArgumentException.class, () -> service.updateAddress(id, "Home"))
        );
        assertSame(original, service.getContact("C1"));
    }

    @Test
    void serviceInstancesHaveIndependentStorage() {
        ContactService other = new ContactService();
        other.addContact(new Contact("C1", "Bob", "Jones", "9876543210", "Home"));
        assertEquals("Alice", service.getContact("C1").getFirstName());
        assertEquals("Bob", other.getContact("C1").getFirstName());
    }
}
