import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class FFMPredictorTest {
    static final FFMPredictor ffm = new FFMPredictor("src/test/resources/test_model.txt");
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

    static List<Arguments> predictTestCases() {
        List<FeatureValue> l = ((Stream<FeatureValue>) IntStream.rangeClosed(1, 16).mapToObj(i -> new FeatureValue(String.valueOf(i), i))).collect(Collectors.toList());
        FeatureValue[] arr = l.toArray(FeatureValue[]::new);
        // these are essentiall random inputs - as such the result is also completely random - this serves as a check that the model
        return List.of(
                Arguments.of(arr, 0.8547664119819542)
        );
    }

    // this test serves only as a check that the model file has correct format
    // the inputs and the output is essentially random
    @ParameterizedTest
    @MethodSource("predictTestCases")
    void predict(FeatureValue[] input, double expected) {assertEquals(ffm.predict(input), expected);}
}
