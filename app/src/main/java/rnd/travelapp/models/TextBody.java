package rnd.travelapp.models;

public class TextBody implements TextBlock {
    private final String content;

    TextBody(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @Override
    public <R> R accept(TextBlockVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
