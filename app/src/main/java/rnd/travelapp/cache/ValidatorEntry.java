package rnd.travelapp.cache;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a Validator response mutation that must be performed to sync the local cache.
 */
public class ValidatorEntry {
    private final String file;
    private final String checksum;
    private final Status status;

    private ValidatorEntry(String file, String checksum, Status status) {
        this.file = file;
        this.checksum = checksum;
        this.status = status;
    }

    public String getFile() {
        return file;
    }

    public String getChecksum() {
        return checksum;
    }

    public Status getStatus() {
        return status;
    }

    public static ValidatorEntry fromJSON(JSONObject object) throws JSONException {
        String file = object.getString("file");
        String checksum = object.getString("checksum");
        Status status = Status.fromString(object.getString("status"));

        return new ValidatorEntry(file, checksum, status);
    }

    /**
     * The mutation type.
     */
    public enum Status {
        /**
         * There is a new entry that should be fetched.
         */
        New,
        /**
         * There is an existing entry that has been modified and should be fetched again.
         */
        Modified,
        /**
         * There is an entry that was removed and should be removed.
         */
        Removed;

        protected static Status fromString(String value) {
            if(value.equals("new")) {
                return New;
            } else if(value.equals("modified")) {
                return Modified;
            } else if(value.equals("removed")) {
                return Removed;
            }

            throw new IllegalArgumentException("Unknown value");
        }
    }
}
