package io.szp.server;

import io.szp.exception.SyntaxException;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

/**
 * 这个类是用于替换ANTLR4默认的打印错误处理，变成抛出异常。
 */
public class SQLThrowErrorListener extends BaseErrorListener {
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        throw new SyntaxException("Syntax error, line " + line + ":" + charPositionInLine + " " + msg);
    }
}
