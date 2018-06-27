package cmu.datastructures;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

import org.junit.Test;

import cmu.conditional.ChoiceFactory.Factory;
import cmu.conditional.Conditional;
import cmu.conditional.One;
import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.FeatureExprFactory;
import gov.nasa.jpf.vm.va.HybridStackHandler;
import gov.nasa.jpf.vm.va.HybridStackHandler.LiftedStack;
import gov.nasa.jpf.vm.va.HybridStackHandler.NormalStack;
import gov.nasa.jpf.vm.va.IStackHandler;
import gov.nasa.jpf.vm.va.StackHandlerFactory;
import gov.nasa.jpf.vm.va.StackHandlerFactory.SHFactory;

public class RandomDifferentialStackTest {

	private static final int ROUNDS = 1_000;
	private static final int METHOD_CALLS = 2;
	
	static {
		Conditional.setFM("");
		FeatureExprFactory.setDefault(FeatureExprFactory.bdd());
	}

	private final List<Object[]> stackHandlers = new ArrayList<>();

	public RandomDifferentialStackTest() {
		for (SHFactory shFactory : SHFactory.values()) {
			if (shFactory == SHFactory.Hybid) {
				for (LiftedStack liftedStack : LiftedStack.values()) {
					for (NormalStack normalStack : NormalStack.values()) {
						stackHandlers.add(new Object[] { shFactory, liftedStack, normalStack });
					}
				}
			} else {
				stackHandlers.add(new Object[] { shFactory, null, null, Factory.MapChoice });
			}
		}
	}

	public void setStackHandler(Object[] params) {
		int i = 0;
		final SHFactory shFactory = (SHFactory) params[i++];
		final LiftedStack liftedStack = (LiftedStack) params[i++];
		final NormalStack normalStack = (NormalStack) params[i++];

		StackHandlerFactory.setFactory(shFactory);

		if (normalStack != null) {
			HybridStackHandler.normalStack = normalStack;
		}
		if (liftedStack != null) {
			HybridStackHandler.liftedStack = liftedStack;
		}

	}

	private void differentalTest(Supplier<List<Object>> action) {
		List<Object> result = null;
		for (Object[] params : stackHandlers) {
			setStackHandler(params);
			final List<Object> execute = action.get();
			if (result == null) {
				result = execute;
			} else {
				for (int i = 0; i < result.size(); i++) {
					if (result.get(i) == execute.get(i)) {
						continue;
					}
					if (result.get(i) instanceof Exception && execute.get(i) instanceof Exception) {
						continue;
					}
					if (result.get(i).getClass().isArray()) {
						if (result.get(i).getClass() == int[].class) {
							assertArrayEquals((int[]) result.get(i), (int[]) execute.get(i));
							continue;
						}
					}
					assertEquals(result.get(i), execute.get(i));
				}
			}
		}
		assertNotNull(result);
	}

	@Test
	public void runDifferentialTest() {
		try {
			for (int i = 0; i < ROUNDS; i++) {
				final int seed = i;
				differentalTest(() -> {
					calls.clear();
					Random r = new Random(seed);
					IStackHandler sh = StackHandlerFactory.createStack(FeatureExprFactory.True(), 10, 10);

					List<Object> returnValues = new ArrayList<>(METHOD_CALLS);
					for (int j = 0; j < METHOD_CALLS; j++) {
						returnValues.add(applyStackOp(sh, r));
					}
					return returnValues;
				});
			}
			StackHandlerFactory.setFactory(SHFactory.Hybid);
			HybridStackHandler.normalStack = NormalStack.OneStack;
			HybridStackHandler.liftedStack = LiftedStack.Buffered;
		} catch (AssertionError e) {
			for (String call : calls) {
				System.out.println(call);
			}
			throw e;
		}
	}

	static Set<String> ignoredMethods = new HashSet<>();
	static {
		ignoredMethods.add("getLocalWidth");
		ignoredMethods.add("getMaxLocal");
		ignoredMethods.add("clone");
		ignoredMethods.add("toString");
		ignoredMethods.add("hashCode");
		ignoredMethods.add("equals");
		ignoredMethods.add("getLocal");
		ignoredMethods.add("setTop");
		ignoredMethods.add("getStack");
	}

	private static List<String> calls = new ArrayList<>(2);

	private static Object applyStackOp(IStackHandler sh, Random r) {
		Method[] methods = IStackHandler.class.getMethods();
		Arrays.sort(methods, (o1, o2) -> o1.toString().compareTo(o2.toString()));
		Method method = methods[r.nextInt(methods.length)];
		while (ignoredMethods.contains(method.getName())) {
			method = methods[r.nextInt(methods.length)];
		}
		try {
			Object[] args = createArgs(method, r);
			StringBuilder sb = new StringBuilder();
			sb.append("sh.");
			sb.append(method.getName());
			sb.append('(');
			for (Object object : args) {
				sb.append(object);
				sb.append(',');
			}
			if (args.length > 0) {
				sb.deleteCharAt(sb.length() - 1);
			}
			sb.append(");");
			calls.add(sb.toString());
			Object returnValue = method.invoke(sh, args);
			return returnValue;
		} catch (IllegalAccessException | InvocationTargetException e) {
			return e;
		}

	}

	private static Object[] createArgs(Method method, Random r) {
		Class<?>[] parameterTypes = method.getParameterTypes();

		Object[] args = new Object[parameterTypes.length];
		for (int i = 0; i < parameterTypes.length; i++) {
			Class<?> type = parameterTypes[i];
			if (type == FeatureExpr.class) {
				args[i] = FeatureExprFactory.True();// TODO randomize				
			} else if (type == int.class) {
				args[i] = r.nextInt(2);
			} else if (type == boolean.class) {
				args[i] = r.nextBoolean();
			} else if (type == Object.class) {
				if ("push".equals(method.getName())) {
					args[i] = One.valueOf(42);// TODO randomize value and type				
				} else {
					throw new RuntimeException(type.toString());
				}
			} else if (type == IStackHandler.Type.class) {
				args[i] = IStackHandler.Type.values()[r.nextInt(IStackHandler.Type.values().length)];
			} else if (type == Conditional.class) {

			} else {
				throw new RuntimeException(type.toString());
			}

		}

		return args;

	}

}
