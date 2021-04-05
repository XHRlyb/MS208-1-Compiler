package Util.symbol;

import java.util.LinkedHashMap;

public class classType extends Type {
    public String nam;
    public LinkedHashMap<String, varEntity> varMap = new LinkedHashMap<>();
    public LinkedHashMap<String, funEntity> funMap = new LinkedHashMap<>();
    public funEntity constructor = null;

    public classType(String nam) { this.nam = nam; }

    @Override
    public boolean sameType(Type o) {
        return o.isNull() ||
                ((o instanceof classType) && (nam.equals(((classType)o).nam)));
    }
}