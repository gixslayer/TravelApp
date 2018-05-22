package rnd.travelapp.models;

import org.json.JSONException;
import org.json.JSONObject;

import rnd.travelapp.serialization.JSONLoadable;
import rnd.travelapp.serialization.JSONLoader;

@JSONLoadable(TestModel.Loader.class)
public class TestModel {
    private final int intValue;
    private final double doubleValue;
    private final String stringValue;

    public TestModel(int intValue, double doubleValue, String stringValue) {
        this.intValue = intValue;
        this.doubleValue = doubleValue;
        this.stringValue = stringValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public double getDoubleValue() {
        return doubleValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    protected static class Loader implements JSONLoader<TestModel> {
        @Override
        public TestModel deserialize(JSONObject object) throws JSONException {
            int intValue = object.getInt("int");
            double doubleValue = object.getDouble("double");
            String stringValue = object.getString("string");

            return new TestModel(intValue, doubleValue, stringValue);
        }

        @Override
        public Class<TestModel> getType() {
            return TestModel.class;
        }
    }
}
