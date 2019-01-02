import java.util.LinkedList;

/*Mark Fraser
 * 989279438
 * m_fraser3@u.pacific.edu
 * 
 * Assignment 5:  Building the Parser
 * 
 */
public class Node3<T> extends Node2<T> {
	Node child3;
	
	public Node3(T data, Node left, Node middle, Node right) {
		super(data, left, middle);
		child3 = right;
	}
	
	public void print(int level) {
		super.print(level);
		child3.print(level+1);
	}
	
	@Override
	public LinkedList<Node> getChildren() {
		LinkedList<Node> nodes = super.getChildren();
		nodes.add(child3);
		return nodes;
	}
}
