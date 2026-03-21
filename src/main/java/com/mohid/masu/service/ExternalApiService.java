package com.mohid.masu.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohid.masu.util.HttpUtil;
import com.mohid.masu.util.PropertiesLoader;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ExternalApiService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getLocationInfo(String location) throws Exception {
        String username = PropertiesLoader.getProperty("geonames.username");

        String encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8);
        String url = "http://api.geonames.org/searchJSON?q=" + encodedLocation
                + "&maxRows=1&username=" + username;

        String response = HttpUtil.sendGetRequest(url);

        JsonNode root = objectMapper.readTree(response);
        JsonNode geonames = root.get("geonames");

        if (geonames == null || !geonames.isArray() || geonames.isEmpty()) {
            return "{\"message\":\"No location info found\"}";
        }

        JsonNode place = geonames.get(0);

        String result = "{"
                + "\"name\":\"" + safeText(place, "name") + "\","
                + "\"countryName\":\"" + safeText(place, "countryName") + "\","
                + "\"adminName1\":\"" + safeText(place, "adminName1") + "\","
                + "\"lat\":\"" + safeText(place, "lat") + "\","
                + "\"lng\":\"" + safeText(place, "lng") + "\""
                + "}";

        return result;
    }
    
    public String getNearbyLocationsForEvent(String postalCode, String country) throws Exception {
        String username = PropertiesLoader.getProperty("geonames.username");

        String url = "http://api.geonames.org/findNearbyPostalCodesJSON?postalcode="
                + postalCode
                + "&country=" + country
                + "&radius=5"
                + "&maxRows=2"
                + "&username=" + username;

        return HttpUtil.sendGetRequest(url);
    }

    public String getWeatherByCoordinates(double latitude, double longitude, String displayLocation) throws Exception {
        String openWeatherApiKey = PropertiesLoader.getProperty("openweather.apiKey");

        String weatherUrl = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude
                + "&lon=" + longitude
                + "&appid=" + openWeatherApiKey
                + "&units=metric";

        String weatherResponse = HttpUtil.sendGetRequest(weatherUrl);

        JsonNode weatherRoot = objectMapper.readTree(weatherResponse);

        JsonNode weatherArray = weatherRoot.get("weather");
        JsonNode main = weatherRoot.get("main");
        JsonNode wind = weatherRoot.get("wind");

        String description = "";
        if (weatherArray != null && weatherArray.isArray() && !weatherArray.isEmpty()) {
            description = safeText(weatherArray.get(0), "description");
        }

        String result = "{"
                + "\"location\":\"" + displayLocation + "\","
                + "\"temperature\":\"" + safeNumber(main, "temp") + "\","
                + "\"feelsLike\":\"" + safeNumber(main, "feels_like") + "\","
                + "\"humidity\":\"" + safeNumber(main, "humidity") + "\","
                + "\"windSpeed\":\"" + safeNumber(wind, "speed") + "\","
                + "\"description\":\"" + description + "\""
                + "}";

        return result;
    }
    
    // Adding a function: Will get the suggestions while setting an event loaction
    public String searchLocationSuggestions(String query) throws Exception {
        String apiKey = PropertiesLoader.getProperty("locationiq.apiKey");

        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);

        String url = "https://us1.locationiq.com/v1/autocomplete"
                + "?key=" + apiKey
                + "&q=" + encodedQuery
                + "&limit=5"
                + "&normalizecity=1"
                + "&format=json";

        return HttpUtil.sendGetRequest(url);
    }

    // Adding a function: Will get an image (static map preview)
    public String getStaticMapPreview(double latitude, double longitude) throws Exception {
        String apiKey = PropertiesLoader.getProperty("locationiq.apiKey");

        return "https://maps.locationiq.com/v3/staticmap"
                + "?key=" + apiKey
                + "&center=" + latitude + "," + longitude
                + "&zoom=14"
                + "&size=900x280"
                + "&format=png"
                + "&maptype=streets"
                + "&markers=icon:large-red-cutout|" + latitude + "," + longitude;
    }

    private String safeText(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value != null ? value.asText().replace("\"", "\\\"") : "";
    }

    private String safeNumber(JsonNode node, String field) {
        if (node == null) {
            return "";
        }
        JsonNode value = node.get(field);
        return value != null ? value.asText() : "";
    }
}