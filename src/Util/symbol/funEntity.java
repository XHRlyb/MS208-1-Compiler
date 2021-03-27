package Util.symbol;

import java.util.ArrayList;

public class funEntity extends Type {
    public String nam, abs_nam;
    public ArrayList<varEntity> params = new ArrayList<>();
    public Type retTyp;

    public funEntity(String nam, String abs_) { this.nam = nam; this.abs_nam = abs_ + nam; }
}
