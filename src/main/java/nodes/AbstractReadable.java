package nodes;

public abstract class AbstractReadable {

    protected final String rename(String original, String oldName, String newName) {
        if(original.equals(oldName)) {
            return newName;
        } else if(original.equals("function " + oldName)) {
            return "function " + newName;
        } else {
            return original;
        }
    }

}
