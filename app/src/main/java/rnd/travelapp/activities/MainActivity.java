package rnd.travelapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import rnd.travelapp.R;

public class MainActivity extends VerifyCacheActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onCacheVerified() {
        View aanbodReizen = findViewById(R.id.aanbod_reizen);
        View aanbodKuren = findViewById(R.id.aanbod_kuren);

        aanbodReizen.setOnClickListener(view -> openActivity(ReisAanbodActivity.class));
        aanbodKuren.setOnClickListener(view -> openActivity(ReisAanbodActivity.class)); // TODO: Fix to kuur activity
    }

    private void openActivity(Class<?> activity) {
        Intent intent = new Intent(this, activity);

        startActivity(intent);
    }
}
