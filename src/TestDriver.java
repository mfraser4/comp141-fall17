
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;

/*Mark Fraser
 * 989279438
 * m_fraser3@u.pacific.edu
 * 
 * Assignment 6:  Building the Interpreter
 * 
 */

public class TestDriver { 

	public static void main(String[] args) throws IOException {
		// loop continues running until end of file
		BufferedReader br = new BufferedReader(new FileReader(args[0]));
		String line;
		// Note:  BufferedReader reading method derived from:
		// http://www.java2s.com/Code/Java/File-Input-Output/DealwithKeyboardInputwithBufferedReader.htm
		InputStreamReader reader = new InputStreamReader(System.in);
		BufferedReader input = new BufferedReader(reader);
		TheScanner scanner = new TheScanner();
		LinkedList<Pair> allTokens = new LinkedList<Pair>();
		LinkedList<Pair> tokens = new LinkedList<Pair>();
		while ((line = br.readLine()) != null) {
			// check exit condition	
			if (line.equals("")) continue;
			tokens = scanner.processString(line);
			if (tokens.isEmpty()) continue;
			allTokens.addAll(tokens);
		}
		System.out.println("TOKENS:");
		printTokens(allTokens);
		System.out.println("PARSE TREE:");
		TheParserFull parser = new TheParserFull(allTokens);
		Node tree = parser.parse();
		tree.print(0);
		
		ReducedEvaluator evaluator = new ReducedEvaluator();
		evaluator.evaluate(tree);
	}

	private static void printTokens(LinkedList<Pair> tokens) {
		// TODO Auto-generated method stub
		Iterator<Pair> iterator = tokens.iterator();

		while (iterator.hasNext()) {
			Pair tempPair = iterator.next();
			Tokens token = tempPair.getToken();
			String string = tempPair.toString();
			System.out.println(token + ": " + string);
		}
		
		// create space between input lines for convenience of viewing
		System.out.println("\n");
	}

}
