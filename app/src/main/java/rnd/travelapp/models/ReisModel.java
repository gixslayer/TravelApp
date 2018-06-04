package rnd.travelapp.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import rnd.travelapp.serialization.JSONLoadable;
import rnd.travelapp.serialization.JSONLoader;

//@JSONLoadable(ReisModel.Loader.class)
public class ReisModel {

    private final String shortDescription;
    private final TextSection algemeen;
    private final TextSection reisInformatie;
    private final TextSection ligging;
    private List<String> tags;

    public ReisModel(JSONObject object) throws JSONException {
        this.shortDescription = object.getString("shord_description");
        this.algemeen = new TextSection(object.getJSONObject("algemeen"));
        this.reisInformatie = new TextSection(object.getJSONObject("reisinformatie"));
        this.ligging = new TextSection(object.getJSONObject("ligging"));
    }

    public String getShortDescription() {
        return shortDescription;
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

//    protected static class Loader implements JSONLoader<ReisModel> {
//        @Override
//        public ReisModel deserialize(JSONObject object) throws JSONException {
//            TextSection algemeen = new TextSection(object.getJSONObject("algemeen"));
//            TextSection reisInformatie = new TextSection(object.getJSONObject("reisInformatie"));
//            TextSection ligging = new TextSection(object.getJSONObject("ligging"));
//            return new ReisModel(algemeen, reisInformatie, ligging);
//        }
//
//        @Override
//        public Class<ReisModel> getType() {
//            return ReisModel.class;
//        }
//    }
}
