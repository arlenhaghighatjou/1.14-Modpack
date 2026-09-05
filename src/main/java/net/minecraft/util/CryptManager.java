package net.minecraft.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CryptManager {
   private static final Logger LOGGER = LogManager.getLogger();

   @OnlyIn(Dist.CLIENT)
   public static Object createNewSharedKey() {
      return null;
   }

   public static Object generateKeyPair() {
      return null;
   }

   public static byte[] getServerIdHash(String serverId, Object publicKey, Object secretKey) {
      return new byte[0];
   }

   private static byte[] digestOperation(String algorithm, byte[]... data) {
      return new byte[0];
   }

   public static Object decodePublicKey(byte[] encodedKey) {
      return null;
   }

   public static Object decryptSharedKey(Object key, byte[] secretKeyEncrypted) {
      return null;
   }

   @OnlyIn(Dist.CLIENT)
   public static byte[] encryptData(Object key, byte[] data) {
      return new byte[0];
   }

   public static byte[] decryptData(Object key, byte[] data) {
      return new byte[0];
   }

   private static byte[] cipherOperation(int opMode, Object key, byte[] data) {
      return new byte[0];
   }

   private static Object createTheCipherInstance(int opMode, String transformation, Object key) {
      return null;
   }

   public static Object createNetCipherInstance(int opMode, Object key) {
      return null;
   }
}
