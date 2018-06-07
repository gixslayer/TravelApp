package rnd.travelapp.resources;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

import rnd.travelapp.serialization.BinaryLoadable;
import rnd.travelapp.serialization.BinaryLoader;

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

    public void getOrFetchToImageView(Context context, ImageView imageView) {
        getOrFetch(context, result -> result.consume(imageView::setImageBitmap, cause -> {
            Log.e("TRAVEL_APP", "Could not fetch image", cause);
        }));
    }

    protected static class Loader implements BinaryLoader<Bitmap> {
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
