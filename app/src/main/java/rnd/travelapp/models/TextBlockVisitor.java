package rnd.travelapp.models;

interface TextBlockVisitor<R> {
    R visit (TextSection textSection);

    R visit (TextBody textBody);
}
