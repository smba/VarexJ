package cmu.vatrace;

import org.eclipse.jdt.annotation.NonNull;

import cmu.vatrace.filters.StatementFilter;
import de.fosd.typechef.featureexpr.FeatureExpr;

public class ExceptionStatement extends Statement {
	
	private String cname;
	private String details;

	public ExceptionStatement(@NonNull Object op, Method m, FeatureExpr ctx) {
		super(op, m, ctx);
	}
	
	public ExceptionStatement(String cname, String details, Method method, FeatureExpr ctx) {
		this(null, method, ctx);
		this.cname = cname;
		this.details = details;
		setColor(NodeColor.firebrick1);
	}

	@Override
	public String toString() {
		if (details == null) {
			return cname;
		}
		return cname +": " + details;
	}
	
	@Override
	public boolean filterExecution(StatementFilter... filter) {
		if (cname.equals(java.lang.StackOverflowError.class.getName())) {
			return true;
		}
		return false;
	}
}
