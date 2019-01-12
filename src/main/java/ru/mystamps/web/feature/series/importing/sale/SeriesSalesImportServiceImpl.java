/*
 * Copyright (C) 2009-2019 Slava Semushin <slava.semushin@gmail.com>
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

import org.apache.commons.lang3.Validate;

import org.slf4j.Logger;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.feature.series.importing.RawParsedDataDto;
import ru.mystamps.web.feature.series.importing.SeriesExtractedInfo;
import ru.mystamps.web.feature.series.importing.SeriesInfoExtractorService;
import ru.mystamps.web.feature.series.importing.extractor.SeriesInfo;
import ru.mystamps.web.feature.series.importing.extractor.SiteParser;
import ru.mystamps.web.feature.series.importing.extractor.SiteParserService;
import ru.mystamps.web.service.DownloaderService;
import ru.mystamps.web.service.dto.DownloadResult;

@RequiredArgsConstructor
public class SeriesSalesImportServiceImpl implements SeriesSalesImportService {
	
	private final Logger log;
	private final SeriesSalesImportDao seriesSalesImportDao;
	private final DownloaderService downloaderService;
	private final SiteParserService siteParserService;
	private final SeriesInfoExtractorService extractorService;
	
	// XXX: @PreAuthorize
	// XXX: @Transactional ?
	// @todo #995 SeriesSalesImportServiceImpl.downloadAndParse(): add unit tests
	@Override
	public SeriesSaleExtractedInfo downloadAndParse(String url) {
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

	// @todo #834 SeriesSalesImportServiceImpl.saveParsedData(): introduce dto without dates
	@Override
	@Transactional
	public void saveParsedData(Integer requestId, SeriesSalesParsedDataDbDto data) {
		Validate.isTrue(requestId != null, "Request id must be non null");
		Validate.isTrue(data != null, "Parsed data must be non null");
		
		seriesSalesImportDao.addParsedData(requestId, data);
	}
	
	@Override
	@Transactional(readOnly = true)
	public SeriesSaleParsedDataDto getParsedData(Integer requestId) {
		Validate.isTrue(requestId != null, "Request id must be non null");
		
		return seriesSalesImportDao.findParsedDataByRequestId(requestId);
	}
	
}
