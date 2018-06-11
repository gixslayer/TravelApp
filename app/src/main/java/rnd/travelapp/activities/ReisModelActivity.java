package rnd.travelapp.activities;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import rnd.travelapp.R;
import rnd.travelapp.models.ReisModel;

public class ReisModelActivity extends ModelActivity<ReisModel> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reis_beschrijving);
    }

    @Override
    protected void populateFromModel(ReisModel reisModel) {
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

        reisModel.getReisAfbeelding().getOrFetchToImageView(appCache, reisAfbeelding);
        reisModel.getKurenAfbeelding().getOrFetchToImageView(appCache, kurenAfbeelding);
        reisModel.getOmgevingAfbeelding().getOrFetchToImageView(appCache, omgevingAfbeelding);
        reisModel.getHotelsAfbeelding().getOrFetchToImageView(appCache, hotelsAfbeelding);

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
        hotelsLangeBeschrijving.setText(reisModel.getHotelsLangeBeschrijving().getSpannedString(this));

        // Expand buttons
        ImageButton expandKurenButton = findViewById(R.id.btn_expand_kuren);
        ImageButton expandOmgevingButton = findViewById(R.id.btn_expand_omgeving);
        ImageButton expandHotelsButton = findViewById(R.id.btn_expand_hotels);

        expandKurenButton.setOnClickListener(new ExpandButtonListener(kurenLangeBeschrijving));
        expandOmgevingButton.setOnClickListener(new ExpandButtonListener(omgevingLangeBeschrijving));
        expandHotelsButton.setOnClickListener(new ExpandButtonListener(hotelsLangeBeschrijving));

        // Boek button
        String modelKey = getIntent().getStringExtra(MODEL_KEY);
        View reisBoekBtn = findViewById(R.id.reis_boek_btn);
        reisBoekBtn.setOnClickListener(new BoekListener(modelKey));

        // HIER MOETEN NOG DE KUUR BUTTONS LISTENERS KRIJGEN
    }

    @Override
    protected Class<ReisModel> getModelType() {
        return ReisModel.class;
    }

    private class BoekListener implements View.OnClickListener {
        private final String modelKey;

        private BoekListener(String modelKey) {
            this.modelKey = modelKey;
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), BoekActivity.class);
            intent.putExtra(BoekActivity.REIS_KEY, modelKey);

            startActivity(intent);
        }
    }

    private class ExpandButtonListener implements View.OnClickListener {
        private final View expandable;
//        private final int oldHeight;
//        private final ValueAnimator slide_down;
//        private final ValueAnimator slide_up;

        public ExpandButtonListener(View expandable) {
            this.expandable = expandable;
//            this.oldHeight = expandable.getHeight();
//            ViewGroup.LayoutParams params = expandable.getLayoutParams();
//            params.height = 0;
//            expandable.setLayoutParams(params);
//            this.slide_down = getToggleAnimation(expandable, 0, oldHeight);
//            this.slide_up = getToggleAnimation(expandable, oldHeight, 0);
        }

        public void onClick(View button) {
            expandable.setVisibility(expandable.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            button.setBackgroundResource(expandable.getVisibility() == View.VISIBLE ? R.drawable.minimize_btn_dark : R.drawable.expand_btn_dark);

        }

//        public ExpandButtonListener(View expandable, int oldHeight) {
//            this.expandable = expandable;
//            this.oldHeight = oldHeight;
//            ViewGroup.LayoutParams params = expandable.getLayoutParams();
//            params.height = 0;
//            expandable.setLayoutParams(params);
//            this.slide_down = getToggleAnimation(expandable, 0, oldHeight);
//            this.slide_up = getToggleAnimation(expandable, oldHeight, 0);
//        }

//        public void onClick(View button) {
//            if (expandable.getLayoutParams().height == oldHeight) {
//                slide_up.start();
//            } else {
//                slide_down.start();
//            }
//        }
    }

    private ValueAnimator getToggleAnimation(View view, int startHeight, int endHeight) {
        ValueAnimator animator = ValueAnimator.ofInt(startHeight, endHeight);
        animator.setDuration(300);

        animator.addUpdateListener(animation -> {
            int val = (int) animation.getAnimatedValue();
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.height = val;
            view.setLayoutParams(params);
        });

        return animator;
    }
}
