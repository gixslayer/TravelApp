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

@JSONLoadable(ReisModel.Loader.class)
public class ReisModel {

    private final List<String> tags;
    private final BitmapResource reisAfbeelding;
    private final BitmapResource reisThumbnail;
    private final String reisTitel;
    private final String reisType;
    private final String reisKorteBeschrijving;
    private final String reisAlgemeneBeschrijving;

    private final BitmapResource kurenAfbeelding;
    private final String kurenTitel;
    private final String kurenKorteBeschrijving;
    private final String kurenLangeBeschrijving;
    private final Map<String, String> kuren;

    private final BitmapResource omgevingAfbeelding;
    private final String omgevingTitel;
    private final String omgevingKorteBeschrijving;
    private final String omgevingLangeBeschrijving;

    private final BitmapResource hotelsAfbeelding;
    private final String hotelsTitel;
    private final String hotelsKorteBeschrijving;
    private final TextSection hotelsLangeBeschrijving;

    public ReisModel(List<String> tags, BitmapResource reisAfbeelding, BitmapResource reisThumbnail, String reisTitel, String reisType, String reisKorteBeschrijving, String reisAlgemeneBeschrijving, BitmapResource kurenAfbeelding, String kurenTitel, String kurenKorteBeschrijving, String kurenLangeBeschrijving, Map<String, String> kuren, BitmapResource omgevingAfbeelding, String omgevingTitel, String omgevingKorteBeschrijving, String omgevingLangeBeschrijving, BitmapResource hotelsAfbeelding, String hotelsTitel, String hotelsKorteBeschrijving, TextSection hotelsLangeBeschrijving) {
        this.tags = tags;
        this.reisAfbeelding = reisAfbeelding;
        this.reisThumbnail = reisThumbnail;
        this.reisTitel = reisTitel;
        this.reisType = reisType;
        this.reisKorteBeschrijving = reisKorteBeschrijving;
        this.reisAlgemeneBeschrijving = reisAlgemeneBeschrijving;
        this.kurenAfbeelding = kurenAfbeelding;
        this.kurenTitel = kurenTitel;
        this.kurenKorteBeschrijving = kurenKorteBeschrijving;
        this.kurenLangeBeschrijving = kurenLangeBeschrijving;
        this.kuren = kuren;
        this.omgevingAfbeelding = omgevingAfbeelding;
        this.omgevingTitel = omgevingTitel;
        this.omgevingKorteBeschrijving = omgevingKorteBeschrijving;
        this.omgevingLangeBeschrijving = omgevingLangeBeschrijving;
        this.hotelsAfbeelding = hotelsAfbeelding;
        this.hotelsTitel = hotelsTitel;
        this.hotelsKorteBeschrijving = hotelsKorteBeschrijving;
        this.hotelsLangeBeschrijving = hotelsLangeBeschrijving;
    }

    public BitmapResource getReisAfbeelding() {
        return reisAfbeelding;
    }
    public BitmapResource getReisThumbnail() {
        return reisThumbnail;
    }
    public String getReisTitel() {
        return reisTitel;
    }
    public String getReisType() {
        return reisType;
    }
    public String getReisKorteBeschrijving() {
        return reisKorteBeschrijving;
    }
    public String getAlgemeneBeschrijving() {
        return reisAlgemeneBeschrijving;
    }
    public List<String> getTags() {
        return tags;
    }
    public BitmapResource getKurenAfbeelding() {
        return kurenAfbeelding;
    }
    public String getKurenTitel() {
        return kurenTitel;
    }
    public String getKurenKorteBeschrijving() {
        return kurenKorteBeschrijving;
    }
    public String getKurenLangeBeschrijving() {
        return kurenLangeBeschrijving;
    }
    public Map<String, String> getKuren() {
        return kuren;
    }
    public BitmapResource getOmgevingAfbeelding() {
        return omgevingAfbeelding;
    }
    public String getOmgevingTitel() {
        return omgevingTitel;
    }
    public String getOmgevingKorteBeschrijving() {
        return omgevingKorteBeschrijving;
    }
    public String getOmgevingLangeBeschrijving() {
        return omgevingLangeBeschrijving;
    }
    public BitmapResource getHotelsAfbeelding() {
        return hotelsAfbeelding;
    }
    public String getHotelsTitel() {
        return hotelsTitel;
    }
    public String getHotelsKorteBeschrijving() {
        return hotelsKorteBeschrijving;
    }
    public TextSection getHotelsLangeBeschrijving() {
        return hotelsLangeBeschrijving;
    }

    protected static class Loader implements JSONLoader<ReisModel> {
        @Override
        public ReisModel deserialize(JSONObject object) throws JSONException {
            JSONArray tagsJSON = object.getJSONArray("tags");
            List<String> tags = new ArrayList<>();
            for (int i = 0; i < tagsJSON.length(); i++)
                tags.add(tagsJSON.getString(i));
            BitmapResource reisAfbeelding = new BitmapResource(object.getString("afbeelding"));
            BitmapResource reisThumbnail = new BitmapResource(object.getString("thumbnail"));
            String reisTitel = object.getString("titel");
            String reisType = object.getString("reis_type");
            String reisKorteBeschrijving = object.getString("korte_beschrijving");
            String reisAlgemeneBeschrijving = jsonArrayToParagraph(object.getJSONArray("algemene_beschrijving"));
            BitmapResource kurenAfbeelding = new BitmapResource(object.getJSONObject("kuren").getString("afbeelding"));
            String kurenTitel = object.getJSONObject("kuren").getString("titel");
            String kurenKorteBeschrijving = object.getJSONObject("kuren").getString("korte_beschrijving");
            String kurenLangeBeschrijving = jsonArrayToParagraph(object.getJSONObject("kuren").getJSONArray("lange_beschrijving"));
            JSONArray kurenJSON = object.getJSONArray("kurenreferenties");
            Map<String, String> kuren = new HashMap<>();
            for (int i = 0; i < kurenJSON.length(); i++)
                kuren.put(kurenJSON.getJSONObject(i).getString("naam"), kurenJSON.getJSONObject(i).getString("pad"));
            BitmapResource omgevingAfbeelding = new BitmapResource(object.getJSONObject("omgeving").getString("afbeelding"));
            String omgevingTitel = object.getJSONObject("omgeving").getString("titel");
            String omgevingKorteBeschrijving = object.getJSONObject("omgeving").getString("korte_beschrijving");
            String omgevingLangeBeschrijving = jsonArrayToParagraph(object.getJSONObject("omgeving").getJSONArray("lange_beschrijving"));
            BitmapResource hotelsAfbeelding = new BitmapResource(object.getJSONObject("hotels").getString("afbeelding"));
            String hotelsTitel = object.getJSONObject("hotels").getString("titel");
            String hotelsKorteBeschrijving = object.getJSONObject("hotels").getString("korte_beschrijving");
            TextSection hotelsLangeBeschrijving = new TextSection(object.getJSONObject("hotels").getJSONObject("lange_beschrijving"));

            return new ReisModel(tags, reisAfbeelding, reisThumbnail, reisTitel, reisType, reisKorteBeschrijving, reisAlgemeneBeschrijving, kurenAfbeelding, kurenTitel, kurenKorteBeschrijving, kurenLangeBeschrijving, kuren, omgevingAfbeelding, omgevingTitel, omgevingKorteBeschrijving, omgevingLangeBeschrijving, hotelsAfbeelding, hotelsTitel, hotelsKorteBeschrijving, hotelsLangeBeschrijving);
        }

        @Override
        public Class<ReisModel> getType() {
            return ReisModel.class;
        }

        private String jsonArrayToParagraph(JSONArray array) throws JSONException {
            StringBuilder sb = new StringBuilder();
            if (array != null && array.length() > 0)
                for (int i = 0; i < array.length(); i++)
                    sb.append(array.getString(i)).append("\n\n");
            return sb.toString();
        }
    }
}
