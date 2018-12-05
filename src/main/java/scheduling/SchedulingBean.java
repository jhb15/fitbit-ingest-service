package scheduling;

import beans.ActivityMappingBean;
import beans.OAuthBean;
import datacollection.ActivityMapLoading;
import datacollection.FitbitDataCollector;
import datacollection.FitbitDataConverter;
import datacollection.FitbitDataProcessor;
import datacollection.ProcessedData;
import datacollection.mappings.ActivityMap;
import persistence.TokenMap;
import persistence.TokenMapDAO;
import scribe_java.GatekeeperLogin;
import scribe_java.gatekeeper.GatekeeperOAuth2AccessToken;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Scheduling EJB for kicking of the scheduling tasks that will run once the application has started up.
 *
 * @author James H Britton
 * @author "jhb15@aber,ac,uk"
 * @version 0.1
 */
@Startup
@Singleton
public class SchedulingBean {

    @PersistenceContext
    private EntityManager em;

    @EJB
    OAuthBean oAuthBean;

    @EJB
    ActivityMappingBean activityMappingBean;

    @EJB
    TokenMapDAO tokenMapDAO;

    @EJB
    GatekeeperLogin gatekeeperLogin;

    private FitbitDataCollector collector;
    private final FitbitDataConverter converter = new FitbitDataConverter();
    private final FitbitDataProcessor processor = new FitbitDataProcessor();
    private final ActivityMapLoading loading = new ActivityMapLoading();

    /**
     * This method is ran once the EJB is created.
     */
    @PostConstruct
    public void atStartup() {
        System.out.println("Scheduling EJB Initialised!");
        collector = new FitbitDataCollector(oAuthBean, tokenMapDAO);

        // These are needed on start up.
        updateActivityMappings();
    }

    /**
     * This method is ran every hour.
     */
    @Schedule(hour = "*/1", persistent = false)
    public void getFitbitData() {
        List<TokenMap> allTokens = TokenMap.getAllTokenMap(em);

        if (allTokens == null || allTokens.size() > 0) {
            return;
        }

        // Retrieve all the JSON strings needed for processing
        ConcurrentLinkedQueue<ProcessedData> data = collector.getAllUsersInfo(allTokens.toArray(new TokenMap[0]));

        // Convert all our strings to usable objects
        data = converter.convertActivityData(data);

        // Process all out data
        processor.ProcessData(data);

        // Update last accessed
        Date now = new Date();
        for (TokenMap map : allTokens) {
            map.setLastAccessed(now);
        }
    }

    /**
     * This method is ran every half hour.
     */
    @Schedule(hour = "*", minute = "*/30", persistent = false)
    public void updateActivityMappings() {
        GatekeeperOAuth2AccessToken accessToken = gatekeeperLogin.getAccessToken();
        ActivityMap[] map = loading.checkMappings(accessToken);
        if (map == null) return;

        activityMappingBean.UpdateMappings(map);
    }

    /**
     * This method is ran once the EJB is destroyed.
     */
    @PreDestroy
    public void atEnd() {
        System.out.println("Destroying Scheduling EJB");
    }
}
