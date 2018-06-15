package rnd.travelapp.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rnd.travelapp.resources.BitmapResource;
import rnd.travelapp.serialization.JSONLoadable;
import rnd.travelapp.serialization.JSONLoader;

@JSONLoadable(KuurModel.Loader.class)
public class KuurModel {

    private final List<String> tags;
    private final BitmapResource kuurAfbeelding;
    private final BitmapResource kuurThumbnail;
    private final String kuurTitel;
    private final String kuurType;
    private final String kuurKorteBeschrijving;
    private final Map<String, String> reizen;

    private final TextSection beschrijving;

    public KuurModel(List<String> tags, BitmapResource kuurAfbeelding, BitmapResource kuurThumbnail, String kuurTitel, String kuurType, String kuurKorteBeschrijving, Map<String, String> reizen, TextSection beschrijving) {
        this.tags = tags;
        this.kuurAfbeelding = kuurAfbeelding;
        this.kuurThumbnail = kuurThumbnail;
        this.kuurTitel = kuurTitel;
        this.kuurType = kuurType;
        this.kuurKorteBeschrijving = kuurKorteBeschrijving;
        this.reizen = reizen;
        this.beschrijving = beschrijving;
    }

    public BitmapResource getKuurAfbeelding() {
        return kuurAfbeelding;
    }
    public BitmapResource getKuurThumbnail() {
        return kuurThumbnail;
    }
    public String getKuurTitel() {
        return kuurTitel;
    }
    public String getKuurType() {
        return kuurType;
    }
    public String getKuurKorteBeschrijving() {
        return kuurKorteBeschrijving;
    }
    public List<String> getTags() {
        return tags;
    }
    public Map<String, String> getReizen() {
        return reizen;
    }
    public TextSection getBeschrijving() {
        return beschrijving;
    }

    public static class Loader implements JSONLoader<KuurModel> {
        @Override
        public KuurModel deserialize(JSONObject object) throws JSONException {
            JSONArray tagsJSON = object.getJSONArray("tags");
            List<String> tags = new ArrayList<>();
            for (int i = 0; i < tagsJSON.length(); i++)
                tags.add(tagsJSON.getString(i));
            BitmapResource kuurAfbeelding = new BitmapResource(object.getString("afbeelding"));
            BitmapResource kuurThumbnail = new BitmapResource(object.getString("thumbnail"));
            String kuurTitel = object.getString("titel");
            String kuurType = object.getString("kuur_type");
            String kuurKorteBeschrijving = object.getString("korte_beschrijving");
            JSONArray reizenJSON = object.getJSONArray("reisreferenties");
            Map<String, String> reizen = new HashMap<>();
            for (int i = 0; i < reizenJSON.length(); i++)
                reizen.put(reizenJSON.getJSONObject(i).getString("naam"), reizenJSON.getJSONObject(i).getString("pad"));
            TextSection beschrijving = new TextSection(object.getJSONObject("beschrijving"));

            return new KuurModel(tags, kuurAfbeelding, kuurThumbnail, kuurTitel, kuurType, kuurKorteBeschrijving, reizen, beschrijving);
        }

        @Override
        public Class<KuurModel> getType() {
            return KuurModel.class;
        }
    }
}
