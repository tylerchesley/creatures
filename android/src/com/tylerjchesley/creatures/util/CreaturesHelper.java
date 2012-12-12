package com.tylerjchesley.creatures.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.*;
import android.os.Bundle;
import android.support.v4.app.*;
import android.widget.Toast;
import com.tylerjchesley.creatures.R;
import com.tylerjchesley.creatures.model.Creature;
import com.tylerjchesley.creatures.provider.CreaturesContract.Creatures;

/**
 * Author: Tyler Chesley
 */
public class CreaturesHelper {

    private static final String DIALOG_DELETE_CREATURE = "dialog_delete_creature";

//------------------------------------------
//  Static Methods
//------------------------------------------

    public static void maybeDeleteCreature(FragmentActivity activity, long creatureId) {
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag(DIALOG_DELETE_CREATURE);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        final DeleteCreatureConfirmDialog dialog = new DeleteCreatureConfirmDialog();
        final Bundle args = new Bundle();
        args.putLong(Creature._ID, creatureId);
        dialog.setArguments(args);

        dialog.show(ft, DIALOG_DELETE_CREATURE);
    }

    public static void deleteCreature(final Activity activity, long creatureId) {
        final AsyncQueryHandler handler = new AsyncQueryHandler(activity.getContentResolver()) {
            @Override
            protected void onDeleteComplete(int token, Object cookie, int result) {
                Toast.makeText(activity, R.string.toast_deleted, Toast.LENGTH_SHORT).show();
                activity.finish();
            }
        };
        handler.startDelete(0, null, Creatures.CONTENT_URI, Creatures._ID + " = ?",
                new String[] {String.valueOf(creatureId)});
    }

    public static Intent createShareIntent(Activity activity, String title, String url) {
        ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(activity)
                .setType("text/plain")
                .setSubject(title)
                .setText(url);
        return builder.getIntent();
    }

    public static void shareCreature(Activity activity, String title, String url) {
        activity.startActivity(Intent.createChooser(
                createShareIntent(activity, title, url),
                activity.getString(R.string.title_share)));
    }

    public static void setCreatureFavorited(Context context, long creatureId, boolean favorited) {
        final ContentValues values = new ContentValues();
        values.put(Creature.IS_FAVORITE, favorited);
        final AsyncQueryHandler handler = new AsyncQueryHandler(context.getContentResolver()) {};
        handler.startUpdate(0, null, Creatures.buildCreatureUri(creatureId), values, null, null);
    }

    public static void setCreatureIsNew(Context context, long creatureId, boolean isNew) {
        final ContentValues values = new ContentValues();
        values.put(Creature.IS_NEW, isNew);
        final AsyncQueryHandler handler = new AsyncQueryHandler(context.getContentResolver()) {};
        handler.startUpdate(0, null, Creatures.buildCreatureUri(creatureId), values, null, null);
    }

//------------------------------------------
//  Inner Classes
//------------------------------------------

    public static class DeleteCreatureConfirmDialog extends DialogFragment  {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.title_delete_creature)
                    .setMessage(R.string.message_delete_creature)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            onOkSelected();
                        }
                    }).create();
        }

        private void onOkSelected() {
            deleteCreature(getActivity(), getArguments().getLong(Creature._ID));
        }

    }

}
