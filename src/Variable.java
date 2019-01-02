
public class Variable<T> {
	String identifier = null;
	T value = null;
	
	public Variable(String name, T val) {
		identifier = name;
		value = val;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public T getValue() {
		return value;
	}
}
