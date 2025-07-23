package com.project.drinkly.util

import android.util.Base64
import com.project.drinkly.BuildConfig
import java.nio.charset.Charset
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

object EncryptManager {

    init {
        // BouncyCastle을 등록해야 PKCS7Padding이 지원됩니다
        Security.addProvider(BouncyCastleProvider())
    }

    fun encryptCardInfo(
        cardNo: String,
        expYear: String,
        expMonth: String,
        idNo: String,
        cardPw: String,
        secretKey: String = BuildConfig.NICE_PAYMENT_KEY
    ): String? {
        val plainText = "cardNo=$cardNo&expYear=$expYear&expMonth=$expMonth&idNo=$idNo&cardPw=$cardPw"
        return try {
            val encrypted = aesEncryptECB(plainText, secretKey)
            encrypted.joinToString("") { "%02x".format(it) } // Hex 인코딩
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun aesEncryptECB(plainText: String, key: String): ByteArray {
        val charset = Charset.forName("UTF-8")
        val keyBytes = key.toByteArray(charset).copyOf(16) // AES-128이므로 16바이트로 잘라냄

        val secretKey = SecretKeySpec(keyBytes, "AES")
        val cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)

        return cipher.doFinal(plainText.toByteArray(charset))
    }
}
