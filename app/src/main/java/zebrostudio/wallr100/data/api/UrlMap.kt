package zebrostudio.wallr100.data.api

object UrlMap {
  const val FIREBASE_PURCHASE_AUTH_URL = "https://us-central1-wallrproduction.cloudfunctions.net/"

  fun getFirebasePurchaseAuthEndpoint(
    packageName: String,
    skuId: String,
    purchaseToken: String
  ) = "purchaseVerification?packageName=$packageName&skuId=$skuId&purchaseToken=$purchaseToken"
}

