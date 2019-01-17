package identity.foxtail;

import identity.foxtail.core.annotation.Encryption;
import identity.foxtail.core.consumer.EncryptionConsumer;
import identity.foxtail.core.domain.model.id.Group;
import mi.foxtail.crypto.EncryptionService;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Hello world!
 */
public class App {
    @Encryption
    private EncryptionService es;

    public App() {
        EncryptionConsumer.processEncryptionAnnotation(this);
    }

    public static void main(String[] args) throws NoSuchFieldException, UnsupportedEncodingException {
        System.out.println("Hello World!");
        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        System.out.println(LocalDateTime.parse("2018-01-13T16:37:53.570Z", DateTimeFormatter.ISO_ZONED_DATE_TIME));
        App app = new App();
        System.out.println(app.getEs());
        //System.out.println("".getBytes().length);
        Field field = Group.class.getDeclaredField("members");
        Deque<String> deque = new ArrayDeque<>(8);
        deque.push("1");
        deque.push("2");
        deque.push("3");
        deque.push("4");
        for (int i = 0; i < 4; i++)
            System.out.println(deque.pop());
    }

    public EncryptionService getEs() {
        return es;
    }
}
