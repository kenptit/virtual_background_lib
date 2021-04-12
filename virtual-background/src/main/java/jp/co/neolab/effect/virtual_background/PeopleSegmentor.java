package jp.co.neolab.effect.virtual_background;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.gpu.GpuDelegate;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PeopleSegmentor {
    private Interpreter interpreter;
    private Context context;
    private GpuDelegate gpuDelegate;

    private static final String MODEL_FILE = "segm_full_v679.tflite";
    private static final String TAG = "PeopleSegmentor";
    protected static final int IMG_W = 256;
    protected static final int IMG_H = 144;

    private ByteBuffer segmentationMasks;
    private ByteBuffer modelInput;

    public PeopleSegmentor(boolean useGPU) {
        context = EffectLib.context;
        interpreter = getInterpreter(MODEL_FILE, useGPU);
        segmentationMasks = ByteBuffer.allocateDirect(IMG_W * IMG_H * 2 * 4);
        segmentationMasks.order(ByteOrder.nativeOrder());

        modelInput = ByteBuffer.allocateDirect(IMG_H * IMG_W * 3 * 4);
        modelInput.order(ByteOrder.nativeOrder());
    }

    private Interpreter getInterpreter(String modelFile, boolean useGPU) {
        Interpreter.Options tfliteOptions = new Interpreter.Options();

        if (useGPU) {
            gpuDelegate = new GpuDelegate();
            tfliteOptions.addDelegate(gpuDelegate);
        }

        return new Interpreter(Utils.loadModelFile(context, modelFile), tfliteOptions);
    }

    public void close() {
        interpreter.close();

        if (gpuDelegate != null) {
            gpuDelegate.close();
        }
    }

    protected Bitmap segment(Bitmap input) {
        Bitmap scaledBitmap = Utils.scale(input, IMG_W, IMG_H);
        Utils.bitmapToByteBuffer(scaledBitmap, modelInput);

        segmentationMasks.rewind();
        interpreter.run(modelInput, segmentationMasks);

        Bitmap mask = convertModelOutputToMask(segmentationMasks);
        return Utils.scale(mask, input.getWidth(), input.getHeight());
    }

    private Bitmap convertModelOutputToMask(ByteBuffer segmentationMasks) {
        Bitmap maskBitmap = Bitmap.createBitmap(IMG_W, IMG_H, Bitmap.Config.ARGB_8888);
        int [] colors = {Color.TRANSPARENT, Color.WHITE};

        for (int i = 0; i < IMG_H; i++) {
            for (int j = 0; j < IMG_W; j++) {
                float maxVal = 0;
                int maxIdx = 0;

                for (int k = 0; k < 2; k++) {
                    float value = segmentationMasks.getFloat(((i * IMG_W + j) * 2 + k) * 4);

                    if (value > maxVal) {
                        maxIdx = k;
                        maxVal = value;
                    }
                }

                maskBitmap.setPixel(j, i, colors[maxIdx]);
            }
        }


        return maskBitmap;
    }
}
