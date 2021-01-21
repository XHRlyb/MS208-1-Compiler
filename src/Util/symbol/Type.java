package Util.symbol;

abstract public class Type {
    public boolean isNull() {
        return false;
    }
    public boolean isVoid() {
        return false;
    }
    public boolean isBool() {
        return false;
    }
    public boolean isInt() {
        return false;
    }
    public boolean isString() { return false; }

    public boolean sameType(Type t) {
        return false;
    }
}