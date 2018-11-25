package datacollection;

import com.google.gson.Gson;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Fitbit Data Converter is intended to be the object that takes in JSON from a Fitbit API response and converts it into
 * the format the Heath Data Repository is expecting.
 */
public class FitbitDataConverter {

    private static Gson gson = new Gson();

    public FitbitDataConverter() {
        //TODO implement
    }

    /**
     * @param input a linked list of the data with the Raw JSON strings
     * @return the list with all the processed data added
     */
    public ConcurrentLinkedQueue<ProcessedData> convertActivityData(ConcurrentLinkedQueue<ProcessedData> input) {
        // TODO check if threading will be faster

        for (ProcessedData data: input) {
            for (ActivityJSON json: data.getActivityJSON()) {
                FitBitJSON activityClass = gson.fromJson(json.JSON, FitBitJSON.class);
                activityClass.setFromDate(json.date);
                data.addProcessedActivity(activityClass);
            }
        }

        return input;
    }
}