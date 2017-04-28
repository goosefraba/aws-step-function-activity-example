# aws-step-function-activity-example
Example for a Java based activity handler

## Run the example
Define VM arguments:

    -Daws.accessKeyId=YOUR_ACCESS_KEY_ID -Daws.secretKey=YOUR_SECRET_KEY -DactivityArn=ACTIVITY_ARN
    
and invoke the main function:

    ActivityProcessor#main
    
## Resources
http://docs.aws.amazon.com/step-functions/latest/dg/welcome.html
