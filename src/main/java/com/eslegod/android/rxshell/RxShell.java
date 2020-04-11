package com.eslegod.android.rxshell;

import io.reactivex.*;

public class RxShell {


    public static boolean isRooted() {
        Exec exec = new Exec();
        Process progress = exec.process(Exec.COMMAND_ROOT);
        return progress != null;
    }

    public static Flowable<OutLine> flowableCommand(final String command) {
        return Flowable.create(emitter -> {
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
        }, BackpressureStrategy.BUFFER);

    }


    public static Observable<OutLine> observableCommand(final String command) {
        return Observable.create(emitter -> {
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
        });

    }
}
