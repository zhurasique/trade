package com.example.trade;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootApplication
public class TradeApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(TradeApplication.class, args);
	}

	public void list(TextMessage message){
		JsonObject convertedObject = new Gson().fromJson(message.getPayload(), JsonObject.class);
		JsonObject createdObject = new JsonObject();

		if(convertedObject.get("type").getAsString().equals("ticker")) {
			String instrument_tmp = convertedObject.get("product_id").getAsString();
			String instrument = instrument_tmp.replaceAll("[-]","");

			double bid = convertedObject.get("best_bid").getAsDouble();
			double ask = convertedObject.get("best_ask").getAsDouble();
			double last = convertedObject.get("price").getAsDouble();

			// Parse time to format HH:MM:SS
			String time_tmp = convertedObject.get("time").getAsString();
			String time = "";
			Pattern pattern = Pattern.compile("T(\\d{2}:\\d{2}:\\d{2})\\.");
			Matcher matcher = pattern.matcher(time_tmp);
			while (matcher.find())
				time = matcher.group(1);

			createdObject.addProperty("instrument", instrument);
			createdObject.addProperty("bid", bid);
			createdObject.addProperty("ask", ask);
			createdObject.addProperty("last", last);
			createdObject.addProperty("time", time);

			System.out.println("Message Mapped: \n" + createdObject + "\n");

			// Sending POST request to save instrument
			postRequest(instrument, bid, ask, last, time);
		}
	}

	public void postRequest(String instrument, double bid, double ask, double last, String time){
		try{
			URL url = new URL("http://localhost:8080");
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("instrument", instrument);
			params.put("bid", bid);
			params.put("ask", ask);
			params.put("last", last);
			params.put("time", time);


			StringBuilder postData = new StringBuilder();
			for (Map.Entry<String, Object> param : params.entrySet()) {
				if (postData.length() != 0) postData.append('&');
				postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
				postData.append('=');
				postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
			}
			byte[] postDataBytes = postData.toString().getBytes("UTF-8");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			conn.setDoOutput(true);
			conn.getOutputStream().write(postDataBytes);

			Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void run(String... args) {
		System.out.println("Connecting To: [wss://ws-feed.pro.coinbase.com]");
		WebSocketConnectionManager connectionManager = new WebSocketConnectionManager(new StandardWebSocketClient(), new GDAXWebSocketHandler(), "wss://ws-feed.pro.coinbase.com");
		connectionManager.start();
	}

	private class GDAXWebSocketHandler extends TextWebSocketHandler {

		@Override
		public void handleTextMessage(WebSocketSession session, TextMessage message) {
			System.out.println("\nMessage Received: \n" + message.getPayload());
			list(message);
		}

		@Override
		public void afterConnectionEstablished(WebSocketSession session) throws Exception {
			System.out.println("Connected");
			String payload = "{\n" +
					"    \"type\": \"subscribe\",\n" +
					"    \"product_ids\": [\n" +
					"        \"ETH-USD\",\n" +
					"        \"ETH-EUR\",\n" +
					"        \"BTC-USD\",\n" +
					"        \"BTC-EUR\"\n" +
					"    ],\n" +
					"    \"channels\": [\n" +
					"        \"ticker\"\n" +
					"    ]\n" +
					"}";
			System.out.println("Sending: \n" + payload + "\n");
			session.sendMessage(new TextMessage(payload));
		}

		@Override
		public void handleTransportError(WebSocketSession session, Throwable exception) {
			System.out.println("Transport Error: " + exception);
		}

		@Override
		public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
			System.out.println("Connection Closed: \n" + status.getReason() + "\n");
		}
	}
}
