package com.the_canuck.openpodcast.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.the_canuck.openpodcast.Episode;
import com.the_canuck.openpodcast.R;
import com.the_canuck.openpodcast.media_store.MediaStoreHelper;
import com.the_canuck.openpodcast.sqlite.MySQLiteHelper;

import java.text.DecimalFormat;
import java.util.HashMap;

public class EpisodeDialog extends DialogFragment{

    EpisodeDialogListener mListener;

    public static EpisodeDialog newInstance(Episode episode) {
        EpisodeDialog frag = new EpisodeDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Episode.EPISODE, episode);
        frag.setArguments(bundle);
        return frag;
    }

    public interface EpisodeDialogListener {
        // TODO: If i add playlists, make "Add playlist" button here!
        void onDialogDeleteClick();
        void onDialogCloseClick();
    }

    public EpisodeDialog setmListener(EpisodeDialogListener mListener) {
        this.mListener = mListener;
        return this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Episode episode = (Episode) getArguments().getSerializable(Episode.EPISODE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.episode_dialog, null);

        TextView title = view.findViewById(R.id.dialog_title);
        TextView description = view.findViewById(R.id.dialog_description);
        TextView size = view.findViewById(R.id.dialog_size);
        TextView duration = view.findViewById(R.id.dialog_duration);

        // Parses the description HTML (so no html tags are in but are still used)
        Spanned descriptionParsed;
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            descriptionParsed = Html.fromHtml(episode.getDescription(), Html.FROM_HTML_MODE_LEGACY);
        } else {
            descriptionParsed = Html.fromHtml(episode.getDescription());
        }

        title.setText(episode.getTitle());
        title.setTextSize(16);
        title.setPadding(16, 16,16, 16);

        // sets description w/ hyperlinks
        description.setText(descriptionParsed);
        description.setMovementMethod(LinkMovementMethod.getInstance());
        description.setPadding(16,16,16,16);

        // sets the episode size and duration if they exist, if not query mediastore for it
        if (episode.getLength() != null) {
            size.setText(getFileSize(Integer.valueOf(episode.getLength())));
        }
        if (episode.getDuration() != null) {
            String time = "Duration: " + episode.getDuration();
            duration.setText(time);
        }

        // sets the view and the buttons for the dialog
        builder.setView(view)
                .setPositiveButton(R.string.delete_episode, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // delete button
                        if (mListener != null) {
                            mListener.onDialogDeleteClick();
                        }
                    }
                })
                .setNegativeButton(R.string.close_dialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // close dialog button
                        if (mListener != null) {
                            mListener.onDialogCloseClick();
                        }
                    }
                });
        return builder.create();
    }

    /**
     * Converts the file size into a more human readable format (KB, MB, etc).
     *
     * @param size file size of podcast
     * @return string of the converted file size
     */
    public String getFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[] { "B", "KB", "MB", "GB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#")
                .format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
