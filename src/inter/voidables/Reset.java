package inter.voidables;

import java.util.ArrayList;

import options.OptId;
import types.TypedValue;
import types.Types;
import types.func.BuiltInFunction;
import types.func.EnumOfBuiltInFunctions;
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
			sinArg.add(new Argument(Types.DOUBLE, Dimension.RADIAN));
		
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
			
			
			table.put("arcsin", new TypedValue(1, Dimension.RADIAN));
			table.put("arccos", new TypedValue(1, Dimension.RADIAN));
			table.put("log", new TypedValue(1, Dimension.DIMENSIONLESS));
			table.put("pow", new TypedValue(2, Dimension.DIMENSIONLESS));
			*/
			output.addln("Сброшена таблица переменных и функций.");
		}

}
