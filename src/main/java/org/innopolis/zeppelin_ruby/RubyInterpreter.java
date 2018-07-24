package org.innopolis.zeppelin_ruby;

import org.apache.zeppelin.interpreter.Interpreter;

import org.apache.zeppelin.interpreter.InterpreterContext;
import org.apache.zeppelin.interpreter.InterpreterResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by bulat on 19.03.17.
 */
public class RubyInterpreter extends Interpreter {
    private static Logger logger = LoggerFactory.getLogger(RubyInterpreter.class);
    private static LocalRubyProcess process;

    private static Pattern error = Pattern.compile(".*(Error|Exception):.*$");

    public RubyInterpreter(Properties property) {
        super(property);
    }

    public void open() {
        logger.info("Ruby interpreter open.");
        logger.info("Ruby path: " + property.getProperty("zeppelin.ruby"));
        if (process == null) {
            RubyProcessBuilder builder = new RubyProcessBuilder();
            builder
                    .setRubyPath(property.getProperty("zeppelin.ruby"));  // .setRubyArgs("--noinspect") doesn't work, add it directly on command line (zeppelin.ruby)
            try {
                process = builder.start();
                process.open();
                logger.info("irb PID: " + process.getPid());
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("Cant create ruby interpreter");
            }
        }
    }

    public void close() {
        logger.info("Opened");
        if (process != null) {
            try {
                process.close();
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("Cant close irb");
            }
            process = null;
        }
    }

    public InterpreterResult interpret(String s, InterpreterContext interpreterContext) {
        if (s == null | s.isEmpty()) {
            return new InterpreterResult(InterpreterResult.Code.SUCCESS, "");
        }
        try {
            logger.info("--- interperet - in: <<<\n" + s);
            
            String out = process.interpret(s);
            
            if (property.getProperty("zeppelin.ruby.noecho").equals("true"))
            {
                // remove s from out
                logger.info("+++ interperet - out: \n" + out);
                logger.info("'zeppelin.ruby.noecho' = true --> remove input ...");

                String lines[] = s.split("\\r?\\n");
                
                for (int i = 0; i < lines.length; ++i)
                {
                    logger.info ("***" + lines[i] + "***");
                    out = out.replaceFirst(Pattern.quote(lines[i]), "");
                }

            }

            logger.info("+++ interperet - out: >>>\n" + out);   // rps
            
            if(isSuccessful(out)) {
                return new InterpreterResult(InterpreterResult.Code.SUCCESS, out);
            } else {
                return new InterpreterResult(InterpreterResult.Code.ERROR, out);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new InterpreterResult(InterpreterResult.Code.ERROR, "");
        }
    }

    public void cancel(InterpreterContext interpreterContext) {
        logger.info("Canceled");
    }

    public FormType getFormType() {
        logger.info("getFormType");
        return FormType.SIMPLE;
    }

    public int getProgress(InterpreterContext interpreterContext) {
        logger.info("getProgress");
        return 0;
    }

    private boolean isSuccessful(String result) {
        String[] lines = result.split("\n");
        Matcher matcher;
        for(String line: lines) {
            matcher = error.matcher(line);
            if(matcher.find()) {
                return false;
            }
        }
        return true;
     }
}
