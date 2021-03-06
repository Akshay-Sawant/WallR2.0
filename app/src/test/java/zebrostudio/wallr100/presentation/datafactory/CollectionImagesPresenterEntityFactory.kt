package zebrostudio.wallr100.presentation.datafactory

import zebrostudio.wallr100.presentation.collection.Model.CollectionsPresenterEntity
import java.util.*

object CollectionImagesPresenterEntityFactory {
  fun getCollectionImagesPresenterEntity() =
      CollectionsPresenterEntity(
        Random().nextLong(),
        UUID.randomUUID().toString(),
        UUID.randomUUID().toString(),
        UUID.randomUUID().toString(),
        Random().nextInt()
      )
}