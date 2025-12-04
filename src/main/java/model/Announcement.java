package model;

import java.util.Date;

/**
 * Announcement model representing announcements in the quiz system
 * Corresponds to the 'announcements' table in the database
 */
public class Announcement {
    
    public enum Priority {
        LOW("low"),
        MEDIUM("medium"),
        HIGH("high");
        
        private final String value;
        
        Priority(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static Priority fromString(String value) {
            for (Priority priority : Priority.values()) {
                if (priority.getValue().equalsIgnoreCase(value)) {
                    return priority;
                }
            }
            return MEDIUM; // Default fallback
        }
    }
    
    private int id;
    private String title;
    private String content;
    private int createdBy;
    private Date createdDate;
    private boolean isActive;
    private Priority priority;
    
    // Default constructor
    public Announcement() {
        this.createdDate = new Date();
        this.isActive = true;
        this.priority = Priority.MEDIUM;
    }
    
    // Constructor for new announcement creation
    public Announcement(String title, String content, int createdBy) {
        this();
        this.title = title;
        this.content = content;
        this.createdBy = createdBy;
    }
    
    // Constructor with priority
    public Announcement(String title, String content, int createdBy, Priority priority) {
        this(title, content, createdBy);
        this.priority = priority;
    }
    
    // Full constructor
    public Announcement(int id, String title, String content, int createdBy, Date createdDate, boolean isActive, Priority priority) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.isActive = isActive;
        this.priority = priority;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public int getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }
    
    public Date getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public Priority getPriority() {
        return priority;
    }
    
    public void setPriority(Priority priority) {
        this.priority = priority;
    }
    
    @Override
    public String toString() {
        return "Announcement{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", createdBy=" + createdBy +
                ", createdDate=" + createdDate +
                ", isActive=" + isActive +
                ", priority=" + priority +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Announcement announcement = (Announcement) obj;
        return id == announcement.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
} 