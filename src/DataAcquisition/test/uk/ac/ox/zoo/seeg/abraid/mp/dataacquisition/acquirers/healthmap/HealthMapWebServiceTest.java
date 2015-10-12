package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap;

import org.joda.time.DateTime;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.JsonParserException;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClient;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClientException;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap.domain.HealthMapAlert;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap.domain.HealthMapLocation;

import java.util.List;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the HealthMapWebService class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapWebServiceTest {
    @Test
    public void successfulHealthMapCall() {
        // Arrange
        DateTime startDate = new DateTime("2014-01-06T10:01:02+0000");
        DateTime endDate = new DateTime("2014-01-07T13:10:59+0000");
        String startDateUrl = "2014-01-06%2010:01:02%2B0000";
        String endDateUrl = "2014-01-07%2013:10:59%2B0000";

        // Real location with 2 alerts
        String jsonLocation1 = "{" +
                "\"country\": \"China\"," +
                "\"place_name\": \"Shenzhen, Guangdong, China\"," +
                "\"lat\": \"22.545540\"," +
                "\"lng\": \"114.068298\"," +
                "\"geonameid\": \"1795565\"," +
                "\"place_basic_type\": \"p\"," +
                "\"place_id\": \"733\"," +
                "\"country_id\": \"155\"," +
                "\"alerts\": [" +
                // Alert 1
                "{" +
                "\"feed\": \"Food and Agriculture Org\"," +
                "\"reviewed\": \"\"," +
                "\"disease_ids\": [" +
                "\"271\"" +
                "]," +
                "\"diseases\": [" +
                "\"Avian Influenza H7N9\"" +
                "]," +
                "\"species_ids\": [" +
                "\"132\"" +
                "]," +
                "\"species\": [" +
                "\"Humans\"" +
                "]," +
                "\"disease\": \"Avian Influenza H7N9\"," +
                "\"summary\": \"Confirmed Influenza - Avian in Guangdong Sheng, China - human\"," +
                "\"date\": \"2014-01-07 00:00:00-0500\"," +
                "\"formatted_date\": \" 7 January 2014 00:00:00 EST\"," +
                "\"link\": \"http://healthmap.org/ln.php?2161280\"," +
                "\"descr\": \"\"," +
                "\"rating\": {" +
                "\"count\": \"1\"," +
                "\"rating\": \"4.0000\"" +
                "}," +
                "\"species_name\": \"Humans\"," +
                "\"dup_count\": \"0\"," +
                "\"summary_es\": \"Confirmado Influenza - aviar en Guangdong Sheng, China - humana\"," +
                "\"summary_fr\": \"Confirmé de grippe - Grippe dans le Guangdong Sheng, Chine - humaine\"," +
                "\"summary_ru\": \"Подтвержденным гриппом - Птичий в Guangdong Sheng, Китай - человеческий\"," +
                "\"summary_zh\": \"确认流感 - 禽流感在广东盛，中国 - 人类\"," +
                "\"summary_pt\": \"Influenza Confirmado - Aviária em Guangdong Sheng, China - humano\"," +
                "\"summary_ar\": \"أكد الأنفلونزا - الطيور فى قوانغدونغ شنغ، الصين - الإنسان\"," +
                "\"summary_vi\": \"Xác nhận cúm - cúm gia cầm ở Quảng Đông Sheng, Trung Quốc - con người\"," +
                "\"place_category\": []," +
                "\"original_url\": \"http://empres-i.fao.org/empres-i/2/obd?idOutbreak=182133&rss=t\"," +
                "\"disease_id\": \"271\"," +
                "\"feed_id\": \"10\"," +
                "\"feed_lang\": \"zh\"" +
                "}," +
                // Alert 2
                "{" +
                "\"feed\": \"Google News\"," +
                "\"reviewed\": \"\"," +
                "\"disease_ids\": [" +
                "\"271\"" +
                "]," +
                "\"diseases\": [" +
                "\"Avian Influenza H7N9\"" +
                "]," +
                "\"species_ids\": [" +
                "\"132\"" +
                "]," +
                "\"species\": [" +
                "\"Humans\"" +
                "]," +
                "\"disease\": \"Avian Influenza H7N9\"," +
                "\"summary\": \"Two New Cases Of Human H7N9 Infections Reported In China - RTT News\"," +
                "\"date\": \"2014-01-06 16:07:33-0500\"," +
                "\"formatted_date\": \" 6 January 2014 16:07:33 EST\"," +
                "\"link\": \"http://healthmap.org/ln.php?2155089\"," +
                "\"descr\": \"The United Nations World Health Organization (WHO) revealed Monday that China's " +
                "National Health and Family Planning Commission had notified it in mid-December of two new laboratory" +
                "-confirmed cases of human infection with avian influenza A(H7N9) \"," +
                "\"rating\": {" +
                "\"count\": 0," +
                "\"rating\": 4" +
                "}," +
                "\"species_name\": \"Humans\"," +
                "\"dup_count\": \"0\"," +
                "\"place_category\": []," +
                "\"original_url\": \"http://news.google.com/news/url?sa=t&fd=R&usg=AFQjCNFduJXlyPpcRmF2wzssn6vgeH6dIw&" +
                "url=http://www.rttnews.com/2247181/two-new-cases-of-human-h7n9-infections-reported-in-china.aspx?type" +
                "%3Dmsgn%26utm_source%3Dgoogle%26utm_campaign%3Dsitemap\"," +
                "\"disease_id\": \"271\"," +
                "\"feed_id\": \"4\"" +
                "}" +
                "]" +
                "}";

        // Location with a mixture of real and dummy data (2 alerts)
        String jsonLocation2 = "{" +
                "\"country\": null," +
                "\"place_name\": \"Lenvik, Troms, Norway\"," +
                "\"lng\": \"17.987391\"," +
                "\"geonameid\": \"undefined\"," +
                "\"place_basic_type\": \"\"," +
                "\"place_id\": null," +
                "\"country_id\": \"107\"," +
                "\"alerts\": [" +
                // Alert 1
                "{" +
                "\"feed\": \"HM Community News Reports\"," +
                "\"disease\": \"Meningitis\"," +
                "\"summary\": \"Konstaterte smittsom hjernehinnebetennelse på 2-åring -Folkebladet.no\"," +
                "\"link\": \"unknown\"," +
                "\"descr\": \"Dubøroppgraderenettleserendin\"," +
                "\"rating\": {" +
                "\"count\": 0," +
                "\"rating\": 3" +
                "}," +
                "\"species_name\": \"Humans\"," +
                "\"dup_count\": \"0\"," +
                "\"cc\": \"1\"," +
                "\"summary_es\": \"Konstaterte smittsom hjernehinnebetennelse på 2-Aring-Folkebladet.no\"," +
                "\"summary_fr\": \"Konstaterte smittsom hjernehinnebetennelse på 2 Aring-Folkebladet.no\"," +
                "\"summary_ru\": \"Konstaterte smittsom hjernehinnebetennelse PA 2-Aring-Folkebladet.no\"," +
                "\"summary_zh\": \"Konstaterte smittsom hjernehinnebetennelsePå2-ARING-Folkebladet.no\"," +
                "\"summary_pt\": \"Konstaterte smittsom hjernehinnebetennelse på 2-Aring-Folkebladet.no\"," +
                "\"summary_ar\": \"Konstaterte smittsom hjernehinnebetennelse PA 2-åring-Folkebladet.no\"," +
                "\"summary_vi\": \"Konstaterte smittsom hjernehinnebetennelse på 2 aring-Folkebladet.no\"," +
                "\"place_category\": []," +
                "\"original_url\": \"http://www.folkebladet.no/nyheter/article8911579.ece\"," +
                "\"disease_id\": \"84\"," +
                "\"feed_id\": \"20\"" +
                "}," +
                // Alert 2 (reviewed and commented, with multiple diseases)
                "{" +
                "\"feed\": \"ABRAID PubMed\"," +
                "\"reviewed\": \"2014-12-03 14:27:57-0500\"," +
                "\"disease_ids\": [\"80\"," +
                "\"124\"]," +
                "\"diseases\": [\"Malaria\"," +
                "\"Schistosomiasis\"]," +
                "\"species_ids\": [\"132\"," +
                "\"132\"]," +
                "\"species\": [\"Humans\"," +
                "\"Humans\"]," +
                "\"disease\": \"Malaria\"," +
                "\"summary\": \"Concomitant infection by Plasmodium falciparum and Schistosoma haematobium among school children in a rural area of Senegal.\"," +
                "\"date\": \"2010-03-06 00:00:00-0500\"," +
                "\"formatted_date\": \" 6 March 2010 00:00:00 EST\"," +
                "\"link\": \"http:\\/\\/healthmap.org\\/ln.php?2576498\"," +
                "\"descr\": \"Malaria is endemic in lowland and coastal regions of Papua New Guinea (PNG)...\"," +
                "\"rating\": {" +
                "\"count\": 0," +
                "\"rating\": 3" +
                "}," +
                "\"comment\": \"s h, p f, p m, \"," +
                "\"species_name\": \"Humans\"," +
                "\"dup_count\": \"0\"," +
                "\"place_category\": [\"Imported Case\"]," +
                "\"original_url\": \"http:\\/\\/www.ncbi.nlm.nih.gov\\/pubmed\\/21129869\"," +
                "\"disease_id\": \"80\"," +
                "\"feed_id\": \"172\"" +
                "}" +
                "]" +
                "}";
        String json = "[" + jsonLocation1 + "," + jsonLocation2 + "]";

        String url = getHealthMapBaseUrl() + addStartDate(startDateUrl) + addEndDate(endDateUrl);
        WebServiceClient client = getMockWebServiceClient(url, json);
        HealthMapWebService webService = getHealthMapWebService(client);

        // Act
        List<HealthMapLocation> healthMapLocations = webService.sendRequest(startDate, endDate);

        // Assert
        assertThat(healthMapLocations).hasSize(2);

        HealthMapLocation location1 = healthMapLocations.get(0);
        assertThat(location1.getCountry()).isEqualTo("China");
        assertThat(location1.getCountryId()).isEqualTo(155);
        assertThat(location1.getGeoNameId()).isEqualTo(1795565);
        assertThat(location1.getLatitude()).isEqualTo(22.54554);
        assertThat(location1.getLongitude()).isEqualTo(114.068298);
        assertThat(location1.getPlaceBasicType()).isEqualTo("p");
        assertThat(location1.getPlaceName()).isEqualTo("Shenzhen, Guangdong, China");
        assertThat(location1.getAlerts()).hasSize(2);

        HealthMapAlert location1Alert1 = location1.getAlerts().get(0);
        assertThat(location1Alert1.getAlertId()).isEqualTo(2161280);
        assertThatDatesAreEqual(location1Alert1.getDate(), new DateTime("2014-01-07T05:00:00+0000"));
        assertThat(location1Alert1.getDescription()).isNull();
        assertThat(location1Alert1.getDiseases()).containsExactly("Avian Influenza H7N9");
        assertThat(location1Alert1.getFeed()).isEqualTo("Food and Agriculture Org");
        assertThat(location1Alert1.getLink()).isEqualTo("http://healthmap.org/ln.php?2161280");
        assertThat(location1Alert1.getOriginalUrl()).isEqualTo("http://empres-i.fao.org/empres-i/2/obd?idOutbreak=" +
                "182133&rss=t");
        assertThat(location1Alert1.getSummary()).isEqualTo("Confirmed Influenza - Avian in Guangdong Sheng, China -" +
                " human");
        assertThat(location1Alert1.getDiseaseIds()).containsExactly(271);
        assertThat(location1Alert1.getFeedId()).isEqualTo(10);
        assertThat(location1Alert1.getFeedLanguage()).isEqualTo("zh");

        HealthMapAlert location1Alert2 = location1.getAlerts().get(1);
        assertThat(location1Alert2.getAlertId()).isEqualTo(2155089);
        assertThatDatesAreEqual(location1Alert2.getDate(), new DateTime("2014-01-06T21:07:33+0000"));
        assertThat(location1Alert2.getDescription()).isEqualTo("The United Nations World Health Organization (WHO) " +
                "revealed Monday that China's National Health and Family Planning Commission had notified it in " +
                "mid-December of two new laboratory-confirmed cases of human infection with avian influenza A(H7N9)");
        assertThat(location1Alert2.getDiseases()).containsExactly("Avian Influenza H7N9");
        assertThat(location1Alert2.getFeed()).isEqualTo("Google News");
        assertThat(location1Alert2.getLink()).isEqualTo("http://healthmap.org/ln.php?2155089");
        assertThat(location1Alert2.getOriginalUrl()).isEqualTo("http://news.google.com/news/url?sa=t&fd=R&usg=AFQjCN" +
                "FduJXlyPpcRmF2wzssn6vgeH6dIw&url=http://www.rttnews.com/2247181/two-new-cases-of-human-h7n9-infect" +
                "ions-reported-in-china.aspx?type%3Dmsgn%26utm_source%3Dgoogle%26utm_campaign%3Dsitemap");
        assertThat(location1Alert2.getSummary()).isEqualTo("Two New Cases Of Human H7N9 Infections Reported In China" +
                " - RTT News");
        assertThat(location1Alert2.getDiseaseIds()).containsExactly(271);
        assertThat(location1Alert2.getFeedId()).isEqualTo(4);
        assertThat(location1Alert2.getFeedLanguage()).isNull();

        HealthMapLocation location2 = healthMapLocations.get(1);
        assertThat(location2.getCountry()).isNull();
        assertThat(location2.getCountryId()).isEqualTo(107);
        assertThat(location2.getGeoNameId()).isNull();
        assertThat(location2.getLatitude()).isNull();
        assertThat(location2.getLongitude()).isEqualTo(17.987391);
        assertThat(location2.getPlaceBasicType()).isNull();
        assertThat(location2.getPlaceName()).isEqualTo("Lenvik, Troms, Norway");
        assertThat(location2.getAlerts()).hasSize(2);

        HealthMapAlert location2Alert1 = location2.getAlerts().get(0);
        assertThat(location2Alert1.getAlertId()).isNull();
        assertThat(location2Alert1.getDate()).isNull();
        assertThat(location2Alert1.getReviewed()).isNull();
        assertThat(location2Alert1.getDescription()).isEqualTo("Dubøroppgraderenettleserendin");
        assertThat(location2Alert1.getDiseases()).containsExactly("Meningitis");
        assertThat(location2Alert1.getFeed()).isEqualTo("HM Community News Reports");
        assertThat(location2Alert1.getLink()).isEqualTo("unknown");
        assertThat(location2Alert1.getOriginalUrl()).isEqualTo("http://www.folkebladet.no/nyheter/article8911579.ece");
        assertThat(location2Alert1.getSummary()).isEqualTo("Konstaterte smittsom hjernehinnebetennelse på 2-åring " +
                "-Folkebladet.no");
        assertThat(location2Alert1.getDiseaseIds()).containsExactly(84);
        assertThat(location2Alert1.getFeedId()).isEqualTo(20);
        assertThat(location2Alert1.getFeedLanguage()).isNull();
        assertThat(location2Alert1.getPlaceCategories()).isEmpty();

        HealthMapAlert location2Alert2 = location2.getAlerts().get(1);
        assertThat(location2Alert2.getAlertId()).isEqualTo(2576498);
        assertThatDatesAreEqual(location2Alert2.getDate(), new DateTime("2010-03-06T00:00:00-0500"));
        assertThatDatesAreEqual(location2Alert2.getReviewed(), new DateTime("2014-12-03T14:27:57-0500"));
        assertThat(location2Alert2.getDescription()).isEqualTo("Malaria is endemic in lowland and coastal regions of " +
                "Papua New Guinea (PNG)...");
        assertThat(location2Alert2.getDiseases()).containsExactly("Malaria", "Schistosomiasis");
        assertThat(location2Alert2.getDiseaseIds()).containsExactly(80, 124);
        assertThat(location2Alert2.getFeed()).isEqualTo("ABRAID PubMed");
        assertThat(location2Alert2.getLink()).isEqualTo("http://healthmap.org/ln.php?2576498");
        assertThat(location2Alert2.getOriginalUrl()).isEqualTo("http://www.ncbi.nlm.nih.gov/pubmed/21129869");
        assertThat(location2Alert2.getSummary()).isEqualTo("Concomitant infection by Plasmodium falciparum and " +
                "Schistosoma haematobium among school children in a rural area of Senegal.");
        assertThat(location2Alert2.getFeedId()).isEqualTo(172);
        assertThat(location2Alert2.getFeedLanguage()).isNull();
        assertThat(location2Alert2.getComment()).isEqualTo("s h, p f, p m,");
        assertThat(location2Alert2.getSplitComment()).containsOnly("sh", "pf", "pm");
        assertThat(location2Alert2.getPlaceCategories()).containsExactly("Imported Case");
    }

    @Test
    public void healthMapReturnsNoResults() {
        // Arrange
        DateTime startDate = new DateTime("2014-01-06T00:00:00+0000");
        String startDateUrl = "2014-01-06%2000:00:00%2B0000";
        String json = "[]";

        String url = getHealthMapBaseUrl() + addStartDate(startDateUrl);
        WebServiceClient client = getMockWebServiceClient(url, json);
        HealthMapWebService webService = getHealthMapWebService(client);

        // Act
        List<HealthMapLocation> healthMapLocations = webService.sendRequest(startDate, null);

        // Assert
        assertThat(healthMapLocations).hasSize(0);
    }

    @Test
    public void webServiceClientThrewException() {
        // Arrange
        String url = getHealthMapBaseUrl();
        WebServiceClient client = mock(WebServiceClient.class);
        //noinspection unchecked
        when(client.makeGetRequest(url)).thenThrow(WebServiceClientException.class);
        HealthMapWebService webService = getHealthMapWebService(client);

        // Act
        catchException(webService).sendRequest(null, null);

        // Assert
        assertThat(caughtException()).isInstanceOf(WebServiceClientException.class);
    }

    @Test
    public void invalidJsonReturnedByGeoNames() {
        // Arrange
        String json = "this is invalid JSON";

        String url = getHealthMapBaseUrl();
        WebServiceClient client = getMockWebServiceClient(url, json);
        HealthMapWebService webService = getHealthMapWebService(client);

        // Act
        catchException(webService).sendRequest(null, null);

        // Assert
        assertThat(caughtException()).isInstanceOf(JsonParserException.class);
    }

    @Test
    public void setDefaultStartDateValid() {
        // Arrange
        DateTime startDate = new DateTime("2014-04-21T14:29:03+0000");
        String startDateString = "2014-04-21 15:29:03+0100";

        // Act
        HealthMapWebService webService = new HealthMapWebService(new WebServiceClient());
        webService.setDefaultStartDate(startDateString);

        // Assert
        assertThatDatesAreEqual(webService.getDefaultStartDate(), startDate);
    }

    @Test
    public void setDefaultStartDateInvalid() {
        // Arrange
        String startDateString = "2014-04-21 15:29:03";

        // Act
        HealthMapWebService webService = new HealthMapWebService(new WebServiceClient());
        catchException(webService).setDefaultStartDate(startDateString);

        // Assert
        assertThat(caughtException()).isInstanceOf(RuntimeException.class);
    }

    @Test
    public void parseHealthMapJsonContainingMultipleDiseases() {
        String alertJson = "[{" +
                "\"alerts\": [" +
                "{" +
                "\"feed\": \"HM Community News Reports\"," +
                "\"disease\": \"This disease should be ignored\"," +
                "\"disease_id\": \"999\"," +
                "\"diseases\": [\"Meningitis\", \"Leishmaniasis\", \"Hookworm\"]," +
                "\"disease_ids\": [84, \"invalid\", \"64\"]" +
                "}" +
                "]" +
                "}]";

        HealthMapWebService webService = getHealthMapWebService(new WebServiceClient());
        List<HealthMapLocation> locations = webService.parseJson(alertJson);

        assertThat(locations).hasSize(1);
        assertThat(locations.get(0).getAlerts()).hasSize(1);
        assertThat(locations.get(0).getAlerts()).hasSize(1);
        HealthMapAlert alert = locations.get(0).getAlerts().get(0);
        assertThat(alert.getDiseases()).containsExactly("Meningitis", "Leishmaniasis", "Hookworm");
        assertThat(alert.getDiseaseIds()).containsExactly(84, null, 64);
    }

    private String getHealthMapBaseUrl() {
        return "http://healthmap.org/HMapi.php?auth=testauthcode&striphtml=false";
    }

    private String addStartDate(String startDateString) {
        return "&sdate=" + startDateString;
    }

    private String addEndDate(String endDateString) {
        return "&edate=" + endDateString;
    }

    private HealthMapWebService getHealthMapWebService(WebServiceClient client) {
        HealthMapWebService webService = new HealthMapWebService(client);
        webService.setRootUrl("http://healthmap.org/HMapi.php");
        webService.setAuthorizationCode("testauthcode");
        webService.setAuthorizationParameterName("auth");
        webService.setStartDateParameterName("sdate");
        webService.setEndDateParameterName("edate");
        webService.setStripHtmlParameterName("striphtml");
        webService.setStripHtml(false);
        return webService;
    }

    private WebServiceClient getMockWebServiceClient(String url, String json) {
        WebServiceClient client = mock(WebServiceClient.class);
        when(client.makeGetRequest(url)).thenReturn(json);
        return client;
    }

    private void assertThatDatesAreEqual(DateTime date1, DateTime date2) {
        // This ensures that timezone differences are ignored
        assertThat(date1.toLocalDateTime()).isEqualTo(date2.toLocalDateTime());
    }
}
