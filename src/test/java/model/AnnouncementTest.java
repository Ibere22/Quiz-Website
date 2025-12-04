package model;

import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

class AnnouncementTest {
    @Test
    void testDefaultConstructor() {
        Announcement a = new Announcement();
        assertNotNull(a.getCreatedDate());
        assertTrue(a.isActive());
        assertEquals(Announcement.Priority.MEDIUM, a.getPriority());
    }

    @Test
    void testConstructorWithTitleContentCreatedBy() {
        Announcement a = new Announcement("Title", "Content", 42);
        assertEquals("Title", a.getTitle());
        assertEquals("Content", a.getContent());
        assertEquals(42, a.getCreatedBy());
        assertTrue(a.isActive());
        assertEquals(Announcement.Priority.MEDIUM, a.getPriority());
    }

    @Test
    void testConstructorWithPriority() {
        Announcement a = new Announcement("T", "C", 1, Announcement.Priority.HIGH);
        assertEquals(Announcement.Priority.HIGH, a.getPriority());
    }

    @Test
    void testFullConstructorAndGettersSetters() {
        Date now = new Date();
        Announcement a = new Announcement(5, "T", "C", 2, now, false, Announcement.Priority.LOW);
        assertEquals(5, a.getId());
        assertEquals("T", a.getTitle());
        assertEquals("C", a.getContent());
        assertEquals(2, a.getCreatedBy());
        assertEquals(now, a.getCreatedDate());
        assertFalse(a.isActive());
        assertEquals(Announcement.Priority.LOW, a.getPriority());

        a.setId(10);
        a.setTitle("NewT");
        a.setContent("NewC");
        a.setCreatedBy(99);
        Date d2 = new Date(now.getTime() + 1000);
        a.setCreatedDate(d2);
        a.setActive(true);
        a.setPriority(Announcement.Priority.HIGH);
        assertEquals(10, a.getId());
        assertEquals("NewT", a.getTitle());
        assertEquals("NewC", a.getContent());
        assertEquals(99, a.getCreatedBy());
        assertEquals(d2, a.getCreatedDate());
        assertTrue(a.isActive());
        assertEquals(Announcement.Priority.HIGH, a.getPriority());
    }

    @Test
    void testPriorityEnum() {
        assertEquals("low", Announcement.Priority.LOW.getValue());
        assertEquals("medium", Announcement.Priority.MEDIUM.getValue());
        assertEquals("high", Announcement.Priority.HIGH.getValue());
        assertEquals(Announcement.Priority.LOW, Announcement.Priority.fromString("low"));
        assertEquals(Announcement.Priority.MEDIUM, Announcement.Priority.fromString("medium"));
        assertEquals(Announcement.Priority.HIGH, Announcement.Priority.fromString("high"));
        assertEquals(Announcement.Priority.MEDIUM, Announcement.Priority.fromString("unknown")); // fallback
    }

    @Test
    void testEqualsAndHashCode() {
        Announcement a1 = new Announcement();
        Announcement a2 = new Announcement();
        a1.setId(1);
        a2.setId(1);
        Announcement a3 = new Announcement();
        a3.setId(2);
        assertEquals(a1, a2);
        assertNotEquals(a1, a3);
        assertEquals(a1.hashCode(), a2.hashCode());
        assertNotEquals(a1.hashCode(), a3.hashCode());
        assertNotEquals(a1, null);
        assertNotEquals(a1, "string");
    }

    @Test
    void testToString() {
        Announcement a = new Announcement(1, "T", "C", 2, new Date(), true, Announcement.Priority.HIGH);
        String s = a.toString();
        assertTrue(s.contains("id=1"));
        assertTrue(s.contains("title='T'"));
        assertTrue(s.contains("content='C'"));
        assertTrue(s.contains("createdBy=2"));
        assertTrue(s.contains("isActive=true"));
        assertTrue(s.contains("priority=HIGH"));
    }
} 