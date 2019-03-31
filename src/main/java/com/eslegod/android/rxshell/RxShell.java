package com.eslegod.android.rxshell;

import io.reactivex.*;

public class RxShell {

    public static Flowable<OutLine> flowableCommand(final String command) {
        return Flowable.create(new FlowableOnSubscribe<OutLine>() {
            @Override
            public void subscribe(FlowableEmitter<OutLine> emitter) throws Exception {
                Exec exec = new Exec();
                Process progress = exec.process(Exec.COMMAND_ROOT);
                if (progress == null) {
                    progress = exec.process(Exec.COMMAND_SH);
                }
                if (progress == null) {
                    emitter.onError(new Exception("progress broke"));
                    emitter.onComplete();
                    return;
                }
                exec.exec(command, progress, new OutShell<>(emitter));
            }
        }, BackpressureStrategy.BUFFER);

    }


    public static Observable<OutLine> observableCommand(final String command) {
        return Observable.create(new ObservableOnSubscribe<OutLine>() {
            @Override
            public void subscribe(ObservableEmitter<OutLine> emitter) throws Exception {
                Exec exec = new Exec();
                Process progress = exec.process(Exec.COMMAND_ROOT);
                if (progress == null) {
                    progress = exec.process(Exec.COMMAND_SH);
                }
                if (progress == null) {
                    emitter.onError(new Exception("progress broke"));
                    emitter.onComplete();
                    return;
                }
                exec.exec(command, progress, new OutShell<>(emitter));
            }
        });

    }
}
