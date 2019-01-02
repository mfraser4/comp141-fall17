import java.util.LinkedList;

/*Mark Fraser
 * 989279438
 * m_fraser3@u.pacific.edu
 * 
 * Assignment 5:  Building the Parser
 * 
 */
public class Node1<T> extends Leaf<T> {
	Node child1;
	
	public Node1(T data, Node child) {
		super(data);
		child1 = child;
	}
	
	public void print(int level) {
		super.print(level);
		child1.print(level+1);
	}
	
	@Override
	public LinkedList<Node> getChildren() {
		LinkedList<Node> nodes = super.getChildren();
		nodes.add(child1);
		return nodes;
	}
}
