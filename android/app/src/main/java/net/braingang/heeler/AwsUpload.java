package net.braingang.heeler;

import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;

public class AwsUpload {
    public static final String LOG_TAG = AwsUpload.class.getName();

    public void writer(Context context, File[] files) {
        AWSCredentials credentials = new BasicAWSCredentials(Personality.AwsAccessKey, Personality.AwsSecretKey);
        AmazonS3 s3 = new AmazonS3Client(credentials);

        TransferUtility transferUtility = new TransferUtility(s3, context);

        for (File file:files) {
            String key = file.getName();

            TransferObserver observer = transferUtility.upload(Personality.AwsBucketName, key, file);
            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (state == TransferState.COMPLETED) {
                        Log.i(LOG_TAG,  "completed:" + observer.getKey());

                        // delete uploaded file
                        for (File target:files) {
                            if (observer.getKey().equals(target.getName())) {
                                Log.i(LOG_TAG, "deleted:" + target.getName());
                                target.delete();
                            }
                        }
                    } else {
                        Log.i(LOG_TAG, "onStateChanged:" + id + ":" + state);
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    Log.i(LOG_TAG, "onProgressChanged:" + id + ":" + bytesCurrent + ":" + bytesTotal);
                }

                @Override
                public void onError(int id, Exception exception) {
                    Log.e(LOG_TAG, "onError:" + id + ":" + exception);
                }
            });
        }
    }
}
