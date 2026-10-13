package finserve.model;

public class Document {
    private final String docId;
    private final String title;
    private final String content;

    public Document(String docId, String title, String content) {
        this.docId = docId;
        this.title = title;
        this.content = content;
    }

    public String getDocId() { return docId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }

    @Override
    public String toString() {
        return String.format("%-8s %s", docId, title);
    }
}
