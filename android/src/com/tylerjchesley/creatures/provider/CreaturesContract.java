package com.tylerjchesley.creatures.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Author: Tyler Chesley
 */
public class CreaturesContract {

    public static final String CONTENT_AUTHORITY = "com.tylerjchesley.creatures";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static interface CreaturesColumns {

        public static final String CREATURE_ID = "creature_id";

        public static final String TITLE = "title";

        public static final String URL = "url";

        public static final String IMAGE = "image";

        public static final String IS_FAVORITE = "is_favorite";

        public static final String IS_NEW = "is_new";

        public static final String CREATED_AT = "created_at";

    }

    public static final class Creatures implements CreaturesColumns, BaseColumns {

        public static final String PATH = "creatures";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.creatures.creature";

        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.creatures.creature";

        public static Uri buildCreatureUri(long creatureId) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(creatureId)).build();
        }

        public static String getCreatureId(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

}
