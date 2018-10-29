import org.hibernate.service.spi.ServiceException;

import javax.persistence.*;
import javax.persistence.EntityManager;
import java.io.Serializable;

public class StorageManager implements Serializable {

    @PersistenceContext
    private EntityManager em;

    public StorageManager() {

    }

    /**
     * This function is for initialising the Entity Manager
     * @return 0 for success, -1 for error
     */
    public boolean initEM() {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("fitbitPU");
            em = emf.createEntityManager();
        } catch (ServiceException se) {
            return false;
        }
        return true;
    }

    /**
     * This Function is for getting the Entity Manager
     */
    public void closeEM() {
        em.close();
    }

    /**
     * This function will take a Token Map and either create one if the user doesn't already have a Token Map stored or
     * update it.
     * @param uID id of currently logged in user.
     * @param accessToken access token to be stored.
     * @param refreshToken refresh token to be stored.
     */
    public void commitTokenMap(String uID, String accessToken, String refreshToken) {
        TokenMap tokenMap = doesTokenMapExist(uID);
        if (tokenMap == null) {
            createTokenMap(uID, accessToken, refreshToken);
        } else {
            updateTokenMap(uID, accessToken, refreshToken);
        }
    }

    /**
     * Gets the Access Token from Database
     * @param uId user id of logged in user
     * @param refresh boolean value TRUE for refresh token & FALSE for access token
     * @return Access Token for user
     */
    public String getToken(String uId, boolean refresh) {
        TokenMap tokenMap = getTokenMap(uId);
        if (refresh) {
            return tokenMap.getRefreshToken();
        } else {
            return tokenMap.getAccessToken();
        }
    }

    /**
     * This function removes a record from persistent storage.
     * @param uId user id of the current user
     * @return 0 for success, -1 failure (record doesn't exist)
     */
    public int removeTokenMap(String uId) {
        TokenMap tokenMap = doesTokenMapExist(uId);
        if (tokenMap != null) {
            em.getTransaction().begin();
            em.remove(tokenMap);
            em.getTransaction().commit();
        } else
            return -1;
        return 0;
    }

    /**
     * Adds a TokenMap record to db.
     * @param uID user id associated with tokens
     * @param accessToken access token for accessing FitBit data
     * @param refreshToken refresh token for receiving new access token when old one expires
     */
    private void createTokenMap(String uID, String accessToken, String refreshToken) {
        try {
            TokenMap tokenMap = new TokenMap();
            tokenMap.setUserID(uID);
            tokenMap.setAccessToken(accessToken);
            tokenMap.setRefreshToken(refreshToken);
            em.getTransaction().begin();
            em.persist(tokenMap);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void updateTokenMap(String uId, String accessToken, String refreshToken) {
        TokenMap tm = getTokenMap(uId);
        TokenMap tokenMap = em.find(TokenMap.class, tm.getId());
        em.getTransaction().begin();
        tm.setUserID(uId);
        tm.setAccessToken(accessToken);
        tm.setRefreshToken(refreshToken);
        em.getTransaction().commit();
    }

    private TokenMap getTokenMap(String uId) {
        Query q = em.createQuery("SELECT b FROM TokenMap b WHERE b.userID = :uId");
        q.setParameter("uId", uId);
        TokenMap tokeMap = (TokenMap) q.getSingleResult();
        return tokeMap;
    }

    /**
     * This method serves a function for retrieving a Token Map and also Checking for it't existence in the Database
     * @param uId user id of current user
     * @return return TokenMap if it Exists, null if it does not.
     */
    public TokenMap doesTokenMapExist(String uId) {
        TokenMap tokeMap;
        try {
            tokeMap = getTokenMap(uId);
        } catch (NoResultException nre) {
            tokeMap = null;
        }
        return tokeMap;
    }

    /**
     * This fuction allows a user to store application credentials in the DB, and also update them if they already exist
     * @param appId app_id code from fitbit
     * @param appSecret app_secret from fitbit
     */
    public void storeAppCreds(String appId, String appSecret) {
        ClientCredentials clientCredentials = doesCredRecordExist();
        if (clientCredentials != null) {
            updateCredRecord(clientCredentials, appId, appSecret);
        } else {
            createCredRecord(appId, appSecret);
        }
    }

    /**
     * This function retrieves the app_id from the database.
     * @return app_id returned, null if no record found
     */
    public String getAppId() {
        ClientCredentials creds = doesCredRecordExist();
        if (creds == null)
            return null;
        return creds.getClientId();
    }

    /**
     * This funtion is for retrieving the app_secret
     * @return app_secret returned, null if no record found.
     */
    public String getAppSecret() {
        ClientCredentials creds = doesCredRecordExist();
        if (creds == null)
            return null;
        return creds.getClientSecret();
    }

    /**
     * This funtion removes Application Credentials
     * @return
     */
    public int removeAppCreds() {
        ClientCredentials creds = doesCredRecordExist();
        if (creds != null) {
            em.getTransaction().begin();
            em.remove(creds);
            em.getTransaction().commit();
            return 0;
        } else
            return -1;
    }

    private void  updateCredRecord(ClientCredentials cred, String id, String secret) {
        em.getTransaction().begin();
        cred.setClientId(id);
        cred.setClientSecret(secret);
        em.getTransaction().commit();
    }

    private void createCredRecord(String id, String secret) {
        ClientCredentials creds = new ClientCredentials();
        creds.setClientId(id);
        creds.setClientSecret(secret);
        creds.setService("fitbit");
        em.getTransaction().begin();
        em.persist(creds);
        em.getTransaction().commit();
    }

    private ClientCredentials getClientCredentials() {
        Query q = em.createQuery("SELECT b FROM ClientCredentials b WHERE b.service = :serv");
        q.setParameter("serv", "fitbit");
        ClientCredentials creds = (ClientCredentials) q.getSingleResult();
        return creds;
    }

    /**
     *
     * @return
     */
    public ClientCredentials doesCredRecordExist() {
        ClientCredentials clientCredentials;
        try {
            clientCredentials = getClientCredentials();
        } catch (NoResultException nre) {
            clientCredentials = null;
        }
        return clientCredentials;
    }
}
