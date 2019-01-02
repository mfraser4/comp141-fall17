import java.util.*;
/*Mark Fraser
 * 989279438
 * m_fraser3@u.pacific.edu
 * 
 * Assignment 5:  Building the Parser
 * 
 */
public class TheParser {
	LinkedList<Pair> tokens;
	Pair currToken;

	public TheParser(LinkedList<Pair> toks) {
		tokens = toks;
		if (!tokens.isEmpty()) {
			currToken = tokens.pop();
		} else {
			System.out.println("No tokens given");
		}
	}

	public Node parse() {
		if (currToken == null) return new Leaf<Pair>(null);
		else {
			Node program = parseProgram();
			if (!tokens.isEmpty()) {
				System.out.println("not all tokens able to be parsed");
			}
			return program;
		}
	}
	
	protected Node parseProgram() {
		return new Node1<String>("program", parseCmds());
	}

	protected Node parseCmds() {
		// TODO Auto-generated method stub
		Node cmd = parseCmd();
		if (cmd.getData() == null) return new Leaf<String>("cmds");
		LinkedList<Node> nodes = new LinkedList<Node>();
		while(true) {
			nodes.add(cmd);
			cmd = parseCmd();
			if (cmd.getData() == null) break;
		}
		return new NodeN<String>("cmds", nodes);
	}

	protected Node parseCmd() {
		Node id = parseId();
		if (id.getData() != null) {
			assert(currToken != null && currToken.toString().equals(":="));
			Node assign_op = new Leaf<Pair>(currToken);
			consumeToken();
			Node expr = parseExpr();
			assert(expr.getData() != null);
			assert(currToken != null && currToken.toString().equals(";"));
			consumeToken();
			return new Node3<String>("cmd", id, assign_op, expr);
		} else {
			return new Leaf<Pair>(null);
		}
	}

	protected Node parseNotOp() {
		// return null if there are no more tokens
		if (currToken == null) {
			return new Leaf<Pair>(null);
		}
		if (currToken.toString().equals("not")) {
			Pair temp = currToken;
			consumeToken();
			return new Node1<String>("not_op", new Leaf<Pair>(temp));
		}

		return new Leaf<Pair>(null);
	}

	protected Node parseFactor() {
		Node node = parseConstant();
		if (node.getData() == null) {
			node = parseId();
			if (node.getData() == null) {
				node = parseNotOp();
				if (node.getData() == null) {
					if(currToken != null && currToken.toString().equals("(")) {
						consumeToken();
						node = parseExpr();
						assert(currToken != null && currToken.toString().equals(")"));
						consumeToken();
					} else {
						return new Leaf<Pair>(null);
					}
				} else {
					return new Node2<String>("factor", node, parseFactor());
				}
			}
		}

		if (node.getData() != null) {
			return new Node1<String>("factor", node);
		} else {
			return new Leaf<String>(null);
		}
	}

	protected Node parseExprs() {
		Node expr = parseExpr();
		if (expr.getData() == null) return new Leaf<String>("exprs");
		LinkedList<Node> nodes = new LinkedList<Node>();
		while(true) {
			nodes.add(expr);
			if (currToken.toString().equals(",")) {
				consumeToken();
				expr = parseExpr();
			} else {
				break;
			}
		}
		return new NodeN<String>("exprs", nodes);
	}

	protected Node parseExpr() {
		// TODO Auto-generated method stub
		Node se1 = parseSimpleExpr();
		if (se1.getData() == null) return new Leaf<Pair>(null);
		Node rel_op = parseRelOp();
		if (rel_op.getData() == null) {
			return new Node1<String>("expr", se1);
		}
		Node se2 = parseSimpleExpr();
		assert(se2.getData() != null);
		return new Node3<String>("expr", se1, rel_op, se2);
	}

	protected Node parseSimpleExpr() {
		// TODO Auto-generated method stub
		Node sign_op = parseSignOp();
		Node term1 = parseTerm();
		if (term1.getData() == null) {
			if (sign_op.getData() != null) assert(1 == 0);
			return new Leaf<Pair>(null);
		}

		Node add_op = parseAddOp();
		if (add_op.getData() != null) {
			// when simple_expr expands to include more add_ops and terms
			Node term2 = parseTerm();
			assert(term2.getData() != null);
			LinkedList<Node> nodes = new LinkedList<Node>();
			if (sign_op.getData() != null) nodes.add(sign_op);
			nodes.add(term1);
			while(true) {
				nodes.add(add_op);
				nodes.add(term2);
				add_op = parseAddOp();
				if (add_op.getData() == null) break;
				term2 = parseTerm();
				assert(term2.getData() != null);
			}

			return new NodeN<String>("simple_expr", nodes);
		} else {
			// when simple_expr ends with one term
			if (sign_op.getData() != null) {
				return new Node2<String>("simple_expr", sign_op, term1);
			}
			return new Node1<String>("simple_expr", term1);
		}
	}

	protected Node parseAddOp() {
		// return null if there are no more tokens
		if (currToken == null) {
			return new Leaf<Pair>(null);
		}
		if (currToken.toString().equals("+")
				|| currToken.toString().equals("-") 
				|| currToken.toString().equals("or")) {
			Pair temp = currToken;
			consumeToken();
			return new Node1<String>("add_op", new Leaf<Pair>(temp));
		} else {
			return new Leaf<Pair>(null);
		}
	}

	protected Node parseSignOp() {
		// return null if there are no more tokens
		if (currToken == null) {
			return new Leaf<Pair>(null);
		}
		if (currToken.getToken() == Tokens.PUNCTUATION && (currToken.toString().equals("+") || currToken.toString().equals("-"))) {
			Pair temp = currToken;
			consumeToken();
			return new Node1<String>("sign_op", new Leaf<Pair>(temp));
		} else {
			return new Leaf<Pair>(null);
		}
	}

	protected Node parseTerm() {
		// TODO Auto-generated method stub
		Node factor = parseFactor();
		if (factor.getData() == null) return new Leaf<Pair>(null);
		Node mul_op = parseMulOp();

		if (mul_op.getData() != null) {
			// when term expands with mul_ops
			LinkedList<Node> nodes = new LinkedList<Node>();
			nodes.add(factor);
			factor = parseFactor();
			assert(factor.getData() != null);
			while(true) {
				nodes.add(mul_op);
				nodes.add(factor);
				mul_op = parseMulOp();
				if (mul_op.getData() == null) break;
				factor = parseFactor();
				assert(factor.getData() != null);
			}

			return new NodeN<String>("term", nodes);
		} else {
			return new Node1<String>("term", factor);
		}
	}

	protected Node parseMulOp() {	
		// return null if there are no more tokens
		if (currToken == null) {
			return new Leaf<Pair>(null);
		}
		String string = currToken.toString();
		boolean bool = string.equals("*") || string.equals("/")
				|| string.equals("%") || string.equals("and");
		if (bool) {
			Leaf<Pair> mul_op = new Leaf<Pair>(currToken);
			consumeToken();
			return new Node1<String>("mul_op", mul_op);
		}

		return new Leaf<Pair>(null);
	}

	protected Node parseRelOp() {
		// return null if there are no more tokens
		if (currToken == null) {
			return new Leaf<Pair>(null);
		}
		String string = currToken.toString();
		boolean bool = (currToken.getToken() == Tokens.PUNCTUATION) &&
				(string.equals("=") || string.equals("!=") || string.equals("<")
						|| string.equals(">") || string.equals("<=") || string.equals(">="));
		if (bool) {
			Leaf<Pair> rel_op = new Leaf<Pair>(currToken);
			consumeToken();
			return new Node1<String>("rel_op", rel_op);
		}

		return new Leaf<Pair>(null);
	}

	protected Node parseId() {
		// return null if there are no more tokens
		if (currToken == null) {
			return new Leaf<Pair>(null);
		}
		if (currToken.getToken() == Tokens.IDENTIFIER) {
			Pair temp = currToken;
			consumeToken();
			return new Node1<String>("id", new Leaf<Pair>(temp));
		}
		return new Leaf<Pair>(null);
	}

	protected Node parseConstant() {
		// return null if there are no more tokens
		if (currToken == null) {
			return new Leaf<Pair>(null);
		}
		Tokens token = currToken.getToken();
		if (token == Tokens.INTEGER || token == Tokens.REAL || token == Tokens.BOOLEAN) {
			Pair constant = currToken;
			consumeToken();
			return new Node1<String>("constant", new Leaf<Pair>(constant));
		}

		return new Leaf<Pair>(null);
	}

	protected void consumeToken() {
		if (currToken == null) {
			return;
		}
		if (tokens.isEmpty()) currToken = null;
		else currToken = tokens.pop();
	}
}
