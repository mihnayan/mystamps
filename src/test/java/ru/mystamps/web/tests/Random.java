/*
 * Copyright (C) 2009-2018 Slava Semushin <slava.semushin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package ru.mystamps.web.tests;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import io.qala.datagen.RandomShortApi;

import ru.mystamps.web.Db.SeriesImportRequestStatus;
import ru.mystamps.web.dao.dto.EntityWithIdDto;
import ru.mystamps.web.dao.dto.EntityWithParentDto;
import ru.mystamps.web.dao.dto.ImportRequestFullInfo;
import ru.mystamps.web.service.TestObjects;
import ru.mystamps.web.validation.ValidationRules;

import static io.qala.datagen.RandomShortApi.bool;
import static io.qala.datagen.RandomShortApi.integer;
import static io.qala.datagen.RandomShortApi.sample;
import static io.qala.datagen.RandomShortApi.sampleMultiple;
import static io.qala.datagen.RandomValue.between;
import static io.qala.datagen.StringModifier.Impls.multipleOf;
import static io.qala.datagen.StringModifier.Impls.oneOf;

public final class Random {
	
	// @todo #687 Random.STATUSES: reduce duplication by using EnumSet.allOf()
	private static final String[] STATUSES = new String[] {
		SeriesImportRequestStatus.UNPROCESSED,
		SeriesImportRequestStatus.DOWNLOADING_SUCCEEDED,
		SeriesImportRequestStatus.DOWNLOADING_FAILED,
		SeriesImportRequestStatus.PARSING_SUCCEEDED,
		SeriesImportRequestStatus.PARSING_FAILED,
		SeriesImportRequestStatus.IMPORT_SUCCEEDED,
	};
	
	private Random() {
	}
	
	public static Integer id() {
		return RandomShortApi.positiveInteger();
	}
	
	public static Integer userId() {
		return RandomShortApi.positiveInteger();
	}
	
	public static Date date() {
		try {
			
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
			return between(fmt.parse("2017-01-01"), fmt.parse("2017-21-20")).date();
		
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static BigDecimal price() {
		// @todo #769 Random.price(): return randomized values
		return new BigDecimal("17");
	}
	
	public static String url() {
		final long minLength = 5;
		final long maxLength = 15;
		String randomPart = between(minLength, maxLength).with(multipleOf('/')).alphanumeric();
		return "http://example.com/page/" + randomPart;
	}
	
	public static String lang() {
		return sample(Locale.getISOLanguages());
	}
	
	public static String name() {
		final long enoughLongLength = 15;
		return between(1, enoughLongLength).english();
	}
	
	public static String categoryName() {
		String name = between(
				ValidationRules.CATEGORY_NAME_MIN_LENGTH,
				ValidationRules.CATEGORY_NAME_MAX_LENGTH
			)
			.with(oneOf(" -"))
			.english();
		
		if (StringUtils.startsWithAny(name, " ", "-")
			|| StringUtils.endsWithAny(name, " ", "-")) {
			return countryName();
		}
		
		return name;
	}
	
	public static String countryName() {
		String name = between(
				ValidationRules.COUNTRY_NAME_MIN_LENGTH,
				ValidationRules.COUNTRY_NAME_MAX_LENGTH
			)
			.with(oneOf(" -"))
			.english();
		
		if (StringUtils.startsWithAny(name, " ", "-")
			|| StringUtils.endsWithAny(name, " ", "-")) {
			return countryName();
		}
		
		return name;
	}
	
	public static String participantName() {
		return between(
			ValidationRules.PARTICIPANT_NAME_MIN_LENGTH,
			ValidationRules.PARTICIPANT_NAME_MAX_LENGTH
		).english();
	}
	
	public static Integer issueYear() {
		return between(ValidationRules.MIN_RELEASE_YEAR, Year.now().getValue()).integer();
	}
	
	public static Integer quantity() {
		return between(
			ValidationRules.MIN_STAMPS_IN_SERIES,
			ValidationRules.MAX_STAMPS_IN_SERIES
		).integer();
	}
	
	public static boolean perforated() {
		return bool();
	}
	
	public static String importRequestStatus() {
		return sample(STATUSES);
	}
	
	public static List<String> importRequestStatuses(int numToReturn) {
		return sampleMultiple(numToReturn, STATUSES);
	}
	
	public static Set<String> solovyovNumbers() {
		return catalogNumbers();
	}
	
	public static Set<String> zagorskiNumbers() {
		return catalogNumbers();
	}
	
	public static Set<String> setOfStrings() {
		final int minSize = 1;
		final int maxSize = 3;
		int size = integer(minSize, maxSize);
		return new HashSet<>(sampleMultiple(size, "foo", "bar", "baz"));
	}
	
	public static List<Integer> listOfIntegers() {
		final int minSize = 1;
		final int maxSize = 3;
		int size = integer(minSize, maxSize);
		return sampleMultiple(size, integer(), integer(), integer());
	}
	
	public static List<EntityWithIdDto> listOfEntityWithIdDto() {
		final int minSize = 1;
		final int maxSize = 3;
		int size = integer(minSize, maxSize);
		return sampleMultiple(
			size,
			TestObjects.createEntityWithIdDto(),
			TestObjects.createEntityWithIdDto(),
			TestObjects.createEntityWithIdDto()
		);
	}
	
	public static List<EntityWithParentDto> listOfEntityWithParentDto() {
		final int minSize = 1;
		final int maxSize = 3;
		int size = integer(minSize, maxSize);
		return sampleMultiple(
			size,
			TestObjects.createEntityWithParentDto(),
			TestObjects.createEntityWithParentDto(),
			TestObjects.createEntityWithParentDto()
		);
	}
	
	public static List<ImportRequestFullInfo> listOfImportRequestFullInfo() {
		final int minSize = 1;
		final int maxSize = 3;
		int size = integer(minSize, maxSize);
		return sampleMultiple(
			size,
			TestObjects.createImportRequestFullInfo(),
			TestObjects.createImportRequestFullInfo(),
			TestObjects.createImportRequestFullInfo()
		);
	}
	
	public static String jsoupLocator() {
		List<String> locators = Arrays.asList("#id", "a[href]", "img[src$=.png]", "div#logo");
		return sample(locators);
	}
	
	public static String tagAttributeName() {
		List<String> attributes = Arrays.asList("href", "src", "data-dst");
		return sample(attributes);
	}
	
	private static Set<String> catalogNumbers() {
		final int minSize = 1;
		final int maxSize = 7;
		int size = integer(minSize, maxSize);
		return new HashSet<>(
			sampleMultiple(
				size,
				catalogNumber(),
				catalogNumber(),
				catalogNumber(),
				catalogNumber(),
				catalogNumber(),
				catalogNumber(),
				catalogNumber()
			)
		);
	}
	
	private static String catalogNumber() {
		final long maxCatalogNumber = 1000;
		return String.valueOf(between(1, maxCatalogNumber).integer());
	}
	
}
