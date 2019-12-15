import java.util.*;

public class SymbolTable extends Object {

    private Hashtable<String, LinkedList<String>> st;
    private Hashtable<String, String> types;
    private Hashtable<String, String> qualifiers;

    SymbolTable() {
        st = new Hashtable<>();
        types = new Hashtable<>();
        qualifiers = new Hashtable<>();

        st.put("global", new LinkedList<>());
    }

    // returns false if already defined in scope
    public void put(String id, String type, String qualifier, String scope) {
        LinkedList<String> tmp = st.get(scope);
        if (tmp == null) { // add new scope
            tmp = new LinkedList<>();
            tmp.add(id);
            st.put(scope, tmp);
        } else {
            tmp.addFirst(id);
        }
        types.put(id + scope, type);
        qualifiers.put(id + scope, qualifier);
    }

    public void print() {
        String scope;
        Enumeration e = st.keys();
        while (e.hasMoreElements()) {
            scope = (String) e.nextElement();
            System.out.println("\nScope: " + scope + "\n-----------------");
            LinkedList<String> tmp = st.get(scope);
            for (String id : tmp) {
                String type = types.get(id + scope);
                String qualifier = qualifiers.get(id + scope);
                System.out.printf("%s: %s (%s)\n", qualifier, id, type);
            }
        }
    }

    public boolean checkForDups() {
        boolean noDuplicateVars = true;
        String scope;
        Enumeration e = st.keys();
        while (e.hasMoreElements()) {
            scope = (String) e.nextElement();
            LinkedList<String> tmp = st.get(scope);
            while (tmp.size() > 0) {
                String id = tmp.pop();
                if (tmp.contains(id)) {
                    System.out.printf("Error: %s already defined in scope %s.\n", id, scope);
                    noDuplicateVars = false;
                }
            }
        }
        return noDuplicateVars;
    }

    public boolean inScope(String id, String scope) {
        LinkedList<String> tmp = st.get(scope);
        if (tmp == null) return false; // scope doesn't exist
        if (tmp.contains(id)) return true;
        return false;
    }

    public String typeLookup(String id, String scope) {
        return types.get(id + scope);
    }

    public String qualifierLookup(String id, String scope) {
        return qualifiers.get(id + scope);
    }
}
