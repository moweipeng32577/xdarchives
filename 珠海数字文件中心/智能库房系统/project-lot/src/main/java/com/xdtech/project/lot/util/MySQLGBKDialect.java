package com.xdtech.project.lot.util;

import org.hibernate.NullPrecedence;
import org.hibernate.dialect.MySQLDialect;

/**
 * Created by Rong on 2018/6/13.
 */
public class MySQLGBKDialect extends MySQLDialect {

	@Override
	public String renderOrderByElement(String expression, String collation, String order, NullPrecedence nulls) {
		StringBuilder orderByElement = new StringBuilder();
		if (nulls != NullPrecedence.NONE) {
			orderByElement.append("case when ").append(expression).append(" is null then ");
			if (nulls == NullPrecedence.FIRST) {
				orderByElement.append("0 else 1");
			} else {
				orderByElement.append("1 else 0");
			}
			orderByElement.append(" end, ");
		}

		if (expression.endsWith("sortsequence") || expression.endsWith("fsequence") || expression.endsWith("gsequence")
				|| expression.endsWith("qsequence")) {
			orderByElement.append(super.renderOrderByElement(expression, collation, order, NullPrecedence.NONE));
		} else {
			orderByElement.append(super.renderOrderByElement("convert(" + expression + " using gbk)", collation, order,
					NullPrecedence.NONE));
		}
		return orderByElement.toString();
	}
}