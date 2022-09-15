import com.fasterxml.jackson.core.JsonParseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.assertj.core.api.Assertions.*;

public class ParserTest {
    Parser p;

    @Test
    void correctJson() throws IOException {
        p = new Parser("./src/test/resources/correctJson.json");

        assertThat(p.getTable()).hasToString("[[lada, vesta, базовая], [lada, vesta, средняя], " +
                "[lada, vesta, бизнес], [lada, x-ray, максимальная], [kia, rio, люкс], [kia, sportage, люкс], " +
                "[kia, sportage, супер-люкс], [kia, sorento, люкс], [kia, sorento, бизнес]]");
    }

    @Test
    void fileNotFound() {
        assertThatExceptionOfType(FileNotFoundException.class)
                .isThrownBy(() -> p = new Parser("/"));
    }

    @Test
    void emptyFile(){
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> p = new Parser("./src/test/resources/emptyFile.json"));
    }

    @Test
    void expectedCurlyBrace() {
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> p = new Parser("./src/test/resources/expectedCurlyBrace.json"))
                .withMessageStartingWith("Ожидается объект");
    }

    @Test
    void expectedSquareBracket() {
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> p = new Parser("./src/test/resources/expectedSquareBracket.json"))
                .withMessageStartingWith("Ожидается массив");

    }

    @Test
    void jsonSyntaxError() {
        assertThatExceptionOfType(JsonParseException.class)
                .isThrownBy(() -> p = new Parser("./src/test/resources/jsonSyntaxError.json"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "./src/test/resources/wrongBrandKey.json",
            "./src/test/resources/wrongModelsKey.json",
            "./src/test/resources/wrongModelsNameKey.json",
            "./src/test/resources/wrongTrimsKey.json",
            "./src/test/resources/wrongTrimsNameKey.json"
    })
    void wrongKey(String pathToJson) {
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> p = new Parser(pathToJson));
    }
}
