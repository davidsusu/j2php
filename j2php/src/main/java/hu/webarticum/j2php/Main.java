package hu.webarticum.j2php;

public class Main {

    public static void main(String[] args) {
        TypeMap typeMap = new TypeMap();
        typeMap.load(new Source(Main.class.getResource("/sample/SampleInterface.java.src")));
        typeMap.load(new Source(Main.class.getResource("/sample/SampleAbstractClass.java.src")));
        typeMap.load(new Source(Main.class.getResource("/sample/SampleClass.java.src")));
        for (Unit unit: typeMap.getUnits()) {
            UnitTranslator unitTranslator = new UnitTranslator(unit, typeMap);
            System.out.println(unitTranslator + "\n\n================\n\n");
        }
    }

}

