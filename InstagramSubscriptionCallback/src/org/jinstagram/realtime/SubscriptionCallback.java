package org.jinstagram.realtime;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Sachin Handiekar
 */
@SuppressWarnings("serial")
public class SubscriptionCallback extends HttpServlet {

    public static final String HUB_MODE = "hub.mode";
    public static final String HUB_CHALLENGE = "hub.challenge";
    public static final String HUB_VERIFY_TOKEN = "hub.verify_token";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handlePostRequest(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	System.out.println(" doGet ");
        handleCallbackURLVerification(req, resp);
    }

    private void handleCallbackURLVerification(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Instagram will send the following parameter in your callback
        // http://your-callback.com/url/?hub.mode=subscribe&hub.challenge=15f7d1a91c1f40f8a748fd134752feb3&hub.verify_token=myVerifyToken
    	System.out.println(" handleCallbackURLVerification : ");

        String hubChallenge = req.getParameter(HUB_CHALLENGE);
        String hubVerifyToken = req.getParameter(HUB_VERIFY_TOKEN);
        String hubMode = req.getParameter(HUB_MODE);

        resp.getWriter().print(hubChallenge);
    }


    private void handlePostRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        // Get the json data from the HttpServletRequest
        String jsonData = getBody(req);
        
        System.out.println(" jsonData : "+jsonData);
        SubscriptionResponseObject[] subscriptionResponseDataList = SubscriptionUtil.getSubscriptionResponseData(jsonData);

        for (SubscriptionResponseObject subscriptionResponseObject : subscriptionResponseDataList) {
            // ObjectId is the name of the tag, i.e. #jinstagram
            //SessionHandler.sendMessage(subscriptionResponseObject.getObjectId());
        }
    }


    private static String getBody(HttpServletRequest request) throws IOException {

        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }

        body = stringBuilder.toString();
        return body;
    }
}