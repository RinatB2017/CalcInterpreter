package types;

/**
 * Перечисление всех возможных типов.
 * @author Ник
 *
 */
public enum Types {
	BOOLEAN, INTEGER, DOUBLE, DATE, FUNCTION, VECTOR;

	public static int getPriority(Types left) {
		switch(left){
		case BOOLEAN:
			return 1;
		case INTEGER:
			return 2;
		case DATE:
			return 3;
		case DOUBLE:
			return 4;
		case FUNCTION:
			return 5;
		case VECTOR:
			return 6;
		}
		return 0;
		//return ordinal();
	}
	
	public static Types getType(int t){
		if(t==1)
			return BOOLEAN;
		if(t==2)
			return INTEGER;
		if(t==3)
			return DATE;
		if(t==4)
			return DOUBLE;
		if(t==5)
			return FUNCTION;
		if(t==6)
			return VECTOR;
		
		return null;
	}
}
