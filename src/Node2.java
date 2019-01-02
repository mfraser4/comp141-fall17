import java.util.LinkedList;

/*Mark Fraser
 * 989279438
 * m_fraser3@u.pacific.edu
 * 
 * Assignment 5:  Building the Parser
 * 
 */
public class Node2<T> extends Node1<T> {
	Node child2;
	
	public Node2(T data, Node left, Node right) {
		super(data, left);
		child2 = right;
	}

	public void print(int level) {
		super.print(level);
		child2.print(level+1);
	}
	
	@Override
	public LinkedList<Node> getChildren() {
		LinkedList<Node> nodes = super.getChildren();
		nodes.add(child2);
		return nodes;
	}
}
