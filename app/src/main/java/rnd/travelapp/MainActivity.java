package rnd.travelapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import org.json.JSONException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import rnd.travelapp.resources.BitmapResource;
import rnd.travelapp.models.TestModel;
import rnd.travelapp.serialization.BinaryLoaders;
import rnd.travelapp.serialization.JSONLoaders;

public class MainActivity extends Activity {

    private InputStream getBitmapStream() {
        Bitmap bitmap = Bitmap.createBitmap(800, 600, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint red = new Paint();
        Paint white = new Paint();
        red.setColor(Color.RED);
        white.setColor(Color.WHITE);

        canvas.drawRect(0, 0, 800, 600, white);
        canvas.drawCircle(400, 300, 100, red);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);

        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            String json = "{\"int\":2,\"double\":3.14,\"string\":\"test string\"}";
            InputStream stream = getBitmapStream();

            TestModel model = JSONLoaders.deserialize(json, TestModel.class);
            Bitmap bitmap = BinaryLoaders.deserialize(stream, BitmapResource.class);

            json.isEmpty();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }
}
