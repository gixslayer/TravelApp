package rnd.travelapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

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
        TextView kuurBestemmingenBtn = findViewById(R.id.kuur_bestemmingen_btn);

        kuurModel.getKuurAfbeelding().getOrFetchToImageView(appCache, kuurAfbeelding);
        kuurTitel.setText(kuurModel.getKuurTitel());
        kuurBeschrijving.setText(kuurModel.getBeschrijving().getSpannedString(this));

        kuurBestemmingenBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), ReisAanbodActivity.class);
            intent.putExtra("filters", new String[]{kuurModel.getKuurTitel()});

            startActivity(intent);
        });
    }

    @Override
    protected Class<KuurModel> getModelType() {
        return KuurModel.class;
    }
}
