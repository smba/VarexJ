package cmu.vagraph.operations;

import cmu.vagraph.VANode;
import de.fosd.typechef.featureexpr.FeatureExpr;
import gov.nasa.jpf.vm.Instruction;

public class SetLocal extends FieldOperation {

	public SetLocal(int reference, String localName, Object newValue, VANode vaNode, Instruction instruction, FeatureExpr ctx) {
		super(reference, localName, newValue, vaNode, instruction, ctx);
	}
	
}
