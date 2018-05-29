package rnd.travelapp.models;

import rnd.travelapp.R;

public enum HeaderStyles {
    H1(R.style.H1), H2(R.style.H2), H3(R.style.H3), H4(R.style.H4), H5(R.style.H5), H6(R.style.H6);

    private final int style;

    HeaderStyles (int style) {
        this.style = style;
    }

    public int getStyle() {
        return style;
    }

    private static HeaderStyles[] list = HeaderStyles.values();

    public static int get (int i) {
        return list[Math.min(i-1, 5)].getStyle();
    }
}
