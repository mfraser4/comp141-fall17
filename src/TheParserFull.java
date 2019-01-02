import java.util.*;

public class TheParserFull extends TheParser {
	public TheParserFull(LinkedList<Pair> toks) {
		super(toks);
	}
	
	@Override
	protected Node parseProgram() {
		Node vars = parseVars();
		Node funcs = parseFuncs();
		Node cmds = parseCmds();
		return new Node3<String>("program", vars, funcs, cmds);
	}
	
	private Node parseFuncs() {
		// TODO Auto-generated method stub
		Node func = parseFunc();
		if (func.getData() == null) return new Leaf<String>("funcs");
		LinkedList<Node> nodes = new LinkedList<Node>();
		while(true) {
			nodes.add(func);
			func = parseFunc();
			if (func.getData() == null) break;
		}
		return new NodeN<String>("funcs", nodes);
	}

	private Node parseFunc() {
		// TODO Auto-generated method stub
		if (currToken != null && currToken.toString().equals("fun")) {
			consumeToken();
			Node id = parseId();
			assert(id.getData() != null);
			assert(currToken.toString().equals("("));
			consumeToken();
			Node id_list = parseIdList();
			assert(id_list.getData() != null);
			assert(currToken.toString().equals(")"));
			consumeToken();
			Node block = parseBlock();
			assert(block.getData() != null);
			return new Node3<String>("func", id, id_list, block);
		} else {
			return new Leaf<Pair>(null);
		}
	}

	protected Node parseBlock() {
		//TODO
		if (currToken == null) return new Leaf<Pair>(null);
		if (currToken.toString().equals("{")) {
			consumeToken();
			Node vars = parseVars();
			Node cmds = parseCmds();
			assert(currToken.toString().equals("}"));
			consumeToken();
			return new Node2<String>("block", vars, cmds);
		} else {
			return new Leaf<String>(null);
		}
	}

	private Node parseVars() {
		// TODO Auto-generated method stub
		Node var = parseVar();
		if (var.getData() == null) return new Leaf<String>("vars");
		LinkedList<Node> nodes = new LinkedList<Node>();
		while(true) {
			nodes.add(var);
			var = parseVar();
			if (var.getData() == null) break;
		}
		return new NodeN<String>("vars", nodes);
	}

	private Node parseVar() {
		// TODO Auto-generated method stub
		if (currToken != null && currToken.toString().equals("var")) {
			consumeToken();
			Node id_list = parseIdList();
			assert(id_list.getData() != null);
			assert(currToken.toString().equals(";"));
			consumeToken();
			return new Node1<String>("var", id_list);
		} else {
			return new Leaf<Pair>(null);
		}
	}

	private Node parseIdList() {
		// TODO Auto-generated method stub
		Node id = parseId();
		if (id.getData() != null) {
			LinkedList<Node> nodes = new LinkedList<Node>();
			while(true) {
				nodes.add(id);
				if (currToken != null && currToken.toString().equals(",")) {
					consumeToken();
					id = parseId();
				} else {
					break;
				}
			}
			
			return new NodeN<String>("id_list", nodes);
		} else {
			return new Leaf<Pair>(null);
		}
	}
	
	@Override
	protected Node parseCmd() {
		Node id = parseId();
		if (id.getData() != null) {
			assert(currToken != null && currToken.toString().equals(":="));
			Node assign_op = new Leaf<Pair>(currToken);
			consumeToken();
			if (currToken.toString().equals("call")) {
				Node key = new Leaf<Pair>(currToken);
				consumeToken();
				Node id2 = parseId();
				assert(currToken.toString().equals("("));
				consumeToken();
				Node exprs = parseExprs();
				assert(currToken.toString().equals(")"));
				consumeToken();
				assert(currToken != null && currToken.toString().equals(";"));
				consumeToken();
				LinkedList<Node> nodes = new LinkedList<Node>();
				nodes.add(id);
				nodes.add(assign_op);
				nodes.add(key);
				nodes.add(id2);
				nodes.add(exprs);
				return new NodeN<String>("cmd", nodes);
			} else {
				Node expr = parseExpr();
				assert(expr.getData() != null);
				assert(currToken != null && currToken.toString().equals(";"));
				consumeToken();
				return new Node3<String>("cmd", id, assign_op, expr);
			}
		} else if (currToken != null && currToken.getToken() == Tokens.KEYWORD) {
			if (currToken.toString().equals("if") || currToken.toString().equals("while")) {
				Node key = new Leaf<Pair>(currToken);
				consumeToken();
				assert(currToken.toString().equals("("));
				consumeToken();
				Node expr = parseExpr();
				assert(expr.getData() != null);
				assert(currToken.toString().equals(")"));
				consumeToken();
				Node block = parseBlock();
				assert(block.getData() != null);
				return new Node3<String>("cmd", key, expr, block);
			} else if (currToken.toString().equals("return") || currToken.toString().equals("read") || currToken.toString().equals("write")) {
				Node key = new Leaf<Pair>(currToken);
				consumeToken();
				Node id_expr = new Leaf<Pair>(null);
				if (((Pair)key.getData()).toString().equals("return")) {
					id_expr = parseExpr();
				} else {
					id_expr = parseId();
				}
				assert(id_expr.getData() != null);
				assert(currToken.toString().equals(";"));
				consumeToken();
				return new Node2<String>("cmd", key, id_expr);
			} else {
				Node block = parseBlock();
				assert(block.getData() != null);
				return new Node1<String>("cmd", block);
			}
		} else {
			return new Leaf<Pair>(null);
		}
	}
}
