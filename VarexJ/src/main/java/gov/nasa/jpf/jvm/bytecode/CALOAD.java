//
// Copyright (C) 2006 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.
// 
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
// 
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//
package gov.nasa.jpf.jvm.bytecode;

import java.util.function.Function;

import cmu.conditional.Conditional;
import cmu.conditional.One;
import de.fosd.typechef.featureexpr.FeatureExpr;
import gov.nasa.jpf.vm.ArrayIndexOutOfBoundsExecutiveException;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.StackFrame;


/**
 * Load char from array
 * ..., arrayref, index => ..., value
 */
public class CALOAD extends ArrayLoadInstruction {

  protected Conditional<?> getPushValue (FeatureExpr ctx, StackFrame frame, ElementInfo e, int index) throws ArrayIndexOutOfBoundsExecutiveException {
    e.checkArrayBounds(ctx, index);
    return e.getCharElement(index).mapr(new Function<Character, Conditional<Integer>>() {

		@Override
		public Conditional<Integer> apply(Character x) {
			return new One<>((int) x);
		}
    	
    });
  }

  public int getByteCode () {
    return 0x34;
  }
  
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void pushValue(FeatureExpr ctx, StackFrame frame, Conditional value) {
		frame.push(ctx, value);
	}

	@Override
	Number getZeroValue() {
		return 0;
	}

}
