package com.eslegod.android.rxshell;

public class OutLine  implements StreamType {

    public static final int UNSPECIFIC = 0x20;

    private int type;

    public String line;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }
}
