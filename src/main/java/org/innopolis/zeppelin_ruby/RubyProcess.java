package org.innopolis.zeppelin_ruby;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;

/**
 * Created by bulat on 20.03.17.
 */
public class RubyProcess {
    private static final Logger logger = LoggerFactory.getLogger(RubyProcess.class);
    private static final String STATEMENT_END = "*!?flush reader!?*";

    InputStream stdin;
    OutputStream stdout;
    PrintWriter writer;
    BufferedReader reader;
    Process process;

    private String rubyPath;
    private long pid;

    public RubyProcess(String rubyPath) {
        this.rubyPath = rubyPath;
    }

    public void open() throws IOException {
        //String cmd = rubyPath + " --noreadline";
        //ProcessBuilder builder = new ProcessBuilder("bash", "-c", cmd);
        //builder.redirectErrorStream(true);
        process = Runtime.getRuntime().exec("bash -c irb --noinspect");
        //process = builder.start();
        stdin = process.getInputStream();
        stdout = process.getOutputStream();
        writer = new PrintWriter(stdout, true);
        reader = new BufferedReader(new InputStreamReader(stdin));
        try {
            Field f = process.getClass().getDeclaredField("pid");
            f.setAccessible(true);
            pid = f.getLong(process);
        } catch (Exception ex) {
            throw new IllegalArgumentException("OS is not supported");
        }
    }

    public void close() throws IOException {
        process.destroy();
        reader.close();
        writer.close();
        stdin.close();
        stdout.close();
    }

    public void interrupt() throws IOException {
        logger.info("Interrupting interpreter with pid " + pid);
        Runtime.getRuntime().exec("kill -SIGINT " + pid);
    }

    String interpret(String cmd) throws IOException {
        writer.println(cmd);
        writer.println("\"" + STATEMENT_END + "\"");
        writer.flush();
        StringBuilder out = new StringBuilder();
        String line = null;
        boolean isFlush = false;
        while((line = reader.readLine()) != null && !isFlush) {
            if (line.contains(STATEMENT_END)) {
                isFlush = true;
            } else {
                logger.info("Result from IRB: " + line);
                out.append(line + "\n");
            }
        }
        return out.toString();
    }

    public long getPid(){
        return pid;
    }
}
