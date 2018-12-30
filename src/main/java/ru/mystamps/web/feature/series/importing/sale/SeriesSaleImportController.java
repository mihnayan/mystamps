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
package ru.mystamps.web.feature.series.importing.sale;

import javax.validation.Valid;

import org.slf4j.Logger;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.feature.series.importing.RawParsedDataDto;
import ru.mystamps.web.feature.series.importing.RequestImportForm;
import ru.mystamps.web.feature.series.importing.SeriesExtractedInfo;
import ru.mystamps.web.feature.series.importing.SeriesInfoExtractorService;
import ru.mystamps.web.feature.series.importing.extractor.SeriesInfo;
import ru.mystamps.web.feature.series.importing.extractor.SiteParser;
import ru.mystamps.web.feature.series.importing.extractor.SiteParserService;
import ru.mystamps.web.service.DownloaderService;
import ru.mystamps.web.service.dto.DownloadResult;

@RestController
@RequiredArgsConstructor
public class SeriesSaleImportController {
	
	private final Logger log;
	private final DownloaderService downloaderService;
	private final SiteParserService siteParserService;
	private final SeriesInfoExtractorService extractorService;
	
	// XXX: extract constant
	// XXX: secure URL
	// XXX: validate URL
	@PostMapping(path = "/series/sales/import")
	public SeriesSaleExtractedInfo downloadAndParse(@RequestBody @Valid RequestImportForm form) {
		// XXX: introduce service
		
		String url = form.getUrl();
		
		log.info("Start downloading '{}'", url);
		
		DownloadResult result = downloaderService.download(url);
		if (result.hasFailed()) {
			log.info("Downloading of '{}' failed: {}", url, result.getCode());
			// XXX: return an error
			return null;
		}
		
		log.info("Downloading succeeded");
		
		SiteParser parser = siteParserService.findForUrl(url);
		if (parser == null) {
			log.error("Could not find appropriate parser");
			// XXX: return an error
			return null;
		}
		
		String content = result.getDataAsString();
		
		SeriesInfo info = parser.parse(content);
		if (info.isEmpty()) {
			// XXX: return an error
			return null;
		}
		
		// XXX: don't process unrelated fields
		RawParsedDataDto data = new RawParsedDataDto(
			info.getCategoryName(),
			info.getCountryName(),
			info.getImageUrl(),
			info.getIssueDate(),
			info.getQuantity(),
			info.getPerforated(),
			info.getMichelNumbers(),
			info.getSellerName(),
			info.getSellerUrl(),
			info.getPrice(),
			info.getCurrency()
		);
		
		SeriesExtractedInfo seriesInfo = extractorService.extract(url, data);
		
		return new SeriesSaleExtractedInfo(
			seriesInfo.getSellerId(),
			seriesInfo.getSellerName(),
			seriesInfo.getSellerUrl(),
			seriesInfo.getSellerGroupId(),
			seriesInfo.getPrice(),
			seriesInfo.getCurrency()
		);
	}
	
	
}
