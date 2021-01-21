package Util.symbol;

import java.util.HashMap;

public class classType extends Type {
    public String nam;
    public HashMap<String, varEntity> varMap = new HashMap<>();
    public HashMap<String, funEntity> funMap = new HashMap<>();
    public funEntity constructor = null;

    public classType(String nam) { this.nam = nam; }

    @Override
    public boolean sameType(Type o) {
        return o.isNull() ||
                ((o instanceof classType) && (nam.equals(((classType)o).nam)));
    }
}