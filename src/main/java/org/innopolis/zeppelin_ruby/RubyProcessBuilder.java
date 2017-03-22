package org.innopolis.zeppelin_ruby;

import java.io.IOException;

/**
 * Created by bulat on 22.03.17.
 */
public class RubyProcessBuilder {
    private ProcessBuilder builder = new ProcessBuilder();

    private String bash = "bash";
    private String bashArgs = "-c";
    private String rubyPath = "irb";
    private String rubyArgs = "--noinspect";

    public RubyProcessBuilder() {
    }

    public RubyProcessBuilder setBash(String path) {
        bash = path;
        return this;
    }

    public RubyProcessBuilder setBashArgs(String args) {
        bashArgs = args;
        return this;
    }

    public RubyProcessBuilder setRubyPath(String path) {
        rubyPath = path;
        return this;
    }

    public RubyProcessBuilder setRubyArgs(String args) {
        rubyArgs = args;
        return this;
    }

    public LocalRubyProcess start() throws IOException {
        ProcessBuilder builder = new ProcessBuilder(bash, bashArgs, rubyPath, rubyArgs);
        builder.redirectErrorStream(true);
        LocalRubyProcess process = new LocalRubyProcess(builder.start());
        return process;
    }
}
