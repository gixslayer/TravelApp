package rnd.travelapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import rnd.travelapp.models.ReisModel;

public class ReisModelActivity extends CacheActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reis_beschrijving);
    }

    @Override
    protected void onCacheInitialized() {
        String key = getIntent().getStringExtra("model");

        appCache.getOrFetch(key, ReisModel.class).onCompletion(result ->
                result.consume(this::populateFromModel, this::handleMissingModel));
    }

    private void populateFromModel(ReisModel reisModel) {
        ImageView reisAfbeelding = findViewById(R.id.reis_afbeelding);
        TextView reisTitel = findViewById(R.id.reis_titel);
        TextView reisType = findViewById(R.id.reis_type);
        TextView reisAangebodenKuren = findViewById(R.id.reis_aangeboden_kuren);
        TextView reisAlgemeneBeschrijving = findViewById(R.id.reis_algemene_beschrijving);
        ImageView kurenAfbeelding = findViewById(R.id.kuren_afbeelding);
        TextView kurenTitel = findViewById(R.id.kuren_titel);
        TextView kurenKorteBeschrijving = findViewById(R.id.kuren_korte_beschrijving);
        TextView kurenLangeBeschrijving = findViewById(R.id.kuren_lange_beschrijving);
        ImageView omgevingAfbeelding = findViewById(R.id.omgeving_afbeelding);
        TextView omgevingTitel = findViewById(R.id.omgeving_titel);
        TextView omgevingKorteBeschrijving = findViewById(R.id.omgeving_korte_beschrijving);
        TextView omgevingLangeBeschrijving = findViewById(R.id.omgeving_lange_beschrijving);
        ImageView hotelsAfbeelding = findViewById(R.id.hotels_afbeelding);
        TextView hotelsTitel = findViewById(R.id.hotels_titel);
        TextView hotelsKorteBeschrijving = findViewById(R.id.hotels_korte_beschrijving);
        TextView hotelsLangeBeschrijving = findViewById(R.id.hotels_lange_beschrijving);

        reisModel.getReisAfbeelding().getOrFetch(this, result -> result.consume(reisAfbeelding::setImageBitmap, cause -> {
            Log.e("TRAVEL_APP", "Could not fetch image", cause);
        }));
        reisModel.getKurenAfbeelding().getOrFetch(this, result -> result.consume(kurenAfbeelding::setImageBitmap, cause -> {
            Log.e("TRAVEL_APP", "Could not fetch image", cause);
        }));
        reisModel.getOmgevingAfbeelding().getOrFetch(this, result -> result.consume(omgevingAfbeelding::setImageBitmap, cause -> {
            Log.e("TRAVEL_APP", "Could not fetch image", cause);
        }));
        reisModel.getHotelsAfbeelding().getOrFetch(this, result -> result.consume(hotelsAfbeelding::setImageBitmap, cause -> {
            Log.e("TRAVEL_APP", "Could not fetch image", cause);
        }));

        List<String> kuren = new ArrayList<>(reisModel.getKuren().keySet());
        if (!kuren.isEmpty()) {
            StringBuilder kurenBuilder = new StringBuilder(kuren.get(0));
            if (kuren.size() > 1) {
                for (int i = 1; i < kuren.size(); i++)
                    kurenBuilder.append(", ").append(kuren.get(i));
            }
            reisAangebodenKuren.setText(kurenBuilder);
        }

        reisTitel.setText(reisModel.getReisTitel());
        reisType.setText(reisModel.getReisType());
        reisAlgemeneBeschrijving.setText(reisModel.getAlgemeneBeschrijving());
        kurenTitel.setText(reisModel.getKurenTitel());
        kurenKorteBeschrijving.setText(reisModel.getKurenKorteBeschrijving());
        kurenLangeBeschrijving.setText(reisModel.getKurenLangeBeschrijving());
        omgevingTitel.setText(reisModel.getOmgevingTitel());
        omgevingKorteBeschrijving.setText(reisModel.getOmgevingKorteBeschrijving());
        omgevingLangeBeschrijving.setText(reisModel.getOmgevingLangeBeschrijving());
        hotelsTitel.setText(reisModel.getHotelsTitel());
        hotelsKorteBeschrijving.setText(reisModel.getHotelsKorteBeschrijving());
        hotelsLangeBeschrijving.setText(reisModel.getHotelsLangeBeschrijving());

        // HIER MOETEN NOG DE KUUR BUTTONS LISTENERS KRIJGEN
    }

    private void handleMissingModel(Throwable cause) {
        String key = getIntent().getStringExtra("model");

        Log.e("TRAVEL_APP", "Missing model: " + key, cause);

        finish();
    }
}
