package com.example.android.emojify;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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

    private static int EMOJI_SCALE_FACTOR = 1;

    public static Bitmap detectFacesAndOverlayEmoji(Bitmap bitmap, Context context){
        FaceDetector faceDetector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        Frame frame = new Frame.Builder().setBitmap(bitmap).build();

        SparseArray<Face> faces = faceDetector.detect(frame);

        Bitmap resultBitmap = bitmap;

        Log.d(TAG, faces.size() + " faces");
        if(faces.size() == 0){
            Toast.makeText(context, "No faces detected", Toast.LENGTH_SHORT).show();
        }else{
            for(int i = 0; i < faces.size(); i++){
                Face face = faces.get(i);
                if(face == null){
                    Log.w(TAG, "face is null");
                }
                Bitmap emojiBitmap = null;

                switch(whichEmoji(face)){
                    case OOS:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.smile);
                        break;
                    case OCS:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.rightwink);
                        break;
                    case COS:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.leftwink);
                        break;
                    case CCS:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.closed_smile);
                        break;
                    case OOF:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.frown);
                        break;
                    case OCF:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.rightwinkfrown);
                        break;
                    case COF:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.leftwinkfrown);
                        break;
                    case CCF:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.closed_frown);
                        break;
                    default: break;
                }
                resultBitmap = addBitmapToFace(bitmap, emojiBitmap, face);
            }
        }

        faceDetector.release();
        return resultBitmap;
    }

    public static Emoji whichEmoji(Face face){
        Log.d(TAG, "left eye open : " + Float.toString(face.getIsLeftEyeOpenProbability()));
        Log.d(TAG, "right eye open : " + Float.toString(face.getIsRightEyeOpenProbability()));
        Log.d(TAG, "smiling : " + Float.toString(face.getIsSmilingProbability()));
        double leftEye = face.getIsLeftEyeOpenProbability();
        double rightEye = face.getIsRightEyeOpenProbability();
        double smiling = face.getIsSmilingProbability();

        double thd = 0.5; //threshold

        Emoji emoji = null;

        if(smiling > thd){
            if(leftEye > thd && rightEye > thd )
                emoji = Emoji.OOS;
            if(leftEye > thd && rightEye < thd )
                emoji = Emoji.OCS;
            if(leftEye < thd && rightEye > thd )
                emoji = Emoji.COS;
            if(leftEye < thd && rightEye < thd )
                emoji = Emoji.CCS;
        }
        else{
            if(leftEye > thd && rightEye > thd )
                emoji = Emoji.OOF;
            if(leftEye > thd && rightEye < thd )
                emoji = Emoji.OCF;
            if(leftEye < thd && rightEye > thd )
                emoji = Emoji.COF;
            if(leftEye < thd && rightEye < thd )
                emoji = Emoji.CCF;
        }
        Log.d(TAG, String.valueOf(emoji));
        return emoji;
    }

    /**
     * Combines the original picture with the emoji bitmaps
     *
     * @param backgroundBitmap The original picture
     * @param emojiBitmap      The chosen emoji
     * @param face             The detected face
     * @return The final bitmap, including the emojis over the faces
     */
    private static Bitmap addBitmapToFace(Bitmap backgroundBitmap, Bitmap emojiBitmap, Face face) {

        // Initialize the results bitmap to be a mutable copy of the original image
        Bitmap resultBitmap = Bitmap.createBitmap(backgroundBitmap.getWidth(),
                backgroundBitmap.getHeight(), backgroundBitmap.getConfig());

        // Scale the emoji so it looks better on the face
        float scaleFactor = EMOJI_SCALE_FACTOR;

        // Determine the size of the emoji to match the width of the face and preserve aspect ratio
        int newEmojiWidth = (int) (face.getWidth() * scaleFactor);
        int newEmojiHeight = (int) (emojiBitmap.getHeight() *
                newEmojiWidth / emojiBitmap.getWidth() * scaleFactor);


        // Scale the emoji
        emojiBitmap = Bitmap.createScaledBitmap(emojiBitmap, newEmojiWidth, newEmojiHeight, false);

        // Determine the emoji position so it best lines up with the face
        float emojiPositionX =
                (face.getPosition().x + face.getWidth() / 2) - emojiBitmap.getWidth() / 2;
        float emojiPositionY =
                (face.getPosition().y + face.getHeight() / 2) - emojiBitmap.getHeight() / 3;

        // Create the canvas and draw the bitmaps to it
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(backgroundBitmap, 0, 0, null);
        canvas.drawBitmap(emojiBitmap, emojiPositionX, emojiPositionY, null);

        return resultBitmap;
    }
}
