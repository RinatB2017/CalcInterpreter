package inter.voidables;

import java.util.ArrayList;

import options.OptId;
import types.TypedValue;
import types.Types;
import types.func.builtins.BuiltInFunction;
import types.func.builtins.EnumOfBuiltInFunctions;
import types.func.def.Argument;
import types.func.def.Dimension;
import inter.*;
import static options.Options.setname;

public class Reset extends Voidable {
	private String string;
	
	public Reset(String string) {
		this.string=string;
	}

	@Override
	public void execute() throws Exception {
		if(string==null){
			options.resetAll();
			resetTable();
			output.addln("Всё сброшено.");
		}else if(string.equals("table")){
			resetTable();
		}else{
			OptId what = setname(string);
			options.reset(what);
		}
		return;
	}
	
	// Сброс таблицы переменных в исходное состояние
		public void resetTable() {
			table.clear();
			table.put("e", new TypedValue(Math.E));
			table.put("pi", new TypedValue(Math.PI));
			table.put("ans", new TypedValue(0));
			
			ArrayList<Argument> sinArg = new ArrayList<Argument>();
			sinArg.add(new Argument(Types.DOUBLE, Dimension.RAD));
			
			ArrayList<Argument> arcsinArg = new ArrayList<Argument>();
			arcsinArg.add(new Argument(Types.DOUBLE, Dimension.DIMENSIONLESS));
			
			ArrayList<Argument> oneintarg = new ArrayList<Argument>();
			oneintarg.add(new Argument(Types.INTEGER, Dimension.DIMENSIONLESS));
			
			// Исполнение функций - в классе BuiltInFunction.java
			table.put(
				"sin",
				new TypedValue(
					new BuiltInFunction(
						EnumOfBuiltInFunctions.SIN,
						sinArg,
						new Argument(Types.DOUBLE, Dimension.DIMENSIONLESS)
					)
				)
			);
			table.put(
				"cos",
				new TypedValue(
					new BuiltInFunction(
						EnumOfBuiltInFunctions.COS,
						sinArg,
						new Argument(Types.DOUBLE, Dimension.DIMENSIONLESS)
					)
				)
			);
			table.put(
				"tan",
				new TypedValue(
					new BuiltInFunction(
						EnumOfBuiltInFunctions.TAN,
						sinArg,
						new Argument(Types.DOUBLE, Dimension.DIMENSIONLESS)
					)
				)
			);
			
			table.put(
				"arcsin",
				new TypedValue(
					new BuiltInFunction(
						EnumOfBuiltInFunctions.ARCSIN,
						arcsinArg,
						new Argument(Types.DOUBLE, Dimension.RAD)
					)
				)
			);
			
			table.put(
				"arccos",
				new TypedValue(
					new BuiltInFunction(
						EnumOfBuiltInFunctions.ARCCOS,
						arcsinArg,
						new Argument(Types.DOUBLE, Dimension.RAD)
					)
				)
			);
			
			table.put(
				"exitf",
				new TypedValue(
					new BuiltInFunction(
						EnumOfBuiltInFunctions.EXITF,
						oneintarg,
						null)
					)
			);
			/*table.put(
				"ctg",
				new TypedValue(
					new BuiltInFunction(
						EnumOfBuiltInFunctions.CTG,
						sinArg,
						new Argument(Types.DOUBLE, Dimension.DIMENSIONLESS)
					)
				)
			);
			
			*/
			output.addln("Сброшена таблица переменных и функций.");
		}

}
