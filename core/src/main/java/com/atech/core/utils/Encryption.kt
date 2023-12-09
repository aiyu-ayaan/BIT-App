package com.atech.core.utils

//import android.content.Context
//import android.util.Base64
//import com.kazakago.cryptore.CipherAlgorithm
//import com.kazakago.cryptore.Cryptore
//import com.kazakago.cryptore.DecryptResult
//import com.kazakago.cryptore.EncryptResult
//
//object Encryption {
//    @Throws(Exception::class)
//    fun Context.getCryptore(alias: String): Cryptore =
//        Cryptore.Builder(alias, CipherAlgorithm.RSA)
//            .also {
//                it.context = this
//            }.build()
//
//
//    @Throws(Exception::class)
//    fun Cryptore.encryptText(plainStr: String): String {
//        val plainByte = plainStr.toByteArray()
//        val result: EncryptResult = this.encrypt(plainByte)
//        return Base64.encodeToString(result.bytes, Base64.DEFAULT)
//    }
//
//    @Throws(Exception::class)
//    fun Cryptore.decryptText(encryptedStr: String?): String {
//        val encryptedByte: ByteArray = Base64.decode(encryptedStr, Base64.DEFAULT)
//        val result: DecryptResult = this.decrypt(encryptedByte, null)
//        return String(result.bytes)
//    }
//}