import java.util.LinkedList;

/*Mark Fraser
 * 989279438
 * m_fraser3@u.pacific.edu
 * 
 * Assignment 5:  Building the Parser
 * 
 */
public class Leaf<T> extends Node {
	T data;
	
	public Leaf(T data) {
		this.data = data;
	}
	
	public void print(int level) {
		if (data == null) return;
		Printer.print(data, level);
	}
	
	@Override
	public T getData() {
		return data;
	}
	
	@Override
	public LinkedList<Node> getChildren() {
		return new LinkedList<Node>();
	}
}
