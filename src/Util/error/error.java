package Util.error;

import Util.position;

abstract public class error extends RuntimeException {
    public position pos;
    public String msg;

    public error(String msg, position pos) {
        this.pos = pos;
        this.msg = msg;
    }

    public String toString() { return msg + ":" + pos.toString(); }
}