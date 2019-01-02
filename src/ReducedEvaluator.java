import java.util.*;

/*Mark Fraser
 * 989279438
 * m_fraser3@u.pacific.edu
 * 
 * Assignment 6:  Building the Interpreter
 * 
 */

public class ReducedEvaluator {
	HashMap<String, String> global_table = new HashMap<String, String>(); // key is variable, value is string representation of value
	
	public void evaluate(Node program) {
		assert(program.getData().equals("program")); // assert we are given a program node
		LinkedList<Node> children = program.getChildren();
		// because this is the full language but a reduced evaluator
		// pop of vars and funcs sections of tree to get to commands
		children.pop();
		children.pop();
		Node cmds = children.pop();
		evaluateCmds(cmds);
		System.out.println("FINAL SYMBOL TABLE:");
		printGlobalTable();
	}

	private void evaluateCmds(Node cmds) {
		assert(cmds.getData().equals("cmds"));
		Iterator<Node> iterator = cmds.getChildren().iterator();
		while (iterator.hasNext()) {
			Node cmd = iterator.next();
			evaluateCmd(cmd);
		}
		
	}

	private void evaluateCmd(Node cmd) {
		assert(cmd.getData().equals("cmd"));
		LinkedList<Node> children = cmd.getChildren();
		String id = evaluateId(children.pop());
		children.pop(); // get rid of ':='
		Node expr = children.pop();
		String value = evaluateExpr(expr);
		global_table.put(id, value);
	}

	private String evaluateExpr(Node expr) {
		LinkedList<Node> children = expr.getChildren();
		String se1 = evaluateSimpleExpr(children.pop()); // pop guaranteed simple expression
		if (children.isEmpty()) {
			return se1;
		} else {
			Node rel_op = (Node) children.pop().getChildren().pop();
			String op = rel_op.getData().toString();
			String se2 = evaluateSimpleExpr(children.pop());
			switch (op) {
				case "=":	return String.valueOf(se1.equals(se2));
				case "!=":	return String.valueOf(!se1.equals(se2));
				case ">":	return String.valueOf(Float.valueOf(se1) > Float.valueOf(se2)); // automatically casts to float for simplicity
				case "<":	return String.valueOf(Float.valueOf(se1) < Float.valueOf(se2)); // automatically casts to float for simplicity
				case "<=":	return String.valueOf(Float.valueOf(se1) < Float.valueOf(se2) || se1.equals(se2)); // automatically casts to float for simplicity
				case ">=":	return String.valueOf(Float.valueOf(se1) > Float.valueOf(se2) || se1.equals(se2)); // automatically casts to float for simplicity
				default:	System.out.println("invalid rel_op");
							return null;
			}
		}
	}

	private String evaluateSimpleExpr(Node se) {
		// TODO Auto-generated method stub
		assert(se.getData().equals("simple_expr"));
		LinkedList<Node> children = se.getChildren();
		Node term = children.pop();
		String value; // declaring scope of value
		if (term.getData().equals("sign_op")) {
			// if someone stuck a negative before the first term, then get the negative value
			String op = getOp(term);
			value = evaluateTerm(children.pop());
			assert(Character.isDigit(value.charAt(0))); // assert we are given a number to negate
			if (op.equals("-")) value = op + value; // negate the number
		} else value = evaluateTerm(term);
		Boolean bool = false;
		Double add = 0.0;
		try{
			add = Double.valueOf(value);			
		} catch (Exception e) {
			// It's a boolean if an exception is thrown, so nothing
			// needs to be done
		}
		if (value.equals("true")) return value; // answer is true no matter what
		if (value.equals("false")) bool = true; // need to check for a true statement
		
		//TODO: implement {add_op term}
		Iterator<Node> iterator = children.iterator();
		while (!children.isEmpty()) {
			String add_op = getOp(children.pop());
			String term2  = evaluateTerm(children.pop());
			//TODO:
			if (bool) {
				// since we assume program is correct, "or" is not needed for evaluation
				if (term2.equals("true")) {
					value = Boolean.toString(true);
					break; // value is true no matter what
				}
				else continue;
			} else {
				//TODO: add to mul based on operator
				switch (add_op) {
				case "+": add = add + Double.valueOf(term2);
						  continue;
				case "-": add = add - Double.valueOf(term2);
						  continue;
				default: System.out.println("Incorrect input for simple_expr");
				}
			}
		}
		
		if (!bool) value = String.valueOf(add);
		return value;
	}

	private String evaluateTerm(Node term) {
		// TODO Auto-generated method stub
		assert(term.getData().equals("term"));
		LinkedList<Node> children = term.getChildren();
		String value = evaluateFactor(children.pop());
		Boolean bool = false;
		Double mul = 0.0;
		try{
			mul = Double.valueOf(value);			
		} catch (Exception e) {
			// It's a boolean if an exception is thrown, so nothing
			// needs to be done
		}
		if (value.equals("true") || value.equals("false")) bool = true;
		
		
		Iterator<Node> iterator = children.iterator();
		while (iterator.hasNext()) {
			String mul_op = getOp(children.pop());
			String factor = evaluateFactor(children.pop());
			//System.out.println(factor);
			if (bool) {
				// since we assume program is correct, "and" is not needed for evaluation
				if (value.equals("false") || factor.equals("false")) {
					value = Boolean.toString(false);
					break; // value is false no matter what
				}
				else continue;
			} else {
				//TODO: add to mul based on operator
				switch (mul_op) {
				case "*": mul = mul * Double.valueOf(factor);
						  continue;
				case "%": mul = mul % Double.valueOf(factor);
						  continue;
				case "/": mul = mul / Double.valueOf(factor);
						  continue;
				}
			}
		}
		
		if (!bool) value = String.valueOf(mul);
		
		return value;
	}

	private String getOp(Node op) {
		Node leaf = (Node) op.getChildren().pop();
		Pair value = (Pair) leaf.getData();
		return value.toString();
	}

	private String evaluateFactor(Node factor) {
		// TODO Auto-generated method stub
		assert(factor.getData().equals("factor"));
		LinkedList<Node> children = factor.getChildren();
		Node child = children.pop();
		String data = (String) child.getData();
		switch (data) {
		case "id":			String id = evaluateId(child);
							return global_table.get(id);
		case "constant": 	return evaluateConstant(child);
		case "not_op":   	// TODO: can get true values if anything null is put in
							return String.valueOf(!Boolean.valueOf(evaluateFactor(children.pop())));
		case "expr":		return evaluateExpr(child);
		default:			System.out.println("problem evaluating factor");
							return null;
		}
			
	}
	
	private String evaluateId(Node id) {
		assert(id.getData().equals("id"));
		Node leaf = (Node) id.getChildren().pop();
		Pair value = (Pair) leaf.getData();
		return value.toString();
	}

	private String evaluateConstant(Node constant) {
		Node leaf = (Node) constant.getChildren().pop();
		Pair value = (Pair) leaf.getData();
		if (value.toString().equals("#t")) {
			return String.valueOf(true);
		} else if (value.toString().equals("#f")) {
			return String.valueOf(false);
		} else return value.toString(); // return int or real_literal
	}
	
	private void printGlobalTable() {
		Collection<String> vars = global_table.values();
		// prints all contents of symbol table
		for (Map.Entry<String, String> entry : global_table.entrySet()) {
			String var = entry.getKey();
			String val = entry.getValue();
			System.out.println(var + " : " + val);
		}
	}
}
