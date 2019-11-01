package com.gstudios.thandhi.translator

import android.util.Log
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions

object OnDeviceTranslator {

    fun translateNow(
        input: String,
        source: Int,
        target: Int,
        onComplete: (output: String, status: String) -> Unit
    ) {
        val options = FirebaseTranslatorOptions.Builder()
            .setSourceLanguage(source)
            .setTargetLanguage(target)
            .build()
        val translator = FirebaseNaturalLanguage.getInstance().getTranslator(options)
        translator.downloadModelIfNeeded().addOnFailureListener {
            onComplete(input, "Download Failed")
        }
        translator.translate(input)
            .addOnSuccessListener { translatedText ->
                onComplete(translatedText, "Success")
            }
    }

    fun detectLanguage(input: String, onComplete: (language: Int, status: Boolean) -> Unit) {
        val languageIdentifier = FirebaseNaturalLanguage.getInstance().languageIdentification
        languageIdentifier.identifyLanguage(input)
            .addOnSuccessListener { languageCode ->
                if (languageCode != "und") {
                    val languageNumericCode =
                        FirebaseTranslateLanguage.languageForLanguageCode(languageCode)!!
                    onComplete(languageNumericCode, true)
                }
            }
            .addOnFailureListener {
                Log.e("Translation Err", it.message.toString())
                onComplete(0, false)
            }
    }
}