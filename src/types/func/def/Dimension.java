package types.func.def;

import exceptions.MyException;

public enum Dimension {
	RAD, DEG, DIMENSIONLESS;
	
	public static void checkNoDimensionless(Dimension d, boolean isFail) throws Exception{
		String s = "Недопустимое использовние безразмерностной величины.";
		if(d==DIMENSIONLESS){
			if(isFail)
				throw new Exception(s);
			else
				throw new MyException(s);
		}
	}
}