package beans;

import javax.ejb.Singleton;

@Singleton(name = "EnvriomentVariableEJB")
public class EnvriomentVariableBean {

    // Fitbit Info
    private static final String fitbitClientId = System.getenv("fitbitClientId");
    private static final String fitbitClientSecret = System.getenv("fitbitClientSecret");
    private static final String fitbitClientCallback = System.getenv("fitbitClientCallback");

    //Aberfitness Info
    private static final String aberfitnessClientId = System.getenv("ABERFITNESS_CLI_ID");
    private static final String aberfitnessClientSecret = System.getenv("ABERFITNESS_CLI_SECRET");
    private static final String aberfitnessClientCallback = System.getenv("ABERFITNESS_CLI_CALLBACK");

    public EnvriomentVariableBean() {
    }

    public String getFitbitClientSecret() {
        return fitbitClientSecret;
    }

    public String getFitbitClientId() {
        return fitbitClientId;
    }

    public String getFitbitClientCallback() {
        return fitbitClientCallback;
    }

    public String getAberfitnessClientId() {
        return aberfitnessClientId;
    }

    public String getAberfitnessClientSecret() {
        return aberfitnessClientSecret;
    }

    public String getAberfitnessClientCallback() {
        return aberfitnessClientCallback;
    }

    public boolean isAberfitnessDataPresent() {
        return aberfitnessClientId != null && aberfitnessClientSecret != null && aberfitnessClientCallback != null;
    }

    public boolean isFitbitDataPresent() {
        return fitbitClientId != null && fitbitClientSecret != null && fitbitClientCallback != null;
    }
}
