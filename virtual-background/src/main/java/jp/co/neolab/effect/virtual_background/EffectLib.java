package jp.co.neolab.effect.virtual_background;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.Log;

import at.favre.lib.dali.Dali;

public class EffectLib {
    public static Context context;

    public static void init(Context _context) {
        context = _context;
    }

    private PeopleSegmentor peopleSegmentor;

    public EffectLib() {
        peopleSegmentor = new PeopleSegmentor(true);
    }

    public Bitmap segment(Bitmap input) {
        return  peopleSegmentor.segment(input);
    }

    public Bitmap blurBackground(Bitmap input, int radius) {
        Bitmap scaledBitmap = Utils.scale(input, PeopleSegmentor.IMG_W, PeopleSegmentor.IMG_H);

        int w = input.getWidth();
        int h = input.getHeight();

        Bitmap mask = peopleSegmentor.segment(scaledBitmap);
        mask = Utils.scale(mask, w, h);
        Bitmap blury = Dali.create(context).load(scaledBitmap).blurRadius(radius).get().getBitmap();
        blury = Utils.scale(blury, w, h);

//        return blury;
        return stackBitmapsWithMask(input, blury, mask);
    }

    public  Bitmap stackBitmapsWithMask(Bitmap foreground, Bitmap background, Bitmap mask) {
        int w = foreground.getWidth();
        int h = foreground.getHeight();


        Bitmap tempBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas tempCanvas = new Canvas(tempBitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        tempCanvas.drawBitmap(foreground, 0, 0, null);
        tempCanvas.drawBitmap(mask, 0, 0, paint);

        Bitmap output = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas resultCanvas = new Canvas(output);
        resultCanvas.drawBitmap(background, 0, 0, null);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        resultCanvas.drawBitmap(tempBitmap, 0, 0, paint);


        return output;
    }

}
