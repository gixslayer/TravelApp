package rnd.travelapp;

import android.app.Activity;
import android.os.Bundle;
import android.text.SpannedString;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import rnd.travelapp.models.ReisModel;
import rnd.travelapp.models.SpannableTextBlockVisitor;

public class ReisActivity extends Activity {
    private ReisModel reisModel;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reis_beschrijving);

        ////////////////////////////////////////// TEMP //////////////////////////////////////////////
        String content;
        Scanner scanner = null;
        JSONObject jsonObject;
        try {
            scanner = new Scanner( new File("../../../res/raw/smarjeske_toplice.txt") );

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (scanner != null) {
            content = scanner.useDelimiter("\\A").next();
            scanner.close();
            try {
                jsonObject = new JSONObject(content);
                reisModel = new ReisModel(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            SpannableTextBlockVisitor stbv = new SpannableTextBlockVisitor(this);
            SpannedString algemeenString = reisModel.getAlgemeen().accept(stbv);

            TextView algemeen = findViewById(R.id.text_algemeen);
            algemeen.setText(algemeenString);
        }
        else {
            try {
                jsonObject = new JSONObject("{\n" +
                        "    \"algemeen\": {\n" +
                        "        \"type\": \"section\",\n" +
                        "        \"titel\": \"Smarjeske Toplice (Slovenië)\",\n" +
                        "        \"subsections\": [\n" +
                        "            {\n" +
                        "                \"type\": \"body\",\n" +
                        "                \"titel\": \"Smarjeske Toplice (Slovenië)\",\n" +
                        "                \"subsections\": [\n" +
                        "                    \"Smarjeske Toplice is een vooraanstaand kenniscentrum op het gebied van leefstijl, gezondheidspreventie en voedingsleer. Hier kunt u verantwoord en resultaat gericht detoxen, afslanken of een op conditie verbetering, weerstand verhoging en stress vermindering gericht Health Booster programma volgen. Kuurprogramma’s die zonder meer tot de beste in Europa behoren en zich onderscheiden door hun uitermate professionele opzet, intensiteit en persoonlijke aanpak!\",\n" +
                        "                    \"Smarjeske Toplice beschikt over een drietal hotelvleugels, die onderling met elkaar verbonden zijn en o.m. de lobby, bar, restaurants en alle overige faciliteiten delen. Als Fontana gast verblijft u of in Hotel Vitarium of in Hotel Smarjeta.\",\n" +
                        "                    \"Naast een medische kuurkliniek, beschikt dit kuuroord over een sfeervol wellness centrum (Vitarium Spa & Clinique) met o.a. een uitgebreid saunalandschap, thermale binnen- en buitenbaden (32°C) en moderne sportfaciliteiten. In Vitarium Spa & Clinique worden alle detox-, afslank- en Health Booster behandelingen gegeven.\"\n" +
                        "                ]\n" +
                        "            }\n" +
                        "        ]\n" +
                        "        \n" +
                        "    },\n" +
                        "    \"reisinformatie\": {\n" +
                        "        \"type\": \"section\",\n" +
                        "        \"titel\": \"Reisinformatie (dagelijks vertrek)\",\n" +
                        "        \"subsections\": [\n" +
                        "            {\n" +
                        "                \"type\": \"body\",\n" +
                        "                \"titel\": \"Per vliegtuig\",\n" +
                        "                \"subsections\": [\n" +
                        "                    \"U kan dagelijks in ca. 1,5 uur vliegen naar Zagreb (Kroatië) of Ljubljana (Slovenië) vanaf Schiphol (met Croatia Airlines, KLM of Adria Airways) of vanaf Brussel (met Croatia Airlines of Brussels Airlines).\",\n" +
                        "                    \"Op vliegveld Zagreb of Ljubljana wordt u opgehaald door een chauffeur van het kuuroord en in ongeveer een uur naar Šmarješke Toplice gebracht.\"\n" +
                        "                ]\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"type\": \"body\",\n" +
                        "                \"titel\": \"Per eigen vervoer\",\n" +
                        "                \"subsections\": [\n" +
                        "                    \"Šmarješke Toplice is prima per auto te bereiken. Parkeren op het parkeerterrein van het kuuroord is gratis.\"\n" +
                        "                ]\n" +
                        "            }\n" +
                        "        ]\n" +
                        "    },\n" +
                        "    \"ligging\": {\n" +
                        "        \"type\": \"section\",\n" +
                        "        \"titel\": \"\",\n" +
                        "        \"subsections\": [\n" +
                        "            {\n" +
                        "                \"type\": \"body\",\n" +
                        "                \"titel\": \"Idyllisch gelegen\",\n" +
                        "                \"subsections\": [\n" +
                        "                    \"Smarjeske Toplice is idyllisch gelegen in het dal van de rivier Krka en wordt omringd door golvende heuvels met bossen en wijngaarden. De mooie natuur nodigt uit tot het maken van wandelingen over één van de vele wandelpaden of rustige landweggetjes. Ook zijn er hier 9 speciaal aangelegde Nordic Walking paden.\"\n" +
                        "                ]\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"type\": \"body\",\n" +
                        "                \"titel\": \"Weldadige thermale bronnen\",\n" +
                        "                \"subsections\": [\n" +
                        "                    \"De hier ontspringende thermale bronnen (32˚C) genieten al sinds 1792 grote bekendheid. Het uit die tijd stammende, en in het aangrenzende bos gelegen, houten thermale buitenbad is nog steeds in gebruik en voor iedereen toegankelijk.\",\n" +
                        "                    \"Het thermale water is rijk aan calcium, magnesium en waterstofcarbonaat. Naast z’n ontspannende werking, wordt het gebruikt voor de behandeling van cardiovasculaire aandoeningen, bij sportrevalidatie en bij revalidatie van het bewegingsapparaat.\"\n" +
                        "                ]\n" +
                        "            }\n" +
                        "        ]\n" +
                        "    }\n" +
                        "}");

                reisModel = new ReisModel(jsonObject);

                SpannedString algemeenString = reisModel.getAlgemeen().getSpannedString(this);

                TextView algemeenText = findViewById(R.id.reis_beschrijving_text_algemeen);
                ImageView headerImage = findViewById(R.id.reis_beschrijving_header_image);

                algemeenText.setText(algemeenString);
                headerImage.setImageResource(R.drawable.image_1);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        //////////////////////////////////////////////////////////////////////////////////////////////


    }
}
