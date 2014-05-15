package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.model;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.ModelRunDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClientException;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.AbstractWebServiceClientIntegrationTests;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * Contains integration tests for the ModelRunRequester class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunRequesterIntegrationTest extends AbstractWebServiceClientIntegrationTests {
    @Autowired
    private ModelRunRequester modelRunRequester;
    @Autowired
    private ModelRunDao modelRunDao;

    private static final String URL = "http://username:password@localhost:8080/ModelWrapper_war_exploded/model/run";

    @Test
    public void requestModelRunSucceeds() {
        // Arrange
        DateTime now = DateTime.now();
        DateTimeUtils.setCurrentMillisFixed(now.getMillis());
        String modelName = "testname";
        String responseJson = "{\"modelRunName\":\"testname\"}";
        mockPostRequest(responseJson); // Note that this includes code to assert the request JSON

        // Act
        modelRunRequester.requestModelRun();

        // Assert
        List<ModelRun> modelRuns = modelRunDao.getAll();
        assertThat(modelRuns).hasSize(1);
        assertThat(modelRuns.get(0).getName()).isEqualTo(modelName);
        assertThat(modelRuns.get(0).getRequestDate()).isEqualTo(now);
    }

    @Test
    public void requestModelRunWithErrorReturnedByModel() {
        // Arrange
        String responseJson = "{\"errorText\":\"testerror\"}";
        String expectedLogMessage = "Error when requesting a model run: testerror";
        Logger logger = mockLogger();
        mockPostRequest(responseJson); // Note that this includes code to assert the request JSON

        // Act
        modelRunRequester.requestModelRun();

        // Assert
        verify(logger, times(1)).fatal(eq(expectedLogMessage));
    }

    @Test
    public void requestModelRunWithWebClientExceptionThrown() {
        // Arrange
        String exceptionMessage = "Web service failed";
        String expectedLogMessage = "Error when requesting a model run: " + exceptionMessage;
        Logger logger = mockLogger();
        WebServiceClientException thrownException = new WebServiceClientException(exceptionMessage);
        when(webServiceClient.makePostRequestWithJSON(eq(URL), anyString())).thenThrow(thrownException);

        // Act
        modelRunRequester.requestModelRun();

        // Assert
        verify(logger, times(1)).fatal(eq(expectedLogMessage), any(WebServiceClientException.class));
    }

    private void mockPostRequest(final String responseJson) {
        when(webServiceClient.makePostRequestWithJSON(eq(URL), anyString())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws IOException {
                String requestJson = (String) invocationOnMock.getArguments()[1];
                assertRequestJson(requestJson);
                return responseJson;
            }
        });
    }

    private Logger mockLogger() {
        Logger mockLogger = mock(Logger.class);
        try {
            Field field = ModelRunRequester.class.getDeclaredField("logger");
            field.setAccessible(true);
            field.set(modelRunRequester, mockLogger);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return mockLogger;
    }

    private void assertRequestJson(String requestJson) throws IOException {
        // Split the JSON into four groups: disease, feature collection header, features, extent
        Pattern regexp = Pattern.compile("\\{\"disease\"\\:\\{(.+?)},\"occurrences\"\\:\\{(.+?),\"features\"\\:\\[(.+?)\\]\\},\"extentWeightings\"\\:\\{(.+?)\\}\\}");
        Matcher matcher = regexp.matcher(requestJson);

        assertThat(matcher.find()).isTrue();
        assertThat(matcher.groupCount()).isEqualTo(4);
        assertThat(matcher.group(1)).isEqualTo("\"id\":87,\"name\":\"Dengue\",\"abbreviation\":\"deng\",\"global\":false");
        assertThat(matcher.group(2)).isEqualTo("\"type\":\"FeatureCollection\",\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"urn:ogc:def:crs:EPSG::4326\"}}");

        List<String> splitFeatures = getSplitFeatures(matcher.group(3));
        assertSplitFeatures(splitFeatures);

        List<String> splitExtent = getSplitExtent(matcher.group(4));
        assertSplitExtent(splitExtent);
    }

    private List<String> getSplitFeatures(String features) {
        // Each feature starts with the string {"type":"Feature"
        String[] splitFeatures = features.split("\\{\"type\"\\:\"Feature\",");

        // Reinstate the {"type":"Feature", at the start of the string, and remove trailing comma (if any)
        // Also ignore the first split feature because it should be empty
        String[] trimmedSplitFeatures = new String[splitFeatures.length - 1];
        for (int i = 1; i < splitFeatures.length; i++) {
            String splitFeature = splitFeatures[i];
            if (splitFeature.length() > 2) {
                trimmedSplitFeatures[i - 1] =
                        "{\"type\":\"Feature\"," + StringUtils.trimTrailingCharacter(splitFeature, ',');
            }
        }

        return Arrays.asList(trimmedSplitFeatures);
    }

    private void assertSplitFeatures(List<String> splitFeatures) {
        assertThat(splitFeatures).hasSize(27);
        assertThat(splitFeatures).contains(
                "{\"type\":\"Feature\",\"id\":274016,\"geometry\":{\"type\":\"Point\",\"coordinates\":[-47.09179,-21.76979]},\"properties\":{\"locationPrecision\":\"PRECISE\",\"weighting\":0.3}}",
                "{\"type\":\"Feature\",\"id\":274444,\"geometry\":{\"type\":\"Point\",\"coordinates\":[121.06667,14.53333]},\"properties\":{\"locationPrecision\":\"PRECISE\",\"weighting\":0.8}}",
                "{\"type\":\"Feature\",\"id\":274763,\"geometry\":{\"type\":\"Point\",\"coordinates\":[-46.60972,-20.71889]},\"properties\":{\"locationPrecision\":\"PRECISE\",\"weighting\":0.5}}",
                "{\"type\":\"Feature\",\"id\":274776,\"geometry\":{\"type\":\"Point\",\"coordinates\":[-42.66564,-22.18996]},\"properties\":{\"locationPrecision\":\"ADMIN1\",\"weighting\":0.6,\"gaulCode\":683}}",
                "{\"type\":\"Feature\",\"id\":274779,\"geometry\":{\"type\":\"Point\",\"coordinates\":[-43.04112,-22.81555]},\"properties\":{\"locationPrecision\":\"ADMIN2\",\"weighting\":0.9,\"gaulCode\":9966}}",
                "{\"type\":\"Feature\",\"id\":274780,\"geometry\":{\"type\":\"Point\",\"coordinates\":[-42.91651,-22.17062]},\"properties\":{\"locationPrecision\":\"ADMIN2\",\"weighting\":0.6,\"gaulCode\":9970}}",
                "{\"type\":\"Feature\",\"id\":274788,\"geometry\":{\"type\":\"Point\",\"coordinates\":[-54.0,-30.0]},\"properties\":{\"locationPrecision\":\"ADMIN1\",\"weighting\":0.4,\"gaulCode\":685}}",
                "{\"type\":\"Feature\",\"id\":274790,\"geometry\":{\"type\":\"Point\",\"coordinates\":[-54.66252,-28.05186]},\"properties\":{\"locationPrecision\":\"ADMIN2\",\"weighting\":0.6,\"gaulCode\":10593}}",
                "{\"type\":\"Feature\",\"id\":275100,\"geometry\":{\"type\":\"Point\",\"coordinates\":[-67.81,-9.97472]},\"properties\":{\"locationPrecision\":\"PRECISE\",\"weighting\":0.4}}",
                "{\"type\":\"Feature\",\"id\":275104,\"geometry\":{\"type\":\"Point\",\"coordinates\":[73.85674,18.52043]},\"properties\":{\"locationPrecision\":\"PRECISE\",\"weighting\":0.9}}",
                "{\"type\":\"Feature\",\"id\":275107,\"geometry\":{\"type\":\"Point\",\"coordinates\":[-76.42313,8.84621]},\"properties\":{\"locationPrecision\":\"PRECISE\",\"weighting\":0.7}}",
                "{\"type\":\"Feature\",\"id\":275219,\"geometry\":{\"type\":\"Point\",\"coordinates\":[102.25616,2.20569]},\"properties\":{\"locationPrecision\":\"PRECISE\",\"weighting\":0.9}}",
                "{\"type\":\"Feature\",\"id\":275378,\"geometry\":{\"type\":\"Point\",\"coordinates\":[-45.88694,-23.17944]},\"properties\":{\"locationPrecision\":\"PRECISE\",\"weighting\":0.4}}",
                "{\"type\":\"Feature\",\"id\":275388,\"geometry\":{\"type\":\"Point\",\"coordinates\":[114.0,1.0]},\"properties\":{\"locationPrecision\":\"PRECISE\",\"weighting\":1.0}}",
                "{\"type\":\"Feature\",\"id\":275519,\"geometry\":{\"type\":\"Point\",\"coordinates\":[-49.06055,-22.31472]},\"properties\":{\"locationPrecision\":\"PRECISE\",\"weighting\":0.8}}",
                "{\"type\":\"Feature\",\"id\":275713,\"geometry\":{\"type\":\"Point\",\"coordinates\":[126.08934,7.30416]},\"properties\":{\"locationPrecision\":\"ADMIN1\",\"weighting\":0.5,\"gaulCode\":67161}}",
                "{\"type\":\"Feature\",\"id\":275715,\"geometry\":{\"type\":\"Point\",\"coordinates\":[126.17626,7.51252]},\"properties\":{\"locationPrecision\":\"PRECISE\",\"weighting\":0.9}}",
                "{\"type\":\"Feature\",\"id\":275716,\"geometry\":{\"type\":\"Point\",\"coordinates\":[126.33333,7.16667]},\"properties\":{\"locationPrecision\":\"ADMIN2\",\"weighting\":0.7,\"gaulCode\":24269}}",
                "{\"type\":\"Feature\",\"id\":275717,\"geometry\":{\"type\":\"Point\",\"coordinates\":[126.0,7.5]},\"properties\":{\"locationPrecision\":\"ADMIN2\",\"weighting\":0.3,\"gaulCode\":24266}}",
                "{\"type\":\"Feature\",\"id\":275748,\"geometry\":{\"type\":\"Point\",\"coordinates\":[-98.28333,26.08333]},\"properties\":{\"locationPrecision\":\"PRECISE\",\"weighting\":0.6}}",
                "{\"type\":\"Feature\",\"id\":275751,\"geometry\":{\"type\":\"Point\",\"coordinates\":[103.80805,1.29162]},\"properties\":{\"locationPrecision\":\"PRECISE\",\"weighting\":0.5}}",
                "{\"type\":\"Feature\",\"id\":275768,\"geometry\":{\"type\":\"Point\",\"coordinates\":[39.21917,21.51694]},\"properties\":{\"locationPrecision\":\"PRECISE\",\"weighting\":0.9}}",
                "{\"type\":\"Feature\",\"id\":275788,\"geometry\":{\"type\":\"Point\",\"coordinates\":[-51.38889,-22.12556]},\"properties\":{\"locationPrecision\":\"PRECISE\",\"weighting\":0.6}}",
                "{\"type\":\"Feature\",\"id\":275799,\"geometry\":{\"type\":\"Point\",\"coordinates\":[-61.5,-17.5]},\"properties\":{\"locationPrecision\":\"ADMIN1\",\"weighting\":0.5,\"gaulCode\":40449}}",
                "{\"type\":\"Feature\",\"id\":275801,\"geometry\":{\"type\":\"Point\",\"coordinates\":[177.46666,-17.61667]},\"properties\":{\"locationPrecision\":\"PRECISE\",\"weighting\":0.3}}",
                "{\"type\":\"Feature\",\"id\":275802,\"geometry\":{\"type\":\"Point\",\"coordinates\":[177.41667,-17.8]},\"properties\":{\"locationPrecision\":\"PRECISE\",\"weighting\":0.7}}",
                "{\"type\":\"Feature\",\"id\":275845,\"geometry\":{\"type\":\"Point\",\"coordinates\":[-80.63333,-5.2]},\"properties\":{\"locationPrecision\":\"PRECISE\",\"weighting\":0.5}}"
        );
    }

    private List<String> getSplitExtent(String extent) {
        return Arrays.asList(extent.split(","));
    }

    ///CHECKSTYLE:OFF MethodLength - contains test data
    private void assertSplitExtent(List<String> splitExtent) {
        assertThat(splitExtent).hasSize(459);
        assertThat(splitExtent).contains(
                "\"2\":0",
                "\"3\":0",
                "\"4\":0",
                "\"5\":0",
                "\"6\":0",
                "\"7\":0",
                "\"8\":0",
                "\"9\":0",
                "\"11\":0",
                "\"13\":0",
                "\"14\":0",
                "\"15\":0",
                "\"18\":0",
                "\"19\":0",
                "\"20\":0",
                "\"21\":0",
                "\"23\":0",
                "\"24\":0",
                "\"26\":0",
                "\"27\":0",
                "\"28\":0",
                "\"29\":0",
                "\"30\":0",
                "\"31\":0",
                "\"33\":0",
                "\"34\":0",
                "\"35\":0",
                "\"36\":0",
                "\"38\":0",
                "\"39\":0",
                "\"40\":0",
                "\"41\":0",
                "\"42\":0",
                "\"43\":0",
                "\"44\":0",
                "\"45\":0",
                "\"47\":0",
                "\"48\":0",
                "\"49\":0",
                "\"50\":0",
                "\"51\":0",
                "\"52\":0",
                "\"54\":0",
                "\"57\":50",
                "\"58\":0",
                "\"59\":0",
                "\"60\":50",
                "\"61\":0",
                "\"62\":0",
                "\"63\":0",
                "\"64\":0",
                "\"65\":0",
                "\"66\":0",
                "\"67\":0",
                "\"68\":0",
                "\"69\":0",
                "\"70\":0",
                "\"71\":0",
                "\"72\":0",
                "\"73\":0",
                "\"74\":0",
                "\"75\":0",
                "\"76\":0",
                "\"77\":0",
                "\"78\":0",
                "\"79\":0",
                "\"81\":0",
                "\"82\":0",
                "\"83\":50",
                "\"84\":0",
                "\"85\":0",
                "\"86\":0",
                "\"87\":0",
                "\"89\":0",
                "\"90\":0",
                "\"91\":0",
                "\"92\":0",
                "\"93\":0",
                "\"94\":0",
                "\"95\":0",
                "\"97\":0",
                "\"98\":0",
                "\"99\":0",
                "\"100\":0",
                "\"101\":0",
                "\"102\":0",
                "\"103\":0",
                "\"104\":0",
                "\"105\":0",
                "\"106\":0",
                "\"107\":0",
                "\"108\":0",
                "\"109\":0",
                "\"111\":0",
                "\"113\":0",
                "\"114\":0",
                "\"117\":0",
                "\"118\":0",
                "\"119\":0",
                "\"120\":0",
                "\"121\":0",
                "\"122\":0",
                "\"123\":0",
                "\"126\":0",
                "\"128\":0",
                "\"130\":0",
                "\"132\":0",
                "\"133\":0",
                "\"134\":0",
                "\"135\":0",
                "\"136\":0",
                "\"137\":0",
                "\"138\":0",
                "\"139\":0",
                "\"140\":0",
                "\"141\":0",
                "\"142\":0",
                "\"144\":0",
                "\"145\":0",
                "\"146\":0",
                "\"147\":0",
                "\"148\":0",
                "\"150\":0",
                "\"151\":0",
                "\"152\":0",
                "\"153\":100",
                "\"155\":0",
                "\"156\":0",
                "\"158\":0",
                "\"159\":0",
                "\"160\":0",
                "\"161\":0",
                "\"163\":0",
                "\"165\":0",
                "\"167\":0",
                "\"168\":0",
                "\"169\":0",
                "\"170\":0",
                "\"172\":0",
                "\"175\":0",
                "\"176\":0",
                "\"177\":0",
                "\"178\":0",
                "\"179\":0",
                "\"180\":0",
                "\"181\":0",
                "\"182\":0",
                "\"183\":0",
                "\"184\":0",
                "\"185\":0",
                "\"186\":0",
                "\"187\":0",
                "\"188\":50",
                "\"189\":0",
                "\"191\":0",
                "\"194\":0",
                "\"196\":50",
                "\"197\":0",
                "\"198\":0",
                "\"199\":0",
                "\"200\":0",
                "\"201\":0",
                "\"203\":0",
                "\"205\":0",
                "\"206\":0",
                "\"207\":0",
                "\"208\":0",
                "\"209\":0",
                "\"210\":0",
                "\"211\":0",
                "\"212\":0",
                "\"214\":0",
                "\"217\":0",
                "\"220\":0",
                "\"221\":0",
                "\"222\":0",
                "\"223\":0",
                "\"224\":0",
                "\"226\":0",
                "\"228\":0",
                "\"229\":0",
                "\"231\":0",
                "\"233\":0",
                "\"234\":0",
                "\"235\":0",
                "\"236\":0",
                "\"237\":0",
                "\"238\":0",
                "\"239\":0",
                "\"240\":50",
                "\"241\":0",
                "\"242\":0",
                "\"243\":0",
                "\"245\":0",
                "\"246\":0",
                "\"248\":0",
                "\"249\":0",
                "\"250\":0",
                "\"251\":0",
                "\"253\":0",
                "\"254\":0",
                "\"255\":0",
                "\"256\":0",
                "\"257\":0",
                "\"258\":0",
                "\"260\":0",
                "\"261\":0",
                "\"262\":0",
                "\"263\":0",
                "\"266\":0",
                "\"268\":0",
                "\"269\":0",
                "\"270\":0",
                "\"271\":0",
                "\"429\":0",
                "\"430\":0",
                "\"431\":0",
                "\"432\":0",
                "\"433\":0",
                "\"434\":0",
                "\"435\":0",
                "\"436\":0",
                "\"437\":0",
                "\"438\":0",
                "\"439\":0",
                "\"440\":0",
                "\"441\":0",
                "\"442\":0",
                "\"443\":0",
                "\"444\":0",
                "\"445\":0",
                "\"446\":0",
                "\"447\":0",
                "\"448\":0",
                "\"449\":0",
                "\"450\":0",
                "\"451\":0",
                "\"452\":0",
                "\"468\":0",
                "\"470\":0",
                "\"471\":0",
                "\"472\":0",
                "\"473\":0",
                "\"474\":0",
                "\"475\":0",
                "\"476\":0",
                "\"477\":0",
                "\"665\":0",
                "\"666\":0",
                "\"667\":0",
                "\"668\":0",
                "\"669\":0",
                "\"670\":0",
                "\"671\":0",
                "\"672\":0",
                "\"673\":0",
                "\"674\":0",
                "\"675\":0",
                "\"676\":0",
                "\"677\":0",
                "\"678\":0",
                "\"679\":0",
                "\"680\":0",
                "\"681\":0",
                "\"682\":0",
                "\"683\":50",
                "\"684\":0",
                "\"685\":50",
                "\"686\":0",
                "\"687\":0",
                "\"688\":0",
                "\"689\":50",
                "\"690\":0",
                "\"691\":0",
                "\"825\":0",
                "\"894\":0",
                "\"898\":0",
                "\"899\":0",
                "\"900\":0",
                "\"901\":0",
                "\"902\":0",
                "\"903\":0",
                "\"904\":0",
                "\"905\":0",
                "\"906\":0",
                "\"907\":0",
                "\"908\":0",
                "\"909\":0",
                "\"911\":0",
                "\"912\":0",
                "\"913\":0",
                "\"914\":0",
                "\"915\":0",
                "\"916\":0",
                "\"917\":0",
                "\"918\":0",
                "\"919\":0",
                "\"920\":0",
                "\"921\":0",
                "\"922\":0",
                "\"923\":0",
                "\"924\":0",
                "\"925\":0",
                "\"927\":0",
                "\"928\":0",
                "\"929\":0",
                "\"1143\":0",
                "\"1484\":0",
                "\"1485\":0",
                "\"1487\":0",
                "\"1489\":0",
                "\"1490\":0",
                "\"1491\":0",
                "\"1492\":0",
                "\"1493\":0",
                "\"1494\":0",
                "\"1495\":0",
                "\"1496\":0",
                "\"1498\":50",
                "\"1500\":0",
                "\"1501\":0",
                "\"1502\":0",
                "\"1503\":0",
                "\"1504\":0",
                "\"1505\":0",
                "\"1506\":0",
                "\"1507\":0",
                "\"1508\":0",
                "\"1509\":0",
                "\"1511\":0",
                "\"2509\":0",
                "\"2622\":0",
                "\"2623\":0",
                "\"2624\":0",
                "\"2625\":0",
                "\"2626\":0",
                "\"2627\":0",
                "\"2628\":0",
                "\"2629\":50",
                "\"2630\":0",
                "\"2631\":0",
                "\"2632\":0",
                "\"2633\":50",
                "\"2634\":0",
                "\"2647\":0",
                "\"2648\":0",
                "\"3214\":0",
                "\"3215\":0",
                "\"3216\":0",
                "\"3217\":0",
                "\"3218\":0",
                "\"3219\":0",
                "\"3220\":0",
                "\"3221\":0",
                "\"3222\":0",
                "\"3223\":0",
                "\"3224\":0",
                "\"3225\":0",
                "\"3226\":0",
                "\"3227\":0",
                "\"3228\":0",
                "\"3229\":0",
                "\"3230\":0",
                "\"3231\":0",
                "\"3232\":0",
                "\"3233\":0",
                "\"3234\":0",
                "\"3235\":0",
                "\"3236\":0",
                "\"3237\":0",
                "\"3238\":0",
                "\"3239\":0",
                "\"3240\":0",
                "\"3241\":0",
                "\"3242\":0",
                "\"3243\":0",
                "\"3244\":0",
                "\"3245\":0",
                "\"3246\":0",
                "\"3247\":0",
                "\"3248\":0",
                "\"3249\":0",
                "\"3250\":0",
                "\"3251\":0",
                "\"3252\":0",
                "\"3253\":0",
                "\"3254\":0",
                "\"3255\":0",
                "\"3256\":0",
                "\"3257\":0",
                "\"3258\":0",
                "\"3259\":0",
                "\"3260\":0",
                "\"3261\":0",
                "\"3262\":0",
                "\"3263\":0",
                "\"3264\":0",
                "\"33364\":0",
                "\"40760\":0",
                "\"40762\":0",
                "\"40765\":0",
                "\"61013\":0",
                "\"70073\":0",
                "\"70074\":0",
                "\"70075\":0",
                "\"70076\":0",
                "\"70077\":0",
                "\"70078\":0",
                "\"70079\":0",
                "\"70080\":0",
                "\"70081\":0",
                "\"70082\":0",
                "\"74578\":0",
                "\"204001\":0",
                "\"229010\":0",
                "\"1006344\":50",
                "\"1011440\":0",
                "\"1011441\":0",
                "\"1011442\":0",
                "\"1011443\":0",
                "\"1011444\":0",
                "\"1011446\":0",
                "\"1011447\":0",
                "\"1012935\":0",
                "\"1012936\":0",
                "\"1013663\":0",
                "\"1013664\":0",
                "\"1013665\":0",
                "\"1013666\":0",
                "\"1013667\":0",
                "\"1013668\":0",
                "\"1013669\":0",
                "\"1013670\":0",
                "\"1013671\":0",
                "\"1013672\":0",
                "\"1013673\":0",
                "\"1013674\":0",
                "\"1013675\":0",
                "\"1013676\":0",
                "\"1013677\":0",
                "\"1013678\":0",
                "\"1013679\":0",
                "\"1013680\":0",
                "\"1013681\":0",
                "\"1013682\":0",
                "\"1013683\":0",
                "\"1013684\":0",
                "\"1013685\":0",
                "\"1013686\":0",
                "\"1013687\":50",
                "\"1013688\":0",
                "\"1013689\":50",
                "\"1013690\":0",
                "\"1013691\":0",
                "\"1013692\":0",
                "\"1013693\":0",
                "\"1013694\":0",
                "\"1013695\":0",
                "\"1013965\":0"
        );
    }
    ///CHECKSTYLE:ON MethodLength
}
