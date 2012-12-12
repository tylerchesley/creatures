package com.tylerjchesley.creatures.util;

import android.support.v4.app.FragmentActivity;
import xxx.tylerchesley.android.util.ImageCache;
import xxx.tylerchesley.android.util.ImageFetcher;

/**
 * Author: Tyler Chesley
 */
public class UiUtils {

    private static final String IMAGE_FETCHER = "creatures";

    private static final int MEM_CACHE_SIZE = 1024 * 1024 * 4;
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 100;

    public static ImageFetcher getImageFetcher(final FragmentActivity activity) {
        // The ImageFetcher takes care of loading remote images into our ImageView
        final ImageFetcher fetcher = new ImageFetcher(activity);
        final ImageCache.ImageCacheParams params = new ImageCache.ImageCacheParams(IMAGE_FETCHER);
        params.memCacheSize = MEM_CACHE_SIZE;
        params.diskCacheSize = DISK_CACHE_SIZE;
        fetcher.setImageCache(ImageCache.findOrCreateCache(activity, params));
        return fetcher;
    }

}
