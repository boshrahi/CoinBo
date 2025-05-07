@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.multiplatform.coinbo.core.network

import platform.Foundation.NSBundle
import platform.Foundation.NSDictionary
import platform.Foundation.dictionaryWithContentsOfFile

actual object ApiKeys {
  actual val COIN_RANKING_API_KEY: String
    get() = getStringResource(
      filename = "ApiKeys",
      fileType = "plist",
      valueKey = "COIN_RANKING_API_KEY",
    ) ?: ""
}

internal fun getStringResource(
  filename: String,
  fileType: String,
  valueKey: String,
): String? {
  val result = NSBundle.mainBundle.pathForResource(filename, fileType)?.let {
    val map = NSDictionary.dictionaryWithContentsOfFile(it)
    map?.get(valueKey) as? String
  }
  return result
}
