package identity.foxtail.core.domain.model.privilege;

import java.util.Map;

/***
 * @author <a href="www.foxtail.cc/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 2019-01-19
 */
public class JobContext {
    public static final JobContext NULL_JOB_CONTEXT = new JobContext();
    private String rule;
    private Map bindVars;
}