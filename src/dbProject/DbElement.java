package dbProject;

import java.util.Objects;

public class DbElement {

    private String name;
    private int value;

    public DbElement(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public DbElement(String name) {
        this.name = name;
    }

    public synchronized void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DbElement other = (DbElement) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }
}
