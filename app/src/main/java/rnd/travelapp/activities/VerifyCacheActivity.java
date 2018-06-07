package rnd.travelapp.activities;


public abstract class VerifyCacheActivity extends CacheActivity {

    @Override
    protected void onCacheInitialized() {
        appCache.verify().onCompletion(this::onCacheVerified);
    }

    protected void onCacheVerified() {

    }
}
