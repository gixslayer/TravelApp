package rnd.travelapp.models;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.SpannedString;
import android.text.style.TextAppearanceSpan;

import rnd.travelapp.R;

public class SpannableTextBlockVisitor implements TextBlockVisitor<SpannedString> {
    private final Context context;
    private int headerType = 1;

    public SpannableTextBlockVisitor(Context context) {
        this.context = context;
    }


    @Override
    public SpannedString visit(TextSection textSection) {
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        
        SpannableString titel = new SpannableString(textSection.getTitel());
        titel.setSpan(new TextAppearanceSpan(context, HeaderStyles.get(headerType)), 0, titel.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.append(titel);
        ssb.append('\n');

        for (TextBlock tb : textSection.getSubsections()) {
            headerType++;
            ssb.append(tb.accept(this));
            headerType--;
        }
        
        return new SpannedString(ssb);
    }

    @Override
    public SpannedString visit(TextBody textBody) {
        return new SpannedString(textBody.getContent() + "\n\n");
    }
}
