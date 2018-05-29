package rnd.travelapp.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TextSection implements TextBlock{
    private final String type;
    private final String titel;
    private final List<TextBlock> subsections;

    TextSection(JSONObject object) throws JSONException {
        this.type = object.getString("type");
        this.titel = object.getString("titel");
        this.subsections = new ArrayList<>();
        JSONArray subsectionsJSON = object.getJSONArray("subsections");
        for (int i = 0; i < subsectionsJSON.length(); i++) {
            if (type.equals("section")) {
                this.subsections.add(new TextSection(subsectionsJSON.getJSONObject(i)));
            }
            else if (type.equals("body")) {
                this.subsections.add(new TextBody(subsectionsJSON.getString(i)));
            }
        }
    }

    public String getType() {
        return type;
    }

    public String getTitel() {
        return titel;
    }

    public List<TextBlock> getSubsections(){
        return subsections;
    }

    public TextBlock getSubsection(int i) {
        return subsections.get(i);
    }

    @Override
    public <R> R accept(TextBlockVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
