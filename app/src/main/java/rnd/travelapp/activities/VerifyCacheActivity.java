package rnd.travelapp.activities;

/**
 * Base for activities that use and verify the AppCache integrity. The start activity of the App
 * should perform the verification to ensure server side content changes are processed.
 */
public abstract class VerifyCacheActivity extends CacheActivity {

    @Override
    protected void onCacheInitialized() {
        appCache.verify().onCompletion(this::onCacheVerified);
    }

    /**
     * Invoked once the AppCache integrity has been verified.
     */
    protected void onCacheVerified() {

    }
}
