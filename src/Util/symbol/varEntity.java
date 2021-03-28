package Util.symbol;

public class varEntity {
    public String nam;
    public Type typ;
    public RegId vid;

    public varEntity(String nam) {
        this.nam = nam;
    }
    public varEntity(String nam, Type typ) {
        this.nam = nam;
        this.typ = typ;
    }
}