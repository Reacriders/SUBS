package com.reacriders.subs;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class YoutubeAPI {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String API_KEY = "AIzaSyCkZI3_qT_5njiMW6XEkVKF9nLthFzOI-M";
    private static YouTube youtube;

    public static YouTube get() {
        if (youtube == null) {
            try {
                youtube = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, null)
                        .setApplicationName("SUBS")
                        .build();
            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
            }
        }
        return youtube;
    }

    public static String getChannelName(String channelId) {
        try {
            YouTube.Channels.List channelRequest = get().channels().list("snippet");
            channelRequest.setId(channelId);
            channelRequest.setKey(API_KEY); // Set the API key directly here
            channelRequest.setFields("items(snippet(title))");
            ChannelListResponse response = channelRequest.execute();
            List<Channel> channels = response.getItems();
            if (!channels.isEmpty()) {
                Channel channel = channels.get(0);
                return channel.getSnippet().getTitle();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
