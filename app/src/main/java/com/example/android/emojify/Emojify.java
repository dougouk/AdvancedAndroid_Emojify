package com.example.android.emojify;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

/**
 * Created by Dan on 28/05/2017.
 */

public class Emojify {
    private static final String TAG = Emojify.class.getName();

    public static int detectFaces(Bitmap bitmap, Context context){
        FaceDetector faceDetector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();

        Frame frame = new Frame.Builder().setBitmap(bitmap).build();

        SparseArray<Face> faces = faceDetector.detect(frame);

        Log.d(TAG, faces.size() + " faces");
        if(faces.size() == 0){
            Toast.makeText(context, "No faces detected", Toast.LENGTH_SHORT).show();
        }
        return faces.size();
    }
}
