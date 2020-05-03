package com.pi.gae.community.parade.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import com.pi.gae.community.parade.models.UserToken;

public class ProvidenceCrypto {
  private static final String HASH_CHARS = "0123456789abcdef";
  private static final int SALT_LENGTH = 16;

  public static String createUserId(String username) {
    String salt = ServerKeys.getInstance().getSalt();

    String id = salt + "|" + username.toLowerCase();
    String userId = ProvidenceCrypto.digest(id);

    return userId;
  }

  public static String digest(String data) {
    return digest(data.getBytes(StandardCharsets.UTF_8));
  }

  public static String digest(byte[] data) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");

      md.update(data);

      byte[] crc = md.digest();
      StringBuilder checksum = new StringBuilder(crc.length * 2);

      for (byte element : crc) {
        checksum.append(Integer.toHexString(0xFF & element));
      }

      return checksum.toString();
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static String encryptPassword(String password) {
    String salt = "";
    SecureRandom random = new SecureRandom();
    for (int i = 0; i < SALT_LENGTH; i++) {
      salt += HASH_CHARS.charAt(random.nextInt(HASH_CHARS.length()));
    }

    String encrypted = digest(salt + ServerKeys.getInstance().getSalt() + password);
    return salt + encrypted;
  }

  public static UserToken generateUserToken(String firstName, String lastName, String userId) {
    UserToken token = new UserToken();
    token.setFirstName(firstName);
    token.setLastName(lastName);
    token.setUserId(userId);
    token.setSignature(hash(ServerKeys.getInstance().getUser(), userId, firstName, lastName));

    return token;
  }

  public static boolean validatePassword(String password, String expected) {
    String salt = expected.substring(0, SALT_LENGTH);
    String encrypted = expected.substring(SALT_LENGTH);

    String hash = digest(salt + ServerKeys.getInstance().getSalt() + password);

    return encrypted.equals(hash);
  }

  public static boolean validateUserToken(UserToken userToken) {
    boolean valid = false;
    valid = validate(ServerKeys.getInstance().getUser(), userToken.getSignature(),
        userToken.getUserId(), userToken.getFirstName(), userToken.getLastName());
    return valid;
  }

  private static String hash(String key, String... message) {
    try {
      StringBuilder buf = new StringBuilder();
      for (String msg : message) {
        if (buf.length() > 0) {
          buf.append('|');
        }
        buf.append(msg);
      }

      Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
      SecretKeySpec secret_key =
          new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
      sha256_HMAC.init(secret_key);

      String hash = Base64
          .encodeBase64String(sha256_HMAC.doFinal(buf.toString().getBytes(StandardCharsets.UTF_8)));

      return hash;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static boolean validate(String key, String signature, String... messages) {
    boolean valid = false;

    try {
      StringBuilder buf = new StringBuilder();
      for (String msg : messages) {
        if (buf.length() > 0) {
          buf.append('|');
        }
        buf.append(msg);
      }

      valid = hash(key, buf.toString()).equals(signature);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return valid;
  }
}
