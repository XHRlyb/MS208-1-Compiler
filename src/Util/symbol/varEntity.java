package Util.symbol;

public class varEntity {
    public String nam;
    public Type typ;
    public RegId vid;
    public boolean incls;

    public varEntity(String nam) {
        this.nam = nam;
        this.incls = false;
    }
    public varEntity(String nam, Type typ) {
        this.nam = nam;
        this.typ = typ;
        this.incls = false;
    }
}