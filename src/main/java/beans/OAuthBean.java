package beans;

import com.github.scribejava.apis.FitbitApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuth20Service;
import scribe_java.GatekeeperApi;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.inject.Inject;

@Singleton
public class OAuthBean {

    @Inject
    EnvriomentVariableBean variableBean;

    private OAuth20Service fitbitService;
    private OAuth20Service aberfitnessService;

    public OAuthBean() {
        //Empty
        System.out.println("Constructing");
    }

    @PostConstruct
    public void init() {
        if (variableBean.isFitbitDataPresent()) {
            fitbitService = new ServiceBuilder(variableBean.getFitbitClientId())
                    .apiSecret(variableBean.getFitbitClientSecret())
                    .scope("activity profile")
                    .callback(variableBean.getFitbitClientCallback())
                    .state("some_params")
                    .build(FitbitApi20.instance());
        }

        if (variableBean.isAberfitnessDataPresent()) {
            aberfitnessService = new ServiceBuilder(variableBean.getAberfitnessClientId())
                    .apiSecret(variableBean.getAberfitnessClientSecret())
                    //.scope("")
                    .callback(variableBean.getFitbitClientCallback())
                    //.state("")
                    .build(GatekeeperApi.instance());
        }
    }

    public OAuth20Service getFitbitService() {
        return fitbitService;
    }

    public OAuth20Service getAberfitnessService() {
        return aberfitnessService;
    }
}