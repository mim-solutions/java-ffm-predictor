import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

class FFMPredictorTest {
    private final static FFMPredictor ffm = new FFMPredictor("src/test/resources/example_model.txt");
    static List<Arguments> ffmPredictorHashTestCases() {
        return List.of(
                Arguments.of(
                        new String[]{"", "a", "bc", "def", "ghij", "klmno", "pqrstu", "vwxyz"},
                        new int[]{1, 85860, 617085, 412530, 126256, 460538, 804307, 453050}
                ));
    }

    @ParameterizedTest
    @MethodSource("ffmPredictorHashTestCases")
    void hash(String[] input, int[] expected) {
        assertArrayEquals(FFMPredictor.hash_table(input), expected);
    }

    static List<Arguments> sigmoidTestCases() {
        return List.of(
                Arguments.of(0.0, 0.5),
                Arguments.of(1.0, 1.0 / (1.0 + (1.0 / Math.E)))
        );
    }

    @ParameterizedTest
    @MethodSource("sigmoidTestCases")
    void sigmoid(double input, double expected) {
        assertEquals(FFMPredictor.sigmoid(input), expected);
    }

    @Test
    void testModelOnSimpleInput() {
        FeatureValue[] input = new FeatureValue[] {new FeatureValue("a", 1)};
        assertEquals(ffm.predict(input), 0.5);
    }
}
