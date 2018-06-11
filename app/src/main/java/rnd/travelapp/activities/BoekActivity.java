package rnd.travelapp.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;

import rnd.travelapp.Booking;
import rnd.travelapp.R;

public class BoekActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boekingsformulier);
    }

    public void onBoekClicked(View view) {
        EditText voornaamVeld = findViewById(R.id.voornaam);
        EditText achternaamVeld = findViewById(R.id.achternaam);
        EditText gebdatumVeld = findViewById(R.id.gebdatum);
        EditText straatHuisNrVeld = findViewById(R.id.straat_huisnr);
        EditText postcodeVeld = findViewById(R.id.postcode);
        EditText stadVeld = findViewById(R.id.stad);
        EditText telefoonVeld = findViewById(R.id.telefoon);
        EditText emailVeld = findViewById(R.id.email);

        String voornaam = voornaamVeld.getText().toString();
        String achternaam = achternaamVeld.getText().toString();
        String gebdatum = gebdatumVeld.getText().toString();
        String straatHuisNr = straatHuisNrVeld.getText().toString();
        String postcode = postcodeVeld.getText().toString();
        String stad = stadVeld.getText().toString();
        String telefoon = telefoonVeld.getText().toString();
        String email = emailVeld.getText().toString();

        new Booking(this, voornaam, achternaam, gebdatum, straatHuisNr, postcode, stad, telefoon, email).send()
                .onSuccess(this::onBookingSucceeded)
                .orOnFailure(this::onBookingFailed);
    }

    private void onBookingSucceeded(int bookingID) {
        String message = String.format(Locale.US, "Het boeken is gelukt. Uw boekID is: %d", bookingID);

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void onBookingFailed(Throwable cause) {
        Toast.makeText(this, "Het boeken is mislukt: "  + cause.getLocalizedMessage(), Toast.LENGTH_LONG).show();

        Log.e("TRAVEL_APP", "Booking failed", cause);
    }
}
