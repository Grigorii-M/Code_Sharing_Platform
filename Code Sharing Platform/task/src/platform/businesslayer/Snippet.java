package platform.businesslayer;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(name = "SNIPPETS")
@Data
public class Snippet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    private Long id;
    private String code;
    private LocalDateTime date;
    @Transient
    private static final String DATE_FORMATTER_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private LocalDateTime accessDeadline;
    private int viewThreshold;
    private int timesViewed;

    boolean hasAccessDeadline;
    boolean hasViewsThreshold;

    @Setter(AccessLevel.NONE)
    private UUID uuid;

    public Snippet() {
        this("", 0, 0);
    }
    public Snippet(String code, int lifespanSeconds, int viewThreshold) {
        this.code = code;
        date = LocalDateTime.now();
        uuid = UUID.randomUUID();
        accessDeadline = date.plusSeconds(lifespanSeconds);
        this.viewThreshold = viewThreshold;
        timesViewed = 0;
        hasAccessDeadline = lifespanSeconds > 0;
        hasViewsThreshold = viewThreshold > 0;
    }

    public long getRemainingTime() {
        long time = ChronoUnit.SECONDS.between(LocalDateTime.now(), accessDeadline);
        return time >= 0 ? time : 0;
    }
    public String getDate() {
        return date.format(DateTimeFormatter.ofPattern(DATE_FORMATTER_PATTERN));
    }

    @Data
    public static class SnippetJsonRepresentation {
        private final String code;
        private final String date;
        private final int views;
        private final long time;

        private SnippetJsonRepresentation(String code, String date, int views, long time) {
            this.code = code;
            this.date = date;
            this.views = views;
            this.time = time;
        }
    }

    public SnippetJsonRepresentation getJsonRepresentation() {
        return new SnippetJsonRepresentation(code, getDate(), hasViewsThreshold ? viewThreshold - timesViewed: 0, hasAccessDeadline ? getRemainingTime() : 0);
    }
}
