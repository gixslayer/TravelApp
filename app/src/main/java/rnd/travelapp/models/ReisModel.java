package rnd.travelapp.models;

import org.json.JSONException;
import org.json.JSONObject;

import rnd.travelapp.serialization.JSONLoadable;
import rnd.travelapp.serialization.JSONLoader;

@JSONLoadable(ReisModel.Loader.class)
public class ReisModel {

    private final TextSection algemeen;
    private final TextSection reisInformatie;
    private final TextSection ligging;

    public ReisModel(TextSection algemeen, TextSection reisInformatie, TextSection ligging) {
        this.algemeen = algemeen;
        this.reisInformatie = reisInformatie;
        this.ligging = ligging;
    }

    public TextSection getAlgemeen() {
        return algemeen;
    }

    public TextSection getReisInformatie() {
        return reisInformatie;
    }

    public TextSection getLigging() {
        return ligging;
    }

    protected static class Loader implements JSONLoader<ReisModel> {
        @Override
        public ReisModel deserialize(JSONObject object) throws JSONException {
            TextSection algemeen = new TextSection(object.getJSONObject("algemeen"));
            TextSection reisInformatie = new TextSection(object.getJSONObject("reisInformatie"));
            TextSection ligging = new TextSection(object.getJSONObject("ligging"));
            return new ReisModel(algemeen, reisInformatie, ligging);
        }

        @Override
        public Class<ReisModel> getType() {
            return ReisModel.class;
        }
    }
}
