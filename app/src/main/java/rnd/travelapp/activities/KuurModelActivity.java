package rnd.travelapp.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import rnd.travelapp.R;
import rnd.travelapp.models.KuurModel;

public class KuurModelActivity extends ModelActivity<KuurModel> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kuur_beschrijving);
    }

    @Override
    protected void populateFromModel(KuurModel kuurModel) {
        ImageView kuurAfbeelding = findViewById(R.id.kuur_afbeelding);
        TextView kuurTitel = findViewById(R.id.kuur_titel);
        TextView kuurBeschrijving = findViewById(R.id.kuur_beschrijving);

        kuurModel.getKuurAfbeelding().getOrFetchToImageView(appCache, kuurAfbeelding);
        kuurTitel.setText(kuurModel.getKuurTitel());
        kuurBeschrijving.setText(kuurModel.getBeschrijving().getSpannedString(this));

        // biem nog knop met 'Toon bestemmingen'
    }

    @Override
    protected Class<KuurModel> getModelType() {
        return KuurModel.class;
    }
}
