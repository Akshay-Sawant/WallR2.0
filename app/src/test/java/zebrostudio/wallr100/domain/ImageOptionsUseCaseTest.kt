package zebrostudio.wallr100.domain

import android.graphics.Bitmap
import android.net.Uri
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.ImageOptionsInteractor
import zebrostudio.wallr100.domain.interactor.ImageOptionsUseCase
import zebrostudio.wallr100.domain.model.imagedownload.ImageDownloadModel
import zebrostudio.wallr100.rules.TrampolineSchedulerRule
import java.util.UUID.*

@RunWith(MockitoJUnitRunner::class)
class ImageOptionsUseCaseTest {

  @get:Rule var trampolineSchedulerRule = TrampolineSchedulerRule()

  @Mock private lateinit var postExecutionThread: PostExecutionThread
  @Mock private lateinit var wallrRepository: WallrRepository
  @Mock private lateinit var mockBitmap: Bitmap
  @Mock private lateinit var mockUri: Uri
  private lateinit var imageOptionsUseCase: ImageOptionsUseCase
  private var randomString = randomUUID().toString()
  private var downloadCompleteValue: Long = 100

  @Before
  fun setup() {
    imageOptionsUseCase = ImageOptionsInteractor(wallrRepository, postExecutionThread)
    stubPostExecutionThreadReturnsIoScheduler()
  }

  @Test fun `should return imageDownloadModel on getImageBitmap call success`() {
    val expectedImageModel = ImageDownloadModel(downloadCompleteValue, mockBitmap)
    `when`(wallrRepository.getImageBitmap(randomString))
        .thenReturn(Observable.just(expectedImageModel))

    val imageModel = imageOptionsUseCase.fetchImageBitmapObservable(randomString).test().values()[0]

    assertEquals(expectedImageModel, imageModel)
    verify(wallrRepository).getImageBitmap(randomString)
    verifyNoMoreInteractions(wallrRepository)
  }

  @Test fun `should return single of shareable link on getShareableImageLink call success`() {
    `when`(wallrRepository.getShortImageLink(randomString)).thenReturn(
        Single.just(randomString))
    imageOptionsUseCase.getImageShareableLinkSingle(randomString)

    verify(wallrRepository).getShortImageLink(randomString)
    verifyNoMoreInteractions(wallrRepository)
    shouldVerifyPostExecutionThreadSchedulerCall()
  }

  @Test fun `should return completable on clearImageCaches call success`() {
    `when`(wallrRepository.clearImageCaches()).thenReturn(Completable.complete())

    imageOptionsUseCase.clearCachesCompletable().test().assertComplete()

    verify(wallrRepository).clearImageCaches()
    verifyNoMoreInteractions(wallrRepository)
    shouldVerifyPostExecutionThreadSchedulerCall()
  }

  @Test fun `should call cancelImageBitmapFetchingOperation on canImageFetching call success`() {
    imageOptionsUseCase.cancelFetchImageOperation()

    verify(wallrRepository).cancelImageBitmapFetchOperation()
    verifyNoMoreInteractions(wallrRepository)
  }

  @Test fun `should return uri on getCroppingSourceUri call success`() {
    `when`(wallrRepository.getCacheSourceUri()).thenReturn(mockUri)

    val uri = imageOptionsUseCase.getCroppingSourceUri()

    assertEquals(mockUri, uri)
    verify(wallrRepository).getCacheSourceUri()
    verifyNoMoreInteractions(wallrRepository)
  }

  @Test fun `should return uri on getCroppingDestinationUri call success`() {
    `when`(wallrRepository.getCacheResultUri()).thenReturn(mockUri)

    val uri = imageOptionsUseCase.getCroppingDestinationUri()

    assertEquals(mockUri, uri)
    verify(wallrRepository).getCacheResultUri()
    verifyNoMoreInteractions(wallrRepository)
  }

  @Test fun `should return Single of bitmap on getBitmapFromUriSingle call success`() {
    `when`(wallrRepository.getBitmapFromUri(mockUri)).thenReturn(Single.just(mockBitmap))

    imageOptionsUseCase.getBitmapFromUriSingle(mockUri).test()
        .assertValue(mockBitmap)

    verify(wallrRepository).getBitmapFromUri(mockUri)
    verifyNoMoreInteractions(wallrRepository)
  }

  private fun stubPostExecutionThreadReturnsIoScheduler() {
    whenever(postExecutionThread.scheduler).thenReturn(Schedulers.trampoline())
  }

  private fun shouldVerifyPostExecutionThreadSchedulerCall() {
    verify(postExecutionThread).scheduler
    verifyNoMoreInteractions(postExecutionThread)
  }

}