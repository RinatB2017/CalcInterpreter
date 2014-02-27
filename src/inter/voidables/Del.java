package inter.voidables;

import inter.Voidable;

public class Del extends Voidable{
	
	private String name4del;
	
	public Del(String s){
		name4del=s;
	}
	
	@Override
	public void execute() throws Exception {
		if (name4del==null){
			table.clear();
			output.addln("Все переменные удалены!");
		}else{
			if (!table.isEmpty()) {
				if (!table.containsKey(name4del)) {
					output.addln("del: Переменной " + name4del
							+ " нет в таблице переменных!");
				} else {
					table.remove(name4del);
					output.addln("del: Переменная " + name4del + " удалена.");
				}
			} else
				output.addln("Таблица переменных пуста, нечего удалять.");
		}	
	}
}
