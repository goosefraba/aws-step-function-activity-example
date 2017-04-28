package at.goosefraba;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.SystemPropertiesCredentialsProvider;
import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.AWSStepFunctionsClientBuilder;
import com.amazonaws.services.stepfunctions.model.GetActivityTaskRequest;
import com.amazonaws.services.stepfunctions.model.GetActivityTaskResult;
import com.amazonaws.services.stepfunctions.model.SendTaskFailureRequest;
import com.amazonaws.services.stepfunctions.model.SendTaskSuccessRequest;
import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.concurrent.TimeUnit;

/**
 * Copyright goosefraba.at
 * Created by goosefraba on 28/04/2017.
 */
public class ActivityProcessor {

    public void listen() {
        final ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setSocketTimeout((int) TimeUnit.SECONDS.toMillis(5000));

        final AWSStepFunctions client = AWSStepFunctionsClientBuilder
                .standard()
                .withCredentials(new SystemPropertiesCredentialsProvider())
                .build();

        String greetingResult;

        while (true) {
            GetActivityTaskResult getActivityTaskResult =
                    client.getActivityTask(
                            new GetActivityTaskRequest().withActivityArn(System.getProperty("activityArn")));

            if (getActivityTaskResult != null) {
                System.out.println("Process event ...");

                try {
                    final JsonNode json = Jackson.jsonNodeOf(getActivityTaskResult.getInput());
                    greetingResult = process(json.get("who").textValue());

                    client.sendTaskSuccess(
                            new SendTaskSuccessRequest()
                                    .withOutput(greetingResult)
                                    .withTaskToken(getActivityTaskResult.getTaskToken()));
                } catch (final Exception e) {
                    client.sendTaskFailure(new SendTaskFailureRequest()
                            .withTaskToken(
                                    getActivityTaskResult.getTaskToken()));
                }
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String process(final String who) {
        return "{\"Hello \": \"" + who + "\"}";
    }
}
