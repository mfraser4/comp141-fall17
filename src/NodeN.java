import java.util.*;
/*Mark Fraser
 * 989279438
 * m_fraser3@u.pacific.edu
 * 
 * Assignment 5:  Building the Parser
 * 
 */
public class NodeN<T> extends Node<T> {
	T data;
	LinkedList<Node> nodes;
	
	public NodeN(T data) {
		this.data = data;
		nodes = new LinkedList<Node>();
	}
	
	public NodeN(T data, LinkedList<Node> nodes) {
		this(data);
		this.nodes = nodes;
	}
	
	public void addNode(Node node) {
		nodes.add(node);
	}
	
	@Override
	public void print(int level) {
		if (data == null) return;
		Printer.print(data, level);
		Iterator<Node> iterator = nodes.iterator();
		while(iterator.hasNext()) {
			Node temp = iterator.next();
			temp.print(level+1);
		}
	}
	
	@Override
	public T getData() {
		return data;
	}
	
	public LinkedList<Node> getChildren() {
		return nodes;
	}

}
