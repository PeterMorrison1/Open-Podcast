package com.the_canuck.openpodcast.dialogs;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.the_canuck.openpodcast.Episode;
import com.the_canuck.openpodcast.R;

import java.text.DecimalFormat;

public class EpisodeDialog extends DialogFragment{

    public static final String EPISODE = "episodeKey";

    public static EpisodeDialog newInstance(Episode episode) {
        EpisodeDialog frag = new EpisodeDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EPISODE, episode);
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Episode episode = (Episode) getArguments().getSerializable(EPISODE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.episode_dialog, null);

        TextView title = view.findViewById(R.id.dialog_title);
        TextView description = view.findViewById(R.id.dialog_description);
        TextView size = view.findViewById(R.id.dialog_size);
        TextView duration = view.findViewById(R.id.dialog_duration);

//        int timeLeft = Integer.valueOf(episode.getLength());
//        String timeLeft = String.valueOf((Integer.valueOf(episode.getLength()) / (1000 * 60)) % 600);

        String time = "Duration: " + episode.getDuration();
        String prettyDescription = android.text.Html.fromHtml(episode.getDescription()).toString();

        title.setText(episode.getTitle());
        title.setTextSize(16);
        title.setPadding(16, 16,16, 16);
        description.setText(prettyDescription);
        description.setPadding(16,16,16,16);
        size.setText(getFileSize(Integer.valueOf(episode.getLength())));
        duration.setText(time);

        builder.setView(view);
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
