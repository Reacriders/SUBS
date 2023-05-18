package com.reacriders.subs;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

public class YoutubeAPI {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String API_KEY = "AIzaSyC10oVm199XxcK7LUH_qNmgUyqe821iA-E";
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
    public static String getProfileImageUrl(String channelId) {
        try {
            YouTube.Channels.List channelRequest = get().channels().list("snippet");
            channelRequest.setId(channelId);
            channelRequest.setKey(API_KEY); // Set the API key directly here
            channelRequest.setFields("items(snippet(thumbnails(default(url))))");
            ChannelListResponse response = channelRequest.execute();
            List<Channel> channels = response.getItems();
            if (!channels.isEmpty()) {
                Channel channel = channels.get(0);
                return channel.getSnippet().getThumbnails().getDefault().getUrl();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static List<SearchResult> getChannelVideos(String channelId) {
        try {
            YouTube.Search.List searchRequest = get().search().list("snippet");
            searchRequest.setChannelId(channelId);
            searchRequest.setKey(API_KEY); // Set the API key directly here
            searchRequest.setOrder("date"); // Order by date
            searchRequest.setType("video"); // Only get videos
            searchRequest.setMaxResults((long) 50); // Limit to 50 videos
            searchRequest.setFields("items(id/videoId,snippet(title,description,thumbnails/default/url))");
            SearchListResponse searchResponse = searchRequest.execute();
            return searchResponse.getItems();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Map<String, String> getVideoDurations(List<String> videoIds) {
        try {
            YouTube.Videos.List videoRequest = get().videos().list("contentDetails");
            videoRequest.setId(String.join(",", videoIds));
            videoRequest.setKey(API_KEY);
            VideoListResponse response = videoRequest.execute();
            List<Video> videos = response.getItems();
            if (!videos.isEmpty()) {
                Map<String, String> videoDurations = new HashMap<>();
                for (Video video : videos) {
                    videoDurations.put(video.getId(), video.getContentDetails().getDuration());
                }
                return videoDurations;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
