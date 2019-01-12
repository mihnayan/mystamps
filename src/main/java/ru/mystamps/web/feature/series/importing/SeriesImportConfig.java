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
package ru.mystamps.web.feature.series.importing;

import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.config.DaoConfig;
import ru.mystamps.web.feature.participant.ParticipantService;
import ru.mystamps.web.feature.series.SeriesController;
import ru.mystamps.web.feature.series.SeriesService;
import ru.mystamps.web.feature.series.importing.extractor.SiteParserService;
import ru.mystamps.web.feature.series.importing.sale.*;
import ru.mystamps.web.feature.series.sale.SeriesSalesService;
import ru.mystamps.web.service.DownloaderService;

/**
 * Spring configuration that is required for importing series in an application.
 *
 * The beans are grouped into two classes to make possible to register a controller
 * and the services in the separated application contexts.
 */
@Configuration
public class SeriesImportConfig {
	
	@RequiredArgsConstructor
	public static class Controllers {
		
		private final ParticipantService participantService;
		private final SeriesImportService seriesImportService;
		private final SeriesSalesImportService seriesSalesImportService;
		private final SeriesController seriesController;
		private final ApplicationEventPublisher eventPublisher;
		
		@Bean
		public SeriesImportController seriesImportController() {
			return new SeriesImportController(
				seriesImportService,
				seriesSalesImportService,
				seriesController,
				participantService,
				eventPublisher
			);
		}
		
		// XXX: move to separate config
		@Bean
		public SeriesSaleImportController seriesSaleImportController() {
			return new SeriesSaleImportController(
				seriesSalesImportService
			);
		}
		
	}
	
	@RequiredArgsConstructor
	public static class Services {
		
		private final NamedParameterJdbcTemplate jdbcTemplate;
		private final ParticipantService participantService;
		private final SeriesService seriesService;
		private final SeriesSalesService seriesSalesService;
		private final ApplicationEventPublisher eventPublisher;
		private final SiteParserService siteParserService;
		private final SeriesInfoExtractorService extractorService;
		
		@Bean
		public SeriesImportService seriesImportService(
			SeriesImportDao seriesImportDao,
			SeriesSalesImportService seriesSalesImportService
			) {
			
			return new SeriesImportServiceImpl(
				LoggerFactory.getLogger(SeriesImportServiceImpl.class),
				seriesImportDao,
				seriesService,
				seriesSalesService,
				seriesSalesImportService,
				participantService,
				eventPublisher
			);
		}
		
		@Bean
		public SeriesSalesImportService getSeriesSalesImportService(
			@Qualifier("seriesDownloaderService") DownloaderService seriesDownloaderService,
			SeriesSalesImportDao seriesSalesImportDao
			) {
			
			return new SeriesSalesImportServiceImpl(
				LoggerFactory.getLogger(SeriesSalesImportServiceImpl.class),
				seriesSalesImportDao,
				seriesDownloaderService,
				siteParserService,
				extractorService
			);
		}
		
		@Bean
		public SeriesSalesImportDao getSeriesSalesImportDao() {
			return new JdbcSeriesSalesImportDao(jdbcTemplate);
		}
		
		@Bean
		public SeriesImportDao seriesImportDao() {
			return new JdbcSeriesImportDao(jdbcTemplate);
		}
		
	}
	
}
