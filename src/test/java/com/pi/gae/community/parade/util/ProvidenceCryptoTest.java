package com.pi.gae.community.parade.util;

import org.junit.Assert;
import org.junit.Test;

public class ProvidenceCryptoTest {
  @Test
  public void testPassword() {
    String[] passwords = new String[] {"123456", "123456789", "qwerty", "password", "111111",
        "12345678", "abc123", "1234567", "password1", "12345", "1234567890", "123123", "000000",
        "iloveyou", "1234", "1q2w3e4r5t", "qwertyuiop", "123", "monkey", "dragon", "123456a",
        "654321", "123321", "666666", "1qaz2wsx", "myspace1", "121212", "homelesspa", "123qwe",
        "a123456", "123abc", "1q2w3e4r", "qwe123", "7777777", "qwerty123", "target123"};

    for (String password : passwords) {
      String encrypted = ProvidenceCrypto.encryptPassword(password);
      boolean valid = ProvidenceCrypto.validatePassword(password, encrypted);

      Assert.assertTrue(valid);
    }
  }
}
