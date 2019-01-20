package identity.foxtail.core.domain.model.privilege;

/***
 * @author <a href="www.foxtail.cc/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 2019-01-19
 */
public class Job {
    //such as:read,discount
    private String name;
    private JobContext jobContext;

    public Job(String name, JobContext jobContext) {
        this.name = name;
        this.jobContext = jobContext;
    }

    public String name() {
        return name;
    }
}
