package Util;

import Util.error.syntaxError;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class ErrorListener extends BaseErrorListener {
    @Override
    public void syntaxError(Recognizer<?, ?> rcg, Object ofdSymbol, int row, int col, String msg, RecognitionException e) {
        throw new syntaxError(msg, new position(row, col));
    }
}