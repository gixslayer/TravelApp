package rnd.travelapp.models;

public interface TextBlock {
    <R> R accept (TextBlockVisitor<R> visitor);
}
