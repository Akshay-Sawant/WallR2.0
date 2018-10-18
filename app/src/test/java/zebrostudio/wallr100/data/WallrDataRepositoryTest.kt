package zebrostudio.wallr100.data

import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.data.api.RemoteAuthServiceFactory
import org.mockito.Mock
import org.mockito.Mockito.`when`
import zebrostudio.wallr100.data.api.UnsplashClientFactory
import zebrostudio.wallr100.data.api.UrlMap
import zebrostudio.wallr100.data.datafactory.UnsplashPictureEntityModelFactory
import zebrostudio.wallr100.data.exception.InvalidPurchaseException
import zebrostudio.wallr100.data.exception.NoResultFoundException
import zebrostudio.wallr100.data.exception.UnableToResolveHostException
import zebrostudio.wallr100.data.exception.UnableToVerifyPurchaseException
import zebrostudio.wallr100.data.mapper.PictureEntityMapper
import zebrostudio.wallr100.data.model.PurchaseAuthResponseEntity
import zebrostudio.wallr100.rules.TrampolineSchedulerRule
import java.lang.Exception
import java.util.UUID.*

@RunWith(MockitoJUnitRunner::class)
class WallrDataRepositoryTest {

  @get:Rule val trampolineSchedulerRule = TrampolineSchedulerRule()

  @Mock lateinit var sharedPrefs: SharedPrefsHelper
  @Mock lateinit var remoteAuthServiceFactory: RemoteAuthServiceFactory
  @Mock lateinit var unsplashClientFactory: UnsplashClientFactory
  private lateinit var pictureEntityMapper: PictureEntityMapper
  private lateinit var wallrDataRepository: WallrDataRepository
  private val dummyString = randomUUID().toString()
  private val dummyInt = 500 // to force some error other than 403 or 404
  private val unableToResolveHostExceptionMessage = "Unable to resolve host " +
      "\"api.unsplash.com\": No address associated with hostname"
  private val purchasePreferenceName = "PURCHASE_PREF"
  private val premiumUserTag = "premium_user"

  @Before fun setup() {
    pictureEntityMapper = PictureEntityMapper()
    wallrDataRepository =
        WallrDataRepository(remoteAuthServiceFactory, unsplashClientFactory, sharedPrefs,
            pictureEntityMapper)
  }

  @Test fun `should return single on server success response`() {
    `when`(remoteAuthServiceFactory.verifyPurchaseService(
        UrlMap.getFirebasePurchaseAuthEndpoint(dummyString, dummyString, dummyString)))
        .thenReturn(Single.just(PurchaseAuthResponseEntity("success", dummyInt, dummyString)))

    wallrDataRepository.authenticatePurchase(dummyString, dummyString, dummyString)
        .test()
        .assertComplete()
  }

  @Test fun `should return invalid purchase exception on server 403 error response`() {
    `when`(remoteAuthServiceFactory.verifyPurchaseService(
        UrlMap.getFirebasePurchaseAuthEndpoint(dummyString, dummyString, dummyString)))
        .thenReturn(Single.just(PurchaseAuthResponseEntity("error", 403, dummyString)))

    wallrDataRepository.authenticatePurchase(dummyString, dummyString, dummyString)
        .test()
        .assertError(InvalidPurchaseException::class.java)
  }

  @Test fun `should return invalid purchase exception on server 404 error response`() {
    `when`(remoteAuthServiceFactory.verifyPurchaseService(
        UrlMap.getFirebasePurchaseAuthEndpoint(dummyString, dummyString, dummyString)))
        .thenReturn(Single.just(PurchaseAuthResponseEntity("error", 404, dummyString)))

    wallrDataRepository.authenticatePurchase(dummyString, dummyString, dummyString)
        .test()
        .assertError(InvalidPurchaseException::class.java)
  }

  @Test fun `should return unable to verify purchase exception on some error during call`() {
    `when`(remoteAuthServiceFactory.verifyPurchaseService(
        UrlMap.getFirebasePurchaseAuthEndpoint(dummyString, dummyString, dummyString)))
        .thenReturn(Single.just(PurchaseAuthResponseEntity(dummyString, dummyInt, dummyString)))

    wallrDataRepository.authenticatePurchase(dummyString, dummyString, dummyString)
        .test()
        .assertError(UnableToVerifyPurchaseException::class.java)
  }

  @Test fun `should return true after successfully updating purchase status`() {
    `when`(sharedPrefs.setBoolean(purchasePreferenceName,
        premiumUserTag, true)).thenReturn(true)

    assertEquals(true, wallrDataRepository.updateUserPurchaseStatus())
  }

  @Test fun `should return false after unsuccessful update of purchase status`() {
    `when`(sharedPrefs.setBoolean(purchasePreferenceName,
        premiumUserTag, true)).thenReturn(false)

    assertEquals(false, wallrDataRepository.updateUserPurchaseStatus())
  }

  @Test fun `should return true after checking if user is premium user`() {
    `when`(sharedPrefs.getBoolean(purchasePreferenceName,
        premiumUserTag, false)).thenReturn(true)

    assertEquals(true, wallrDataRepository.isUserPremium())
  }

  @Test fun `should return false after checking if user is premium user`() {
    `when`(sharedPrefs.getBoolean(purchasePreferenceName,
        premiumUserTag, false)).thenReturn(false)

    assertEquals(false, wallrDataRepository.isUserPremium())
  }

  @Test fun `should return no result found exception on get pictures call`() {
    `when`(unsplashClientFactory.getPicturesService(dummyString)).thenReturn(
        Single.just(emptyList()))

    wallrDataRepository.getPictures(dummyString)
        .test()
        .assertError(NoResultFoundException::class.java)
  }

  @Test fun `should return unable to resolve host exception on get pictures call`() {
    `when`(unsplashClientFactory.getPicturesService(dummyString)).thenReturn(
        Single.error(Exception(unableToResolveHostExceptionMessage)))

    wallrDataRepository.getPictures(dummyString)
        .test()
        .assertError(UnableToResolveHostException::class.java)
  }

  @Test fun `should return mapped search pictures model list on get pictures call`() {
    val unsplashPicturesEntityList = mutableListOf(
        UnsplashPictureEntityModelFactory.getUnsplashPictureEntityModel())

    val searchPicturesModelList = pictureEntityMapper
        .mapFromEntity(unsplashPicturesEntityList)

    `when`(unsplashClientFactory.getPicturesService(dummyString)).thenReturn(
        Single.just(unsplashPicturesEntityList))

    val picture = wallrDataRepository.getPictures(dummyString)
        .test()
        .values()[0][0]

    assertTrue(searchPicturesModelList[0].id == picture.id)
  }

}