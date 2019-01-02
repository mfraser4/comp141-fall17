
/*Mark Fraser
 * 989279438
 * m_fraser3@u.pacific.edu
 * 
 * Assignment 5:  Building the Parser
 * 
 */
public class Pair {
	private Tokens t;
	private String s;

	public Pair(Tokens token, String string) {
		t = token;
		s = string;
	}

	public Tokens getToken() {
		return t;
	}

	public String toString() {
		return s;
	}
}