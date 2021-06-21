package net.avdw.skilltracker;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;


public class StringFormatTest {
    @Test
    public void testCamelToTitleCase() {
        assertEquals("Hello There", StringFormat.camelCaseToTitleCase("helloThere"));
        assertEquals("Hello There", StringFormat.camelCaseToTitleCase("HelloThere"));
        assertEquals("I Love The USA", StringFormat.camelCaseToTitleCase("ILoveTheUSA"));
        assertEquals("I Love The USA", StringFormat.camelCaseToTitleCase("iLoveTheUSA"));
        assertEquals("DB Host Country", StringFormat.camelCaseToTitleCase("DBHostCountry"));
        assertEquals("Set Slot 123 To Input 456", StringFormat.camelCaseToTitleCase("SetSlot123ToInput456"));
        assertEquals("I Love The USA Network In The USA", StringFormat.camelCaseToTitleCase("ILoveTheUSANetworkInTheUSA"));
        assertEquals("Limit IOC Duration", StringFormat.camelCaseToTitleCase("Limit_IOC_Duration"));
        assertEquals("A Song About The ABCs Is Fun To Sing", StringFormat.camelCaseToTitleCase("ASongAboutTheABCsIsFunToSing"));
        assertEquals("CFDs", StringFormat.camelCaseToTitleCase("CFDs"));
        assertEquals("DB Settings", StringFormat.camelCaseToTitleCase("DBSettings"));
        assertEquals("I Would Love 1 Apple", StringFormat.camelCaseToTitleCase("IWouldLove1Apple"));
        assertEquals("Employee 22 Is Cool", StringFormat.camelCaseToTitleCase("Employee22IsCool"));
        assertEquals("Sub ID In", StringFormat.camelCaseToTitleCase("SubIDIn"));
        assertEquals("Configure CFDs Immediately", StringFormat.camelCaseToTitleCase("ConfigureCFDsImmediately"));
        assertEquals("Use Taker Login For On Behalf Of Sub ID In Orders", StringFormat.camelCaseToTitleCase("UseTakerLoginForOnBehalfOfSubIDInOrders"));
        assertEquals("To Get Your GED In Time A Song About The 26 ABCs Is Of The Essence But A Personal ID Card For User 456 In Room 26A Containing ABC 26 Times Is Not As Easy As 123 For C3PO Or R2D2 Or 2R2D", StringFormat.camelCaseToTitleCase("__ToGetYourGEDInTimeASongAboutThe26ABCsIsOfTheEssenceButAPersonalIDCardForUser_456InRoom26AContainingABC26TimesIsNotAsEasyAs123ForC3POOrR2D2Or2R2D"));
    }
}
