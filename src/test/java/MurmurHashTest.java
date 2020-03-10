import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

public class MurmurHashTest {
    private static List<Arguments> murmurhashTestCases() {
        return List.of(
                Arguments.of("",0),
                Arguments.of("a",1009084850),
                Arguments.of("bc",1328615756),
                Arguments.of("def",-176587294),
                Arguments.of("ghij",326125929),
                Arguments.of("klmno", 2107458430),
                Arguments.of("pqrstu",-813194880),
                Arguments.of("vwxyz",192452857)
        );
    }

    @ParameterizedTest
    @MethodSource("murmurhashTestCases")
    void hash(String input, int expected) {
        assertEquals(MurmurHash.murmur3_32(input), expected);
    }
}
