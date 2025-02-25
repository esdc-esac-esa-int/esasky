/*
ESASky
Copyright (C) 2025 Henrik Norman

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published
by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

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
