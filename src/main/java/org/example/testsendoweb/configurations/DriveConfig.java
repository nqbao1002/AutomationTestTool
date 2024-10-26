//package org.example.testsendoweb.configurations;
//
//import com.google.api.client.auth.oauth2.Credential;
//import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
//import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
//import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
//import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
//import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
//import com.google.api.client.http.javanet.NetHttpTransport;
//import com.google.api.client.json.JsonFactory;
//import com.google.api.client.json.gson.GsonFactory;
//import com.google.api.client.json.jackson2.JacksonFactory;
//import com.google.api.client.util.store.FileDataStoreFactory;
//import com.google.api.services.drive.DriveScopes;
//import com.google.api.services.sheets.v4.Sheets;
//import com.google.api.services.sheets.v4.SheetsScopes;
//import com.google.api.services.sheets.v4.model.ValueRange;
//import org.springframework.stereotype.Service;
//
//import java.io.*;
//import java.security.GeneralSecurityException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//@Service
//public class DriveConfig {
//    private static final String APPLICATION_NAME = "Google Drive API Java";
//    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
//    private static final String TOKENS_DIRECTORY_PATH = "tokens";
//
//    // Include both Drive and Sheets scopes
//    private static final List<String> SCOPES = Arrays.asList(DriveScopes.DRIVE, SheetsScopes.SPREADSHEETS);
//    private static final String CREDENTIALS_FILE_PATH = "credentials.json";
//
//    // Helper method to create a Sheets service
//    public static Sheets getSheetService() throws IOException, GeneralSecurityException {
//        NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//        Credential credential = getCredentials(HTTP_TRANSPORT);
//        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
//                .setApplicationName(APPLICATION_NAME)
//                .build();
//    }
//
//    // Creates an authorized Credential object
//    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
//        InputStream in = DriveConfig.class.getClassLoader().getResourceAsStream(CREDENTIALS_FILE_PATH);
//        if (in == null) {
//            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
//        }
//        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
//
//        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
//                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
//                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
//                .setAccessType("offline")
//                .build();
//
//        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
//        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
//    }
//
//    // Upload CSV content to Google Sheets
//    public void uploadCSVToGoogleSheet(String spreadsheetId, String content) throws IOException, GeneralSecurityException {
//        Sheets sheetsService = getSheetService();
//        List<List<Object>> values = parseCSVToValues(content);
//        ValueRange body = new ValueRange().setValues(values);
//
//        // Use the correct method to append the data to the Google Sheet
//        sheetsService.spreadsheets().values()
//                .append(spreadsheetId, "Sheet1!A1", body) // Specify the range
//                .setValueInputOption("RAW")
//                .execute();
//    }
//
//    // Helper method to parse CSV content into a List of Lists
//    private List<List<Object>> parseCSVToValues(String csvContent) {
//        List<List<Object>> values = new ArrayList<>();
//        String[] rows = csvContent.split("\n");
//        for (String row : rows) {
//            List<Object> rowData = Arrays.asList(row.split(","));
//            values.add(rowData);
//        }
//        return values;
//    }
//}
