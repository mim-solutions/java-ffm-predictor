import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Class for serving FFM model loaded from file
 * @author Piotr Wygocki
 *
 */
public class FFMPredictor implements Model {
    private int m_nr_feature;
    private int m_nr_factor;
    private int m_nr_field;
    private float[] m_coefs;
    /*
    this loads the model from a text file
    the format of the file is as follows:
    in the first line of the file, there are 4 integers, separated by spaces:
    NUM_FEATURES NUM_FACTORS NUM_FIELD SIZE
    SIZE is the number of coefficients in the model
    then, there are SIZE lines, i-th line containng the i-th coefficient
    so the file has SIZE+1 lines in total
     */
    public FFMPredictor(String path) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(path));
            String line = reader.readLine();
            String[] vals = line.split(" ");
            assert(vals.length == 4);

            m_nr_feature = Integer.parseInt(vals[0]);
            m_nr_factor = Integer.parseInt(vals[1]);
            m_nr_field = Integer.parseInt(vals[2]);
            int size = Integer.parseInt(vals[3]);

            m_coefs = new float[size];
            for (int i = 0; i < size; ++i) {
                m_coefs[i] = Float.parseFloat(reader.readLine());
            }

        } catch (FileNotFoundException e) {
            System.err.println("no such file " + path);
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("incorrect format of " + path);
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void print() {
        System.out.println("Model: ");
        System.out.print("m_nr_feature: ");
        System.out.println(m_nr_feature);
        System.out.print("m_nr_factor: ");
        System.out.println(m_nr_factor);
        System.out.print("m_nr_field: ");
        System.out.println(m_nr_field);
        System.out.print("size: ");
        System.out.println(m_coefs.length);
    }

    /**
     *
     * @param input array of integers - indices of features that are True
     *              the length of this array must be equal to NR_FIELD (m_nr_field)
     * @return a number between 0.0 and 1.0 inclusive, interpreted as probability
     */
    public double predict(int[] input) {
        final int kW_NODE_SIZE = 2;
        final int align0 = m_nr_factor*kW_NODE_SIZE;
        final int align1 = m_nr_field*align0;

        final double v = 1.0 / m_nr_field;

        assert(input.length == m_nr_field);

        double score = 0.0;
        for (int f1 = 0; f1 < m_nr_field; ++f1) {
            final int j1 = correct_feature_nr(input[f1]);
            for (int f2 = f1+1; f2 < m_nr_field; ++f2) {
                final int j2 = correct_feature_nr(input[f2]);

                final int w1 = j1*align1 + f2*align0;
                final int w2 = j2*align1 + f1*align0;
                for (int d = 0; d < m_nr_factor; ++d) {
                    final double W1 = m_coefs[w1+d];
                    final double W2 = m_coefs[w2+d];

                    score += W1 * W2 * v;
                }
            }
        }

        return sigmoid(score);
    }

    public int correct_feature_nr(final int nr) {
        assert(nr <= m_nr_feature);
        assert(nr > 0);
        return nr - 1;
    }

    public static double sigmoid(double x) {
        return 1.0/(1.0 + Math.exp(-x));
    }

    static public int[] hash_table(String[] input) {
        final int size = input.length;
        int[] int_input = new int[size];
        for (int i = 0; i < size; ++i) {
            int_input[i] = hash(input[i]);
        }
        return int_input;
    }

    @Override
    public double predict(FeatureValue[] features) {
        List<String> featureStrings = (List<String>)Arrays.stream(features).map(FeatureValue::toString);
        return predict(hash_table((String[])featureStrings.toArray()));
    }

    public double predict(String[] input) {
        return predict(hash_table(input));
    }

    static public int hash(String str) {
        final int D = 999999;
        final int mm_hash = MurmurHash.murmur3_32(str);
        return (((mm_hash % D) + D) % D) + 1;
    }

    static public void test_hash() {
        String[] sample_input = {
                "",
                "a",
                "bc",
                "def",
                "ghij",
                "klmno",
                "pqrstu",
                "vwxyz"
        };
        int[] expected = {
                1,
                85860,
                617085,
                412530,
                126256,
                460538,
                804307,
                453050
        };

        for (int i = 0; i < sample_input.length; ++i) {

            int res = hash(sample_input[i]);
            if (res != expected[i]) {
                System.err.print("expected: ");
                System.err.print(expected[i]);
                System.err.print(" but got: ");
                System.err.println(res);
            }
        }
    }

}