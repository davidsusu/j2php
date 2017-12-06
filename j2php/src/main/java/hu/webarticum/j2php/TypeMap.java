package hu.webarticum.j2php;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeMap {
    
    private List<TypeMap> attachedTypeMaps = new ArrayList<TypeMap>();
    
    private Map<String, Unit> nameUnitMap = new HashMap<String, Unit>();
    
    public void attach(TypeMap other) {
        attachedTypeMaps.add(other);
    }
    
    public void load(Source source) /* TODO throws */ {
        Unit unit = new Unit(source);
        nameUnitMap.put(unit.getName(), unit);
    }
    
    public Unit lookUp(String name) {
        {
            if (nameUnitMap.containsKey(name)) {
                return nameUnitMap.get(name);
            }
            // TODO: nested types
        }
        
        for (TypeMap typeMap: attachedTypeMaps) {
            Unit result = typeMap.lookUp(name);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
    
}
