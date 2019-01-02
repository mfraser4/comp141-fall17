import java.util.LinkedList;

/*Mark Fraser
 * 989279438
 * m_fraser3@u.pacific.edu
 * 
 * Assignment 5:  Building the Parser
 * 
 */
public abstract class Node<T> {
	public abstract void print(int level);
	public abstract T getData();
	public abstract LinkedList<Node> getChildren();
}
