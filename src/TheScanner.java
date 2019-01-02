
import java.util.LinkedList;

/*Mark Fraser
 * 989279438
 * m_fraser3@u.pacific.edu
 * 
 * Assignment 5:  Building the Parser
 * 
 */

public class TheScanner {
	StringBuilder currBuilder;
	String numVal = ""; // stored as a string to generate numbers easier
	char currChar;
	LinkedList<Pair> tokens = new LinkedList<Pair>();

	/** Given a string, will return a linked list with the tokens derived from given string (in order).
	 * 
	 * @param string
	 * @return
	 */
	public LinkedList<Pair> processString(String string) {
		tokens = new LinkedList<Pair>();
		// initial screening of string
		//trim whitespace from beginning
		if (string != null) {
			string = string.trim();
			currBuilder = new StringBuilder(string);
		}
		
		// process line until completion
		while(currBuilder.length() != 0) {			
			// consume whitespace until we hit next possible token
			while(currBuilder.length() > 0 && currBuilder.charAt(0) == ' ') currBuilder.deleteCharAt(0);

			// check for special case that given line is nothing but white space
			if (currBuilder.length() == 0) {
				return tokens;
			}

			currChar = currBuilder.charAt(0);
			// allocate processing to different methods
			if (Character.isDigit(currChar) || currChar == '.') {
				// token must either be an Integer or Real number
				processNumToken();
			} else if (Character.isLetter(currChar)) {
				// if char is a letter, then must either be identifier
				// or keyword
				processWordToken();
			} else if (isPunctuation(currChar)) {
				processPunctuation();
			} else if (currChar == '#') {
				processBoolean();
			} else {
				tokens.add(new Pair(Tokens.ERROR, String.valueOf(currChar)));
				return tokens;
			}			
		}
		
		return tokens;
	}

	/** knowing that the symbol starting a boolean value has been found, this helper
	 * function processes which boolean is to be found, if there is one
	 */
	private void processBoolean() {
		// since we know currChar == '#', we can delete that char
		currBuilder.deleteCharAt(0);

		// error-checking
		if (currBuilder.length() == 0) {
			tokens.add(new Pair(Tokens.ERROR, String.valueOf(currChar)));
			endLine();
			return;
		}

		currChar = currBuilder.charAt(0);
		if (currChar == 't' || currChar == 'f') {
			tokens.add(new Pair(Tokens.BOOLEAN, "#" + currChar));
			if (currBuilder.length() > 0) currBuilder.deleteCharAt(0);
			return;
		}

		// error:  # is followed by an invalid input, or whitespace
		tokens.add(new Pair(Tokens.ERROR, "#" + currChar));
		endLine();
	}

	/**
	 *  Takes the current string and consumes the punctuation (one or two chars), and 
	 *  recognizes the error of an '!' without an '=' following.
	 */
	private void processPunctuation() {
		// check possible error with "!"
		if ((currChar == '!' || currChar == ':') && (currBuilder.length() == 1 || currBuilder.charAt(1) != '=')) {
			tokens.add(new Pair(Tokens.ERROR, String.valueOf(currChar)));
			endLine();
			return;
		}

		// check special cases for 2 character punctuation
		if (currBuilder.length() > 1 && currBuilder.charAt(1) == '=' && (currChar == ':' || 
				currChar == '!' || currChar == '<' || currChar == '>')) {
			tokens.add(new Pair(Tokens.PUNCTUATION, "" + currChar + currBuilder.charAt(1)));

			// we don't have to check currBuilder length because we've already checked
			currBuilder.deleteCharAt(1); // delete chars consumed
			currBuilder.deleteCharAt(0);
		} else {
			// process one-character punctuation
			tokens.add(new Pair(Tokens.PUNCTUATION, "" + currChar));
			if (currBuilder.length() > 0) currBuilder.deleteCharAt(0);
		}	
	}


	/** returns whether given character is punctuation
	 * @param currChar
	 * @return
	 */
	private boolean isPunctuation(char currChar) {
		if (currChar == '(' || currChar == ')' || currChar == ':' || currChar == '{' || currChar == '}'
				|| currChar == ',' || currChar == '+' || currChar == '-' || currChar == '*' || currChar == '!' ||
				currChar == '/' || currChar == '%' || currChar == '<' || currChar == '>' || currChar == '=' || currChar == ';')
			return true;
		return false;
	}

	/** this method is called once we know that the current character being
	 * processed is a letter, indicating either a keyword or else an identifier
	 */
	private void processWordToken() {
		String word = getWord(); // get the word to be processed

		if (isKeyword(word)) {
			tokens.add(new Pair(Tokens.KEYWORD, word));
		} else {
			tokens.add(new Pair(Tokens.IDENTIFIER, word));
		}
	}

	/** returns whether the given word is a keyword in our supplied language
	 * @param word
	 * @return
	 */
	private boolean isKeyword(String word) {
		if (word.equals("var") || word.equals("fun") || word.equals("if")
				|| word.equals("else") || word.equals("return") || word.equals("read")
				|| word.equals("write") || word.equals("not") || word.equals("or") ||
				word.equals("and") || word.equals("call") || word.equals("while")) 
			return true;

		return false;
	}

	/** a helper function to processWordToken() and processBoolean(), this function finds in
	 * the current string, from the current character, what the longest
	 * legal substring is for an identifier starting from the current character, and returns it
	 * @return word
	 */
	private String getWord() {
		String word = "";
		while(Character.isDigit(currChar) || Character.isLetter(currChar)) {
			// iteratively build the string by appending to the current word string
			word = word + String.valueOf(currChar);
			currBuilder.deleteCharAt(0);
			if (currBuilder.length() > 0) currChar = currBuilder.charAt(0);

			// exit condition if we've emptied the currBuilder
			if (currBuilder.length() == 0) {
				endLine();
				break;
			}
		}

		return word;
	}

	/** processes and prints the appropriate token with the value displayed */
	private void processNumToken() {
		int periods = 0;
		while (((Character.isDigit(currChar) || (currChar == '.' && periods <= 1))) && currBuilder.length() > 0){
			if (currChar == '.') periods +=1; // possible error-tracking
			if (periods == 2) {
				break;
			}
			numVal = numVal + currChar;
			currBuilder.deleteCharAt(0);
			if (currBuilder.length() > 0) currChar = currBuilder.charAt(0);
		}

		// print out proper token
		if(periods == 0) {
			tokens.add(new Pair(Tokens.INTEGER, numVal));
		} else if (periods >= 1 && numVal.length() > 1){
			// check to see if a period is the beginning or end of the string,
			// and add 0 if necessary
			if (numVal.indexOf(".") == 0) numVal = "0" + numVal;
			if (numVal.indexOf(".") == numVal.length()-1) numVal = numVal + "0";

			tokens.add(new Pair(Tokens.REAL, numVal)); 
		} else if (numVal.length() == 1 && periods == 1) {
			tokens.add(new Pair(Tokens.ERROR, numVal));
			endLine();
		}

		// numVal has been completely evaluated, and must be reset
		numVal = "";
	}

	/** when an error has been detected, this function is called to end
	 * the tokenizing process for this line by resetting both currChar
	 * and currBuilder
	 */
	private void endLine() {
		currBuilder = new StringBuilder();
		currChar = Character.MIN_VALUE;
	}
}
