package org.innopolis.zeppelin_ruby;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;

/**
 * Created by bulat on 20.03.17.
 */
public class LocalRubyProcess {
    private static final Logger logger = LoggerFactory.getLogger(LocalRubyProcess.class);
    private static final String STATEMENT_END = "*!?flush reader!?*";

    InputStream stdin;
    OutputStream stdout;
    PrintWriter writer;
    BufferedReader reader;
    Process process;

    private long pid;

    public LocalRubyProcess(Process rubyProc) {
        this.process = rubyProc;
    }

    public void open() throws IOException {
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

    public String interpret(String cmd) throws IOException {
        writer.println(cmd);
        writer.println("\"" + STATEMENT_END + "\"");
        writer.flush();
        StringBuilder out = new StringBuilder();
        String line = null;
        boolean isFlush = false;
        while ( !isFlush && ((line = reader.readLine()) != null ) ) // changed order of conditions to avoid endless loop
        {
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
