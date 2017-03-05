package org.elasticsearch.dsl.parser.syntax.query.exact;

import com.alibaba.druid.sql.ast.expr.SQLBetweenExpr;
import org.elasticsearch.dsl.bean.AtomQuery;
import org.elasticsearch.dsl.enums.SQLConditionOperator;
import org.elasticsearch.dsl.exception.ElasticSql2DslException;
import org.elasticsearch.dsl.parser.helper.ElasticSqlArgTransferHelper;
import org.elasticsearch.dsl.parser.listener.ParseActionListener;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public class BetweenAndAtomQueryParser extends AbstractAtomExactQueryParser {

    public BetweenAndAtomQueryParser(ParseActionListener parseActionListener) {
        super(parseActionListener);
    }

    public AtomQuery parseBetweenAndQuery(SQLBetweenExpr betweenAndExpr, String queryAs, Object[] sqlArgs) {
        Object from = ElasticSqlArgTransferHelper.transferSqlArg(betweenAndExpr.getBeginExpr(), sqlArgs);
        Object to = ElasticSqlArgTransferHelper.transferSqlArg(betweenAndExpr.getEndExpr(), sqlArgs);

        if (from == null || to == null) {
            throw new ElasticSql2DslException("[syntax error] Between Expr only support one of [number,date] arg type");
        }

        return parseCondition(betweenAndExpr.getTestExpr(), SQLConditionOperator.BetweenAnd, new Object[]{from, to}, queryAs, new IConditionExactQueryBuilder() {
            @Override
            public QueryBuilder buildQuery(String queryFieldName, SQLConditionOperator operator, Object[] rightParamValues) {
                return QueryBuilders.rangeQuery(queryFieldName).gte(rightParamValues[0]).lte(rightParamValues[1]);
            }
        });
    }
}
