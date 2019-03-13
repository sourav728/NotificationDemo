package com.tvd.notificationdemo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    Button click;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        click = findViewById(R.id.btn_notification);
        click.setOnClickListener(view -> sendNotification());
    }

    private void sendNotification() {
        int uniqueId = (int) System.currentTimeMillis();
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager != null) {
            String channelID = "Your Channel ID";// The id of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelID, "My_Name", importance);

            FutureTarget<Bitmap> futureTarget = Glide.with(this).asBitmap()
                    .load("https://imgur.com/17UDaLS.png").submit();

            LoadImageTask task = new LoadImageTask(icon -> {
                NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle()
                        .bigPicture(icon);

                Notification notification = getNotificationBuilder("Notification By Sourav", icon, style, defaultSoundUri)
                        .setChannelId(channelID)
                        .build();
                notificationManager.createNotificationChannel(mChannel);
                notificationManager.notify(uniqueId, notification);
            });
            task.execute(futureTarget);


        } else if (notificationManager != null) {
            FutureTarget<Bitmap> futureTarget = Glide.with(this).asBitmap()
                    .load("https://imgur.com/17UDaLS.png").submit();
            LoadImageTask task = new LoadImageTask(icon -> {
                NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle()
                        .bigPicture(icon);
                NotificationCompat.Builder notificationBuilder = getNotificationBuilder("Notification By Sourav", icon, style, defaultSoundUri);
                notificationManager.notify(uniqueId,
                        notificationBuilder.build());
            });
            task.execute(futureTarget);

        }
    }

    private NotificationCompat.Builder getNotificationBuilder(String demo, Bitmap bitmap, NotificationCompat.BigPictureStyle style, Uri defaultSoundUri) {
        return new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.app_icon2)
                .setContentTitle(demo)
                .setContentText("welcome here")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Big Text"))
                .setStyle(style)
                .setSound(defaultSoundUri)
                .setAutoCancel(true);
    }

    public static class LoadImageTask extends AsyncTask<FutureTarget<Bitmap>, Void, Bitmap> {
        private OnSuccess onSuccess;

        interface OnSuccess {
            void onSuccess(Bitmap bitmap);
        }

        LoadImageTask(OnSuccess onSuccess) {
            this.onSuccess = onSuccess;
        }

        @SafeVarargs
        @Override
        protected final Bitmap doInBackground(FutureTarget<Bitmap>... futureTargets) {
            try {
                return futureTargets[0].get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null)
                onSuccess.onSuccess(bitmap);
        }
    }

}
