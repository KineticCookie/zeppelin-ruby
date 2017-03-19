package org.innopolis.zeppelin_ruby;

import org.apache.zeppelin.interpreter.Interpreter;

import org.apache.zeppelin.interpreter.Interpreter;
import org.apache.zeppelin.interpreter.InterpreterContext;
import org.apache.zeppelin.interpreter.InterpreterResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;


/**
 * Created by bulat on 19.03.17.
 */
public class RubyInterpreter extends Interpreter {
    private static Logger logger = LoggerFactory.getLogger(RubyInterpreter.class);

    public RubyInterpreter(Properties property) {
        super(property);
    }

    public void open() {
        logger.info("Opened");
    }

    public void close() {
        logger.info("Opened");
    }

    public InterpreterResult interpret(String s, InterpreterContext interpreterContext) {
        return new InterpreterResult(InterpreterResult.Code.SUCCESS, InterpreterResult.Type.TEXT, s);
    }

    public void cancel(InterpreterContext interpreterContext) {
        logger.info("Canceled");
    }

    public FormType getFormType() {
        logger.info("getFormType");
        return FormType.SIMPLE;
    }

    public int getProgress(InterpreterContext interpreterContext) {
        logger.info("getProcess");
        return 0;
    }
}
