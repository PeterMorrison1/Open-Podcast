package com.the_canuck.openpodcast.media_player;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;

import com.the_canuck.openpodcast.activities.MainActivity;

public class NotificationHelper {

    public static NotificationCompat.Builder from (
            Context context, MediaSessionCompat mediaSessionCompat) {

        MediaControllerCompat controllerCompat = mediaSessionCompat.getController();
        MediaMetadataCompat mediaMetadataCompat = controllerCompat.getMetadata();
        MediaDescriptionCompat descriptionCompat = mediaMetadataCompat.getDescription();

        // Makes the episode title bold
        Spannable title = new SpannableString(descriptionCompat.getTitle());
        title.setSpan(new StyleSpan(Typeface.BOLD), 0, descriptionCompat.getTitle().length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // The intent to start/open the main activity when notification is clicked
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, "Media_Channel");
        builder
                .setContentTitle(title)
                .setContentText(descriptionCompat.getSubtitle())
                .setSubText(descriptionCompat.getDescription())
                .setLargeIcon(descriptionCompat.getIconBitmap())
//                .setContentIntent(controllerCompat.getSessionActivity())
                .setContentIntent(pendingIntent)
                .setChannelId("1")
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent
                        (context, PlaybackStateCompat.ACTION_STOP))
                .setVibrate(new long[]{0L})
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        return builder;
    }
}
