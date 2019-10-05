package identity.hoprxi;

import identity.hoprxi.core.annotation.Encryption;
import identity.hoprxi.core.consumer.EncryptionConsumer;
import identity.hoprxi.core.domain.model.id.Group;
import mi.hoprxi.crypto.EncryptionService;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.time.ZonedDateTime;
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
        //System.out.println(LocalDateTime.parse("2019-03-05T15:37:52.452+08:00",DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        System.out.println(ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
        //System.out.println(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(OffsetDateTime.now()));
        //System.out.println(LocalDateTime.parse("2018-01-13T16:37:53.570Z", DateTimeFormatter.ISO_ZONED_DATE_TIME));
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
}
