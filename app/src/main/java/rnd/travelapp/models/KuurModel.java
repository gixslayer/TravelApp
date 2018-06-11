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
    private final String kuurTitel;
    private final String kuurType;
    private final String kuurKorteBeschrijving;

    private final TextSection kuurAlgemeneBeschrijving;
    private final TextSection kuurKlachtSoorten;
    private final TextSection kuurProgramma;

    public KuurModel(List<String> tags, BitmapResource kuurAfbeelding, String kuurTitel, String kuurType, String kuurKorteBeschrijving, TextSection kuurAlgemeneBeschrijving, TextSection kuurKlachtSoorten, TextSection kuurProgramma) {
        this.tags = tags;
        this.kuurAfbeelding = kuurAfbeelding;
        this.kuurTitel = kuurTitel;
        this.kuurType = kuurType;
        this.kuurKorteBeschrijving = kuurKorteBeschrijving;
        this.kuurAlgemeneBeschrijving = kuurAlgemeneBeschrijving;
        this.kuurKlachtSoorten = kuurKlachtSoorten;
        this.kuurProgramma = kuurProgramma;
    }

    public BitmapResource getKuurAfbeelding() {
        return kuurAfbeelding;
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
    public TextSection getAlgemeneBeschrijving() {
        return kuurAlgemeneBeschrijving;
    }

    public TextSection getKuurKlachtSoorten() {
        return kuurKlachtSoorten;
    }

    public TextSection getKuurProgramma() {
        return kuurProgramma;
    }

    protected static class Loader implements JSONLoader<KuurModel> {
        @Override
        public KuurModel deserialize(JSONObject object) throws JSONException {
            JSONArray tagsJSON = object.getJSONArray("tags");
            List<String> tags = new ArrayList<>();
            for (int i = 0; i < tagsJSON.length(); i++)
                tags.add(tagsJSON.getString(i));
            BitmapResource kuurAfbeelding = new BitmapResource(object.getString("afbeelding"));
            String kuurTitel = object.getString("titel");
            String kuurType = object.getString("kuur_type");
            String kuurKorteBeschrijving = object.getString("korte_beschrijving");

            TextSection kuurAlgemeneBeschrijving = new TextSection(object.getJSONObject("algemene_beschrijving"));
            TextSection kuurKlachtSoorten = new TextSection(object.getJSONObject("klacht_soorten"));
            TextSection kuurProgramma = new TextSection(object.getJSONObject("kuur_programma"));

            return new KuurModel(tags, kuurAfbeelding, kuurTitel, kuurType, kuurKorteBeschrijving, kuurAlgemeneBeschrijving, kuurKlachtSoorten, kuurProgramma);
        }

        @Override
        public Class<KuurModel> getType() {
            return KuurModel.class;
        }
    }
}
