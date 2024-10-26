package org.example.testsendoweb.services;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Service
public class DriveService{
    private Drive googleDrive;
    /**
     * Application name.
     */
    private static final String APPLICATION_NAME = "Google Drive API Java";
    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    /**
     * Directory to store authorization tokens for this application.
     */
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES =
            Collections.singletonList(DriveScopes.DRIVE);

    private static final String CREDENTIALS_FILE_PATH = "./src/main/java/org/example/testsendoweb/credentials.json";

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    public static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        // Load client secrets from the resources folder.
        InputStream in = DriveService.class.getClassLoader().getResourceAsStream("credentials.json");
        if (in == null) {
            throw new FileNotFoundException("Resource not found: credentials.json");
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }


    public List<File> getAllGoogleDriveFiles() throws IOException {
        FileList result = googleDrive.files().list()
                .setFields("nextPageToken, files(id, name, parents, mimeType)")
                .execute();
        return result.getFiles();
    }

    public void uploadGoogleSheetToDrive(String csvContent) throws IOException, GeneralSecurityException {
        // Set up Google Drive API client
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        // Find shared folder
        FileList result = service.files().list()
                .setQ("sharedWithMe = true and mimeType = 'application/vnd.google-apps.folder'")
                .execute();

        String folderId = null;
        for (File folder : result.getFiles()) {
            System.out.printf("Found folder: %s (%s)\n", folder.getName(), folder.getId());
            folderId = folder.getId();  // Get the first shared folder ID
        }

        if (folderId == null) {
            throw new IOException("No shared folder found.");
        }

        // Create file metadata for the Google Sheet
        File fileMetadata = new File();
        fileMetadata.setName("Generative_AI_Sheet");
        fileMetadata.setMimeType("application/vnd.google-apps.spreadsheet");  // Set mime type to Google Sheet
        fileMetadata.setParents(Collections.singletonList(folderId));  // Set the parent to the shared folder

        // Create file content from string (this remains in CSV format initially)
        InputStream csvInputStream = new ByteArrayInputStream(csvContent.getBytes());
        AbstractInputStreamContent mediaContent = new InputStreamContent("text/csv", csvInputStream);

        // Upload file to Google Drive as a Google Sheet
        File file = service.files().create(fileMetadata, mediaContent)
                .setFields("id, parents")
                .execute();

        System.out.println("Google Sheet uploaded. File ID: " + file.getId());
    }


}