package com.friendzrandroid.utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.rekognition.AmazonRekognitionClient;
import com.amazonaws.services.rekognition.model.CompareFacesRequest;
import com.amazonaws.services.rekognition.model.CompareFacesResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.util.IOUtils;
import com.friendzrandroid.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class FaceDetecting {

    private static final String TAG = "FaceDetecting";

    private final Activity mActivity;
    private final String mAccessKey;
    private final String mSecretKey;

    public FaceDetecting(Activity activity, String mAccessKey, String mSecretKey) {
        this.mActivity = activity;
        this.mAccessKey = mAccessKey;
        this.mSecretKey = mSecretKey;
    }

    public CompareFacesResult detect(String sourceImage, String targetImage) {

        Float similarityThreshold = 70F;
        //String sourceImage = "/storage/emulated/0/Android/data/com.friendzrandroid/files/DCIM/IMG_20220406_003504352.jpg";
        //String targetImage = "/storage/emulated/0/Android/data/com.friendzrandroid/files/DCIM/IMG_20220406_003504352.jpg";


        ByteBuffer sourceImageBytes = null;
        ByteBuffer targetImageBytes = null;

        AWSCredentials credentials = new BasicAWSCredentials(mAccessKey, mSecretKey);
        AmazonRekognitionClient rekognitionClient = new AmazonRekognitionClient(credentials);

        Log.e(TAG, "detect: Source: " + sourceImage + "Target: " + targetImage);


        //Load source and target images and create input parameters
        try (InputStream inputStream = new FileInputStream(new File(sourceImage))) {
            sourceImageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
        } catch (Exception e) {
            System.out.println("Failed to load source image " + sourceImage);
            System.exit(1);
        }
        try (InputStream inputStream = new FileInputStream(new File(targetImage))) {
            targetImageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
        } catch (Exception e) {
            System.out.println("Failed to load target images: " + targetImage);
            System.exit(1);
        }

        Image source = new Image()
                .withBytes(sourceImageBytes);
        Image target = new Image()
                .withBytes(targetImageBytes);

        CompareFacesRequest request = new CompareFacesRequest()
                .withSourceImage(source)
                .withTargetImage(target)
                .withSimilarityThreshold(similarityThreshold);


        // Call operation
        try {

            CompareFacesResult compareFacesResult = rekognitionClient.compareFaces(request);
            return compareFacesResult;

        } catch (Exception ex) {

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    View layout = mActivity.getLayoutInflater().inflate(R.layout.toast_layout, mActivity.findViewById(R.id.toastContainer));
                    TextView textView = layout.findViewById(R.id.toastMessageView);
                    textView.setText(ex.getMessage());

                    Toast toast = new Toast(mActivity);
                    toast.setView(layout);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.show();
                }
            });

            return null;
        }


//        // Display results
//        List<CompareFacesMatch> faceDetails = compareFacesResult.getFaceMatches();
//        for (CompareFacesMatch match : faceDetails) {
//            ComparedFace face = match.getFace();
//            BoundingBox position = face.getBoundingBox();
//            System.out.println("Face at " + position.getLeft().toString()
//                    + " " + position.getTop()
//                    + " matches with " + match.getSimilarity().toString()
//                    + "% confidence.");
//
//        }
//        List<ComparedFace> uncompared = compareFacesResult.getUnmatchedFaces();
//
//        System.out.println("There was " + uncompared.size()
//                + " face(s) that did not match");
    }


}
