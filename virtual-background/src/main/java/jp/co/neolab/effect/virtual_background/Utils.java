package jp.co.neolab.effect.virtual_background;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class Utils {
    private static final String TAG = "Util";

    public static MappedByteBuffer loadModelFile(Context context, String modelFile) {
        MappedByteBuffer retFile = null;
        try {
            AssetFileDescriptor fileDescriptor = context.getAssets().openFd(modelFile);
            FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
            FileChannel fileChannel = inputStream.getChannel();
            long startOffset = fileDescriptor.getStartOffset();
            long declareLength = fileDescriptor.getDeclaredLength();
            retFile = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declareLength);

            fileDescriptor.close();
        } catch (Exception e) {
            Log.e(TAG, "Can not load model", e);
        }

        return retFile;
    }

    public static Bitmap scale(Bitmap input, int w, int h) {
        return Bitmap.createScaledBitmap(input, w, h, false);
    }

    public static void bitmapToByteBuffer(Bitmap inputBitmap, ByteBuffer output) {
        int w = inputBitmap.getWidth();
        int h = inputBitmap.getHeight();

        output.rewind();

        int []pixels = new int[w * h];
        inputBitmap.getPixels(pixels, 0, w, 0, 0, w, h);

        int pixelIdx = 0;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int value = pixels[pixelIdx++];

                output.putFloat(((value >> 16) & 0xFF) / 255.0f);
                output.putFloat(((value >> 8) & 0xFF) / 255.0f);
                output.putFloat((value & 0xFF) / 255.0f);

                if (i == j  && i == 0) {
                    Log.d(TAG, "====" + (((value >> 16) & 0xFF) ) + " " + ((value >> 8) & 0xFF)  + " " + (value & 0xFF) );
                    Log.d(TAG, "====" + (((value >> 16) & 0xFF) / 255.0f) + " " + ((value >> 8) & 0xFF) / 255.0f + " " + (value & 0xFF) / 255.0f);
                }

            }
        }

        output.rewind();
    }
}
