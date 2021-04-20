package Util.symbol;
import IR.operand.*;

public class varEntity {
    public String nam;
    public Type typ;
    public Operand oprnd;
    public boolean isGlb = false;
    public boolean incls = false;

    public varEntity(String nam) {
        this.nam = nam;
    }
    public varEntity(String nam, Type typ) {
        this.nam = nam;
        this.typ = typ;
    }
}