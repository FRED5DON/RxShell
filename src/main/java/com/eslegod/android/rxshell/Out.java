package com.eslegod.android.rxshell;

public interface Out {
    void normal(String line);

    void error(String line);

    void terminal();
}
