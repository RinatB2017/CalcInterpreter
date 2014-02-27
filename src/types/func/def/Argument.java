package types.func.def;

import types.Types;

public class Argument {
	public Types type;
	Dimension dimension;
	
	public Argument(Types type, Dimension dimension) {
		this.type = type;
		this.dimension = dimension;
	}
	/*@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Argument other = (Argument) obj;
		if (type != other.type)
			return false;
		return true;
	}*/
	
	@Override
	public String toString() {
		return "" + type + ":" + dimension + "";
	}

	public Dimension getDimension() {
		return dimension;
	}
}
