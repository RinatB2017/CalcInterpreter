package types;

import java.util.ArrayList;

/**
 * Тип математическоого вектора.
 * Благодаря рекурсии можно строить матрицы.
 * 
 * Здесь "рекурсивный" тип нужен для хранения вектора,
 * а в "новая-грамматика.txt" рекурсивный нетерминал нужен для парсинга.
 * @author Ник
 *
 */
public class MathVector {
	private ArrayList<TypedValue> elements;
}
