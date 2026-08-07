package de.taymaerz.skyfox.feeder.core.api

import de.taymaerz.skyfox.common.http.HttpModule
import de.taymaerz.skyfox.common.serialization.SerializationModule
import org.junit.jupiter.api.BeforeEach
import testhelper.BaseTest
import testhelper.coroutine.TestDispatcherProvider

class FeederEndpointTest : BaseTest() {
    private lateinit var endpoint: FeederEndpoint

    @BeforeEach
    fun setup() {
        endpoint = FeederEndpoint(
            baseClient = HttpModule().baseHttpClient(),
            dispatcherProvider = TestDispatcherProvider(),
            jsonConverterFactory = HttpModule().jsonConverter(SerializationModule().json())
        )
    }

//    @Test
//    fun `de-serialization`() = runTest {
//        val testId = ""
//        val infos = endpoint.getFeedInfos(setOf(testId))
//        infos.entries.single().apply {
//            key shouldBe testId
//            value.beast shouldNotBe emptyList<FeedInfos.Beast>()
//            value.mlat shouldNotBe emptyList<FeedInfos.Mlat>()
//        }
//    }
}
