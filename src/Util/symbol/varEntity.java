package Util.symbol;

public class varEntity {
    public String nam;
    public Type typ;
    public int vid;

    public varEntity(String nam) {
        this.nam = nam;
        this.vid = 0;
    }
    public varEntity(String nam, Type typ) {
        this.nam = nam;
        this.typ = typ;
        this.vid = 0;
    }
}