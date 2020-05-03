package com.pi.gae.community.parade.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseManager {
  private static FirebaseManager instance;

  public static FirebaseManager createInstance(String databaseUrl) {
    instance = new FirebaseManager(databaseUrl);
    return instance;
  }

  public static FirebaseManager getInstance() {
    if (instance == null) {
      throw new RuntimeException("Firebase not initialized");
    }
    return instance;
  }


  private FirebaseDatabase database;
  private FirebaseApp firebaseApp;

  private FirebaseManager(String databaseUrl) {
    try {
      FirebaseOptions options =
          new FirebaseOptions.Builder().setCredentials(GoogleCredentials.getApplicationDefault())
              .setDatabaseUrl(databaseUrl).build();
      firebaseApp = FirebaseApp.initializeApp(options);
      database = FirebaseDatabase.getInstance(firebaseApp);
    } catch (Exception e) {
      if (e instanceof RuntimeException) {
        throw (RuntimeException) e;
      } else {
        throw new RuntimeException(e);
      }
    }
  }

  public String createJwt(String userId) throws FirebaseAuthException {
    String customToken = FirebaseAuth.getInstance(firebaseApp).createCustomToken(userId);
    return customToken;
  }

  public FirebaseDatabase getDatabase() {
    return database;
  }

  public DatabaseReference getDatabaseRoot() {
    return database.getReference(getFirebaseRoot());
  }

  public String getFirebaseRoot() {
    String root = System.getenv("firebase-data-root");
    if (root == null) {
      root = "production";
    }

    return root;
  }

  public void set(String path, Object value) throws Exception {
    DatabaseReference ref = getDatabaseRoot().child(path);

    ApiFuture<Void> future = ref.setValueAsync(value);


    future.get(5, TimeUnit.SECONDS);
  }


  public <T> T value(String path, Class<T> clazz) throws Exception {
    DatabaseReference ref = getDatabaseRoot().child(path);

    CompletableFuture<T> future = new CompletableFuture<>();


    ref.addListenerForSingleValueEvent(new ValueEventListener() {

      @SuppressWarnings("unchecked")
      @Override
      public void onDataChange(DataSnapshot snapshot) {
        try {
          T value = snapshot.getValue(clazz);
          future.complete(value);
        } catch (Exception ignored) {

          try {
            future.complete((T) snapshot.getValue());
          } catch (Exception e) {
            future.completeExceptionally(e);
          }
        }
      }

      @Override
      public void onCancelled(DatabaseError error) {
        future.completeExceptionally(new RuntimeException(error.getMessage()));
      }
    });


    T result = future.get(5, TimeUnit.SECONDS);
    return result;

  }


}
