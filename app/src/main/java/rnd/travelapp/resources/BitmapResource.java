package rnd.travelapp.resources;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

import rnd.travelapp.cache.AppCache;
import rnd.travelapp.serialization.BinaryLoadable;
import rnd.travelapp.serialization.BinaryLoader;

/**
 * Represents a Resource wrapper for a Bitmap.
 */
@BinaryLoadable(BitmapResource.Loader.class)
public class BitmapResource extends Resource<Bitmap> {

    public BitmapResource(String path) {
        this(path, null);
    }

    public BitmapResource(String path, Bitmap bitmap) {
        super(Bitmap.class, BitmapResource.class, path, bitmap);
    }

    @Override
    public int getSize() {
        return resource != null ? resource.getAllocationByteCount() : 0;
    }

    /**
     * Fill the given imageView with the Bitmap resource, fetching it from the cache if required.
     * If the fetching fails, the imageView is not filled.
     * @param context the context to get the AppCache from
     * @param imageView the ImageView to fill
     */
    public void getOrFetchToImageView(Context context, ImageView imageView) {
        getOrFetch(context, imageView::setImageBitmap, cause -> {
            Log.e("TRAVEL_APP", "Could not fetch image", cause);
        });
    }

    /**
     * Fill the given imageView with the Bitmap resource, fetching it from the cache if required.
     * If the fetching fails, the imageView is not filled.
     * @param cache the AppCache to fetch from
     * @param imageView the ImageView to fill
     */
    public void getOrFetchToImageView(AppCache cache, ImageView imageView) {
        getOrFetch(cache, imageView::setImageBitmap, cause -> {
            Log.e("TRAVEL_APP", "Could not fetch image", cause);
        });
    }

    public static class Loader implements BinaryLoader<Bitmap> {
        @Override
        public Bitmap deserialize(InputStream stream) throws IOException {
            Bitmap bitmap = BitmapFactory.decodeStream(stream);

            if(bitmap == null) {
                throw new IOException("Could not decode bitmap from stream");
            }

            return bitmap;
        }

        @Override
        public Resource<Bitmap> create(String path, Bitmap instance) {
            return new BitmapResource(path, instance);
        }

        @Override
        public Class<Bitmap> getType() {
            return Bitmap.class;
        }
    }
}
