package org.trading;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.DoubleNum;
import org.ta4j.core.num.Num;
import org.trading.event.Confirms;

//@SpringBootTest
class DemoApplicationTests {

	@Test
	void contextLoads() throws JsonProcessingException, ParseException {
		var bb = LocalDateTime.now();
		var a = LocalDate.parse("2023-12-24");
		var dtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		var utcConfirmsUpdate = "2022-06-13T07:08:25.058".replace("T"," ");
		var b = dtf.parse(utcConfirmsUpdate).toInstant();
		var utcNow = Instant.now();
		var c = "{\"direction\":\"BUY\",\"epic\":\"IX.D.FTSE.IFE.IP\",\"stopLevel\":null,\"limitLevel\":null,\"dealReference\":\"1MYREFIXDFTSEIFEIP\",\"dealId\":\"DIAAAAJNND42QAZ\",\"limitDistance\":11.666667,\"stopDistance\":5.833333,\"expiry\":\"-\",\"affectedDeals\":[{\"dealId\":\"DIAAAAJNND42QAZ\",\"status\":\"OPENED\"}],\"dealStatus\":\"ACCEPTED\",\"guaranteedStop\":false,\"trailingStop\":false,\"level\":7264.1,\"reason\":\"SUCCESS\",\"status\":\"OPEN\",\"size\":1,\"profit\":null,\"profitCurrency\":null,\"date\":\"2022-06-13T07:08:25.058\",\"channel\":\"PublicRestOTC\"}";
		var mapper = new ObjectMapper();
		mapper.readValue(c, Confirms.class);
	}

	@Test
	void atr() {
		var now = ZonedDateTime.now();
		var earlyBar = BaseBar.builder(DecimalNum::valueOf, Number.class)
				.endTime(now)
				.timePeriod(Duration.ofMinutes(1))
				.openPrice(100.)
				.closePrice(50.)
				.highPrice(150.)
				.lowPrice(10.)
				.volume(1).build();
		var laterBar = BaseBar.builder(DecimalNum::valueOf, Number.class)
				.endTime(now.plusMinutes(4))
				.timePeriod(Duration.ofMinutes(1))
				.openPrice(1000.)
				.closePrice(500.)
				.highPrice(1500.)
				.lowPrice(100.)
				.volume(10.).build();
		var series = new BaseBarSeries("test");
		series.addBar(earlyBar);
		series.addBar(laterBar);

		var per = series.getSeriesPeriodDescription();
		//"Europe/Stockholm"
		assert series.getBar(series.getEndIndex()).getClosePrice() == laterBar.getClosePrice();
	}

}
