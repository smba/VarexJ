package cmu.vatrace;

import java.io.PrintWriter;
import java.util.function.Function;

import org.eclipse.jdt.annotation.NonNull;

import cmu.conditional.Conditional;
import gov.nasa.jpf.vm.FieldInfo;

public class FieldPutStatement extends Statement {

	private Conditional<Integer> oldValue;
	private Conditional<Integer> newValue;
	private FieldInfo fi;

	private FieldPutStatement(@NonNull Object op, Method m) {
		super(op, m);
	}

	public FieldPutStatement(Conditional<Integer> oldValue, Conditional<Integer> newValue, Method m, FieldInfo fi) {
		this(null, m);
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.fi = fi;
	}

	private final Function<Integer, String> f = val -> {
		if (fi.isBooleanField()) {
			return Boolean.toString(val == 1);
		}
		if (fi.isReference()) {
			if (val == 0) {
				return "null";
			}
			return '@' + val.toString();
		}
		return val.toString();
	};
	
	@Override
	public String toString() {
		Conditional<String> oldString = oldValue.map(f);
		Conditional<String> newString = newValue.map(f);
		if (fi.getAnnotation(gov.nasa.jpf.annotation.Conditional.class.getName()) != null) {
			return "\"" + fi.getFullName() + " = " + newString + '\"';
		} else {
			return "\"" + fi.getFullName() + ": " + oldString + " \u2192 " + newString + '\"';
		}
	}
	
	@Override
	public void printLabel(PrintWriter out) {
		out.print(getID());// TODO dont create this node
		if (oldValue.equals(newValue)) {
			out.print("[label=X]");
			return; 
		}
		
		out.print("[label=");
		out.print(this);
		if (fi.getAnnotation(gov.nasa.jpf.annotation.Conditional.class.getName()) != null) {
			out.println(']');
			return;
		}
		if (oldValue.toMap().size() < newValue.toMap().size()) {
			out.print(", color=tomato");
		}
		
		out.println(']');
	}
}