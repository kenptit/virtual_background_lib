package jp.co.neolab.effect.virtualbackground;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

import jp.co.neolab.effect.virtual_background.EffectLib;
import jp.co.neolab.effect.virtual_background.PeopleSegmentor;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private ImageView imageView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EffectLib.init(getApplicationContext());
        PeopleSegmentor peopleSegmentor = new PeopleSegmentor(true);

        imageView = findViewById(R.id.imageView);
        imageView2 = findViewById(R.id.imageView2);

        Bitmap input = getBitmapFromAssets(getApplicationContext(),"boy.jpg");
        imageView.setImageBitmap(input);

        imageView2.setImageBitmap(peopleSegmentor.segment(input));

    }

    private Bitmap getBitmapFromAssets(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            // handle exception
        }

        return bitmap;
    }
}