/*Mark Fraser
 * 989279438
 * m_fraser3@u.pacific.edu
 * 
 * Assignment 5:  Building the Parser
 * 
 */
public class Printer {
	public static <T> void print(T value, int level) {
		String spaces = "";
		for (int i=0; i < level; i++) {
			spaces += "    ";
		}
		
		if (value == null) {
			System.out.println(spaces + "null");
		} else {
		System.out.println(spaces + value.toString());
		}
	}
}
