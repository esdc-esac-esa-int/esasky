package esac.archive.esasky.ifcs.model.shared;

import org.junit.jupiter.api.Test;

import java.util.AbstractMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class EsaskyConstantsTest {
    @Test
    public void getAvailableLanguagesTest() {
        List<AbstractMap.SimpleEntry<String, String>> langs =  EsaSkyConstants.getAvailableLanguages(false);
        List<String> keys = langs.stream().map(l -> l.getKey()).collect(Collectors.toList());

        assertTrue(keys.contains("en"));
        assertTrue(keys.contains("es"));
        assertTrue(keys.contains("zh"));


        langs = EsaSkyConstants.getAvailableLanguages(true);
        keys = langs.stream().map(l -> l.getKey()).collect(Collectors.toList());
        assertTrue(keys.contains("fr"));
    }
}
