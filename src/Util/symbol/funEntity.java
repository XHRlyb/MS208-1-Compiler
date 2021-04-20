package Util.symbol;

import IR.Func;
import java.util.ArrayList;

public class funEntity extends Type {
    public String nam;//@, abs_nam;
    public ArrayList<varEntity> params = new ArrayList<>();
    public Type retTyp;

    public Func fun = null;

    public funEntity(String nam) { this.nam = nam;}
}
