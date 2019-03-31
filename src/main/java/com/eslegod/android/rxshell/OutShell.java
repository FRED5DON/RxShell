package com.eslegod.android.rxshell;

import io.reactivex.Emitter;

public class OutShell<T extends Emitter<OutLine>> implements Out, StreamType {

    private T emitter;

    public OutShell(T emitter) {
        this.emitter = emitter;
    }

    @Override
    public void normal(String line) {
        OutLine _temp = new OutLine();
        _temp.setType(NORMAL);
        _temp.setLine(line);
        emitter.onNext(_temp);
    }

    @Override
    public void error(String line) {
        OutLine _temp = new OutLine();
        _temp.setType(ERROR);
        _temp.setLine(line);
        emitter.onNext(_temp);
    }

    @Override
    public void terminal() {
        emitter.onComplete();
    }
}
