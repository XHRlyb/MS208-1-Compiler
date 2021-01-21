package Util.error;
import Util.position;

public class syntaxError extends error {
    public syntaxError(String msg, position pos) {
        super("Syntax Error: " + msg, pos);
    }

    public String toString() { return msg + ":" + pos.toString(); }
}