package cn.icodening.collapse.sample.sequence.generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author icodening
 * @date 2023.07.14
 */
@Component
public class SimpleJdbcSequenceGenerator extends AbstractRepositorySequenceGenerator {

    private static final String UPDATE_SQL = "update t_sequence set sequence = sequence + :incrementBy where business_type = :businessType";

    private static final String QUERY_SQL = "select sequence from t_sequence where business_type = :businessType";

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private TransactionTemplate transactionTemplate;

    @Autowired
    public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Autowired
    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    protected Long increment(String businessType, int incrementBy) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("incrementBy", incrementBy);
        parameters.put("businessType", businessType);
        return transactionTemplate.execute(status -> {
            int update = namedParameterJdbcTemplate.update(UPDATE_SQL, parameters);
            if (update == 0) {
                throw new IllegalArgumentException("business type not exists. ['" + businessType + "']");
            }
            return namedParameterJdbcTemplate.queryForObject(QUERY_SQL, parameters, Long.class);
        });
    }
}
