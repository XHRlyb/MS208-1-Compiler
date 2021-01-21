package Util.symbol;

import java.util.ArrayList;

public class funEntity extends Type {
    public String nam;
    public ArrayList<varEntity> params = new ArrayList<>();
    public Type retTyp;

    public funEntity(String nam) { this.nam = nam; }
}
