import org.junit.Assert;
import org.junit.Test;
import org.junit.Assume;
import persistence.StorageManager;

public class TestPersistance_Local {

    //Test Tokens
    String user1 = "jhb15";
    String accessToken1 = "access_toke_1";
    String refreshToken1 = "refresh_toke_1";

    String user2 = "ajb10";
    String accessToken2 = "access_toke_2";
    String refreshToken2 = "refresh_toke_2";

    //Test Creds
    String app_id1 = "Application ID 123";
    String app_secret1 = "Applicatio Secret 123";

    String app_id2 = "Application ID 999";

    @Test
    public void TestCommitToken() {
        StorageManager store = new StorageManager();
        String input;

        Assume.assumeTrue(store.initEM());

        store.commitTokenMap(user1, accessToken1, refreshToken1);
        input = store.getToken(user1, false);

        Assert.assertEquals(accessToken1, input);

        accessToken1 = "test_update_toke_1";
        store.commitTokenMap(user1, accessToken1, refreshToken1);
        input = store.getToken(user1, false);

        Assert.assertEquals(accessToken1, input);

        store.closeEM();
    }

    @Test
    public void TestRemoveToken() {
        StorageManager store = new StorageManager();
        String input;

        Assume.assumeTrue(store.initEM());

        store.commitTokenMap(user2, accessToken2, refreshToken2);
        Assert.assertNotNull(store.doesTokenMapExist(user2));

        store.removeTokenMap(user2);
        Assert.assertNull(store.doesTokenMapExist(user2));

        store.closeEM();
    }

    @Test
    public void TestRemoveToken_NonExistant() {
        StorageManager store = new StorageManager();

        Assume.assumeTrue(store.initEM());

        Assert.assertFalse(store.removeTokenMap("fake_uid"));

        store.closeEM();
    }

    @Test
    public void TestStoreAppCreds() {
        StorageManager store = new StorageManager();
        String[] input = {"", ""};

        Assume.assumeTrue(store.initEM());

        store.storeAppCreds(app_id1, app_secret1);
        input[0] = store.getAppId();
        input[1] = store.getAppSecret();

        Assert.assertEquals(app_id1, input[0]);
        Assert.assertEquals(app_secret1, input[1]);

        store.storeAppCreds(app_id2, app_secret1);
        input[0] = store.getAppId();
        Assert.assertEquals(app_id2, input[0]);

        store.closeEM();
    }

    @Test
    public void TestRemoveAppCreds() {
        StorageManager store = new StorageManager();
        boolean ret;

        Assume.assumeTrue(store.initEM());

        store.storeAppCreds(app_id1, app_secret1);
        Assert.assertNotNull(store.doesCredRecordExist());

        Assert.assertTrue(store.removeAppCreds());
        Assert.assertNull(store.doesCredRecordExist());

        store.closeEM();
    }

    @Test
    public void TestRemoveAppCreds_NonExitant() {
        StorageManager store = new StorageManager();

        Assume.assumeTrue(store.initEM());

        if (store.doesCredRecordExist() != null) {
            store.removeAppCreds();
        }

        Assert.assertFalse(store.removeAppCreds());

        store.closeEM();
    }
}