public class FeatureValue {
    public String featureName;
    public Object featureValue;

    public FeatureValue(String featureName, Object featureValue) {
        this.featureName = featureName;
        this.featureValue = featureValue;
    }

    public String toString() {
        return featureName + "_" + featureValue.toString();
    }

}
