package com.eslegod.android.rxshell;

import android.system.ErrnoException;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Exec {

    public static final String COMMAND_SH = "sh";
    public static final String COMMAND_LINE_END = "\n";
    public static final String COMMAND_EXIT = "exit\n";
    public static final String COMMAND_ROOT = "su";

    private static Process lastProcess;

    private static ExecutorService pool;


    public Exec() {
        if (pool != null) {
            pool.shutdownNow();
            pool = null;
        }
        pool = Executors.newCachedThreadPool();
    }

    public Process process(String command) {
        Runtime rt = Runtime.getRuntime();
        Process p = null;
        try {
            p = rt.exec(command);
        } catch (IOException e) {
            e.printStackTrace();
            p = null;
        }
        return p;
    }

    public static void terminate() {
        if (lastProcess != null) {
            String s = lastProcess.toString();
            String pid = s.replaceAll("[^\\d]*(\\d+).*", "$1");
            int pidInt = Integer.parseInt(pid);
            android.os.Process.killProcess(pidInt);
            try (InputStream in = lastProcess.getInputStream();
                 InputStream er = lastProcess.getErrorStream();
                 OutputStream out = lastProcess.getOutputStream()) {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                if (er != null) {
                    er.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            lastProcess.destroy();
            lastProcess = null;
        }
        if (pool != null && !pool.isTerminated()) {
            pool.shutdown();
            pool = null;
        }
    }

    public void exec(String command, Process process, Out out) {
        if (process == null) return;
        InputStream err = null, normal = null;
        DataOutputStream dos = null;
        try {
            lastProcess = process;
            err = process.getErrorStream();
            normal = process.getInputStream();
            dos = new DataOutputStream(process.getOutputStream());
            dos.write(command.getBytes());
            dos.writeBytes(COMMAND_LINE_END);
            dos.flush();
            dos.writeBytes(COMMAND_EXIT);
            dos.flush();
            dos.close();
//            List<Future<OutLine>> futures = new ArrayList<>();
            pool.submit(new StreamReaderWorker(normal, StreamType.NORMAL, out));
            pool.submit(new StreamReaderWorker(err, StreamType.ERROR, out));
//            new StreamReaderWorker(err, StreamType.ERROR, out).run();
//            new StreamReaderWorker(normal, StreamType.NORMAL, out).run();
            pool.shutdown();
            int ins = process.waitFor();
            while (true) {
                if (pool.isTerminated()) {
                    if (out != null) {
                        out.terminal();
                    }
                    break;
                }
                Thread.sleep(200);
            }
            process.destroy();
            lastProcess = null;
//            System.out.println(ins);
//            if (out != null) out.terminal();
//            new StreamReaderWorker(normal, StreamType.NORMAL, out).start();
//            for (Future<OutLine> future : futures) {
//                if (out != null) {
//                    OutLine mo = future.get();
//                    if (mo == null || mo.getLine() == null) {
//                        continue;
//                    }
//                    if (mo.getType() == StreamType.NORMAL)
//                        out.normal(mo.getLine());
//                    else out.error(mo.getLine());
//                }
//            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
//            try {
//                if (err != null) err.close();
//                if (normal != null) normal.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

        }
    }


    public String syncExec(String command) {
        Process progress = process(Exec.COMMAND_ROOT);
        if (progress == null) {
            progress = process(Exec.COMMAND_SH);
        }
        if (progress != null) {
            try (InputStream is = progress.getInputStream();
                 InputStream err = progress.getErrorStream();
                 DataOutputStream dos = new DataOutputStream(progress.getOutputStream())) {
                dos.write(command.getBytes());
                dos.writeBytes(COMMAND_LINE_END);
                dos.flush();
                dos.writeBytes(COMMAND_EXIT);
                dos.flush();
                dos.close();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                StringBuffer stringBuffer = new StringBuffer();
                while ((line = br.readLine()) != null) {
                    stringBuffer.append(line);
                    stringBuffer.append("\n");
                }
                br.close();
                isr.close();
                return stringBuffer.toString();
            } catch (IOException e) {
                throw new RuntimeException();
            } finally {
                progress.destroy();
            }
        }
        return null;
    }

    public interface StdoutCallback {
        void out(String line);

        void err(String message);

        void complete(String message);
    }
}
