/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2015>  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.wtms.service.es;

import static org.egov.wtms.utils.constants.WaterTaxConstants.WATER_TAX_INDEX_NAME;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.utils.DateUtils;
import org.egov.ptis.constants.PropertyTaxConstants;
import org.egov.ptis.domain.entity.es.BillCollectorIndex;
import org.egov.ptis.domain.model.ErrorDetails;
import org.egov.wtms.bean.dashboard.TaxPayerDetails;
import org.egov.wtms.bean.dashboard.TaxPayerResponseDetails;
import org.egov.wtms.bean.dashboard.WaterChargeDashBoardRequest;
import org.egov.wtms.bean.dashboard.WaterChargeDashBoardResponse;
import org.egov.wtms.entity.es.WaterChargeDocument;
import org.egov.wtms.repository.es.WaterChargeDocumentRepository;
import org.egov.wtms.utils.constants.WaterTaxConstants;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

@Service
public class WaterChargeElasticSearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WaterChargeElasticSearchService.class);

    private final WaterChargeDocumentRepository waterChargeIndexRepository;

    private static final String TOTAL_COLLECTION = "total_collection";
    private static final String AGGREGATION_FIELD = "by_aggregationField";
    private static final String TOTAL_DEMAND = "totalDemand";
    private static final String TOTALDEMAND = "totaldemand";

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private WaterChargeCollectionDocService waterChargeCollDocService;

    @Autowired
    public WaterChargeElasticSearchService(final WaterChargeDocumentRepository waterChargeIndexRepository) {
        this.waterChargeIndexRepository = waterChargeIndexRepository;
    }

    public Page<WaterChargeDocument> findByConsumercode(final String consumerCode) {
        return waterChargeIndexRepository.findByConsumerCodeAndCityName(consumerCode,
                ApplicationThreadLocals.getCityName(), new PageRequest(0, 10));
    }

    /**
     * API returns the current year total demand from WaterCharge index
     *
     * @return BigDecimal
     */
    public BigDecimal getTotalDemand() {
        final SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices(WATER_TAX_INDEX_NAME).addAggregation(AggregationBuilders
                        .sum(WaterTaxConstants.WATERCHARGETOTALDEMAND).field(WaterTaxConstants.WATERCHARGETOTALDEMAND))
                .build();

        final Aggregations aggregations = elasticsearchTemplate.query(searchQuery,
                response -> response.getAggregations());

        final Sum aggr = aggregations.get(WaterTaxConstants.WATERCHARGETOTALDEMAND);
        return BigDecimal.valueOf(aggr.getValue()).setScale(0, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Populates the consolidated demand information in CollectionIndexDetails
     *
     * @param waterChargedashBoardRequest
     * @param collectionIndexDetails
     */
    public List<WaterChargeDashBoardResponse> getConsolidatedDemandInfo(
            final WaterChargeDashBoardRequest waterChargedashBoardRequest) {
        final List<WaterChargeDashBoardResponse> collectionIndexDetailsList = new ArrayList<>();
        final WaterChargeDashBoardResponse collectionIndexDetails = new WaterChargeDashBoardResponse();

        Date fromDate;
        Date toDate;
        /**
         * For fetching total demand between the date ranges if dates are sent
         * in the request, consider fromDate and toDate+1 , else calculate from
         * current year start date till current date+1 day
         */
        if (StringUtils.isNotBlank(waterChargedashBoardRequest.getFromDate())
                && StringUtils.isNotBlank(waterChargedashBoardRequest.getToDate())) {
            fromDate = DateUtils.getDate(waterChargedashBoardRequest.getFromDate(), "yyyy-MM-dd");
            toDate = org.apache.commons.lang3.time.DateUtils
                    .addDays(DateUtils.getDate(waterChargedashBoardRequest.getToDate(), "yyyy-MM-dd"), 1);
        } else {
            fromDate = new DateTime().withMonthOfYear(4).dayOfMonth().withMinimumValue().toDate();
            toDate = org.apache.commons.lang3.time.DateUtils.addDays(new Date(), 1);
        }
        Long startTime = System.currentTimeMillis();
        final BigDecimal totalDemand = getTotalDemandBasedOnInputFilters(waterChargedashBoardRequest);
        Long timeTaken = System.currentTimeMillis() - startTime;
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Time taken by getTotalDemandBasedOnInputFilters() is (millisecs) : " + timeTaken);
        startTime = System.currentTimeMillis();
        final int noOfMonths = DateUtils.noOfMonths(fromDate, toDate) + 1;
        collectionIndexDetails.setTotalDmd(totalDemand);

        // Proportional Demand = (totalDemand/12)*noOfmonths
        final BigDecimal proportionalDemand = totalDemand.divide(BigDecimal.valueOf(12), BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(noOfMonths));
        collectionIndexDetails.setCurrentYearTillDateDmd(proportionalDemand.setScale(0, BigDecimal.ROUND_HALF_UP));

        // performance = (current year tilldate collection * 100)/(proportional
        // demand)
        collectionIndexDetails.setPerformance(collectionIndexDetails.getCurrentYearTillDateColl()
                .multiply(WaterTaxConstants.BIGDECIMAL_100).divide(proportionalDemand, 1, BigDecimal.ROUND_HALF_UP));
        // variance = ((currentYearCollection -
        // lastYearCollection)*100)/lastYearCollection
        BigDecimal variation ;
        if (collectionIndexDetails.getLastYearTillDateColl().compareTo(BigDecimal.ZERO) == 0)
            variation = WaterTaxConstants.BIGDECIMAL_100;
        else
            variation = collectionIndexDetails.getCurrentYearTillDateColl()
                    .subtract(collectionIndexDetails.getLastYearTillDateColl())
                    .multiply(WaterTaxConstants.BIGDECIMAL_100)
                    .divide(collectionIndexDetails.getLastYearTillDateColl(), 1, BigDecimal.ROUND_HALF_UP);
        collectionIndexDetails.setLastYearVar(variation);
        timeTaken = System.currentTimeMillis() - startTime;
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Time taken for setting values in getConsolidatedDemandInfo() is (millisecs) : " + timeTaken);
        final ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setErrorCode(WaterTaxConstants.THIRD_PARTY_ERR_CODE_SUCCESS);
        errorDetails.setErrorMessage(WaterTaxConstants.THIRD_PARTY_ERR_MSG_SUCCESS);
        collectionIndexDetails.setErrorDetails(errorDetails);
        collectionIndexDetailsList.add(collectionIndexDetails);
        return collectionIndexDetailsList;
    }

    /**
     * Returns total demand from WaterCharge index, based on input filters
     *
     * @param waterChargedashBoardRequest
     * @return
     */
    public BigDecimal getTotalDemandBasedOnInputFilters(final WaterChargeDashBoardRequest waterChargedashBoardRequest) {
        final BoolQueryBuilder boolQuery = waterChargeCollDocService.prepareWhereClause(waterChargedashBoardRequest,
                null);
        final SearchQuery searchQueryColl = new NativeSearchQueryBuilder()
                .withIndices(WATER_TAX_INDEX_NAME).withQuery(boolQuery).addAggregation(AggregationBuilders
                        .sum(WaterTaxConstants.WATERCHARGETOTALDEMAND).field(WaterTaxConstants.WATERCHARGETOTALDEMAND))
                .build();

        final Aggregations collAggr = elasticsearchTemplate.query(searchQueryColl,
                response -> response.getAggregations());

        final Sum aggr = collAggr.get(WaterTaxConstants.WATERCHARGETOTALDEMAND);
        return BigDecimal.valueOf(aggr.getValue()).setScale(0, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Returns List of Top Ten Tax Performers
     *
     * @param waterChargedashBoardRequest
     * @return
     */
    public TaxPayerResponseDetails getTopTenTaxPerformers(
            final WaterChargeDashBoardRequest waterChargedashBoardRequest) {
        List<TaxPayerDetails> taxProducers;
        List<TaxPayerDetails> taxAchievers;
        final TaxPayerResponseDetails topTaxPerformers = new TaxPayerResponseDetails();
        if (StringUtils.isNotBlank(waterChargedashBoardRequest.getType()) && waterChargedashBoardRequest.getType()
                .equalsIgnoreCase(PropertyTaxConstants.DASHBOARD_GROUPING_BILLCOLLECTORWISE)) {
            // Fetch the ward wise data for the filters
            final List<TaxPayerDetails> wardWiseTaxProducers = returnUlbWiseAggregationResults(
                    waterChargedashBoardRequest, WATER_TAX_INDEX_NAME, false, TOTAL_COLLECTION, 250, true);
            final Map<String, TaxPayerDetails> wardWiseTaxPayersDetails = new HashMap<>();
            final Map<String, List<TaxPayerDetails>> billCollectorWiseMap = new LinkedHashMap<>();
            final List<TaxPayerDetails> taxPayerDetailsList = new ArrayList<>();
            final List<TaxPayerDetails> billCollectorWiseTaxPayerDetails = new ArrayList<>();
            // Get ward wise tax payers details
            prepareWardWiseTaxPayerDetails(wardWiseTaxProducers, wardWiseTaxPayersDetails);
            // Group the revenue ward details by bill collector
            prepareBillCollectorWiseMapData(waterChargedashBoardRequest, wardWiseTaxPayersDetails, billCollectorWiseMap,
                    taxPayerDetailsList);
            // Prepare Bill Collector wise tax payers details
            prepareTaxersInfoForBillCollectors(waterChargedashBoardRequest, billCollectorWiseMap,
                    billCollectorWiseTaxPayerDetails);
            taxProducers = getTaxPayersForBillCollector(false, billCollectorWiseTaxPayerDetails, true);
            taxAchievers = getTaxPayersForBillCollector(false, billCollectorWiseTaxPayerDetails, false);
        } else {
            taxProducers = returnUlbWiseAggregationResults(waterChargedashBoardRequest, WATER_TAX_INDEX_NAME, false,
                    TOTAL_COLLECTION, 10, false);
            taxAchievers = returnUlbWiseAggregationResults(waterChargedashBoardRequest, WATER_TAX_INDEX_NAME, false,
                    TOTAL_COLLECTION, 120, false);
        }
        topTaxPerformers.setProducers(taxProducers);
        topTaxPerformers.setAchievers(taxAchievers);

        return topTaxPerformers;
    }

    /**
     * Returns List of Bottom Ten Tax Performers
     *
     * @param waterChargedashBoardRequest
     * @return
     */
    public TaxPayerResponseDetails getBottomTenTaxPerformers(
            final WaterChargeDashBoardRequest waterChargedashBoardRequest) {
        final TaxPayerResponseDetails topTaxPerformers = new TaxPayerResponseDetails();
        List<TaxPayerDetails> taxProducers;
        List<TaxPayerDetails> taxAchievers;

        if (StringUtils.isNotBlank(waterChargedashBoardRequest.getType()) && waterChargedashBoardRequest.getType()
                .equalsIgnoreCase(PropertyTaxConstants.DASHBOARD_GROUPING_BILLCOLLECTORWISE)) {
            final List<TaxPayerDetails> wardWiseTaxProducers = returnUlbWiseAggregationResults(
                    waterChargedashBoardRequest, WATER_TAX_INDEX_NAME, false, TOTAL_COLLECTION, 250, true);
            final Map<String, TaxPayerDetails> wardWiseTaxPayersDetails = new HashMap<>();
            final Map<String, List<TaxPayerDetails>> billCollectorWiseMap = new LinkedHashMap<>();
            final List<TaxPayerDetails> taxPayerDetailsList = new ArrayList<>();
            final List<TaxPayerDetails> billCollectorWiseTaxPayerDetails = new ArrayList<>();
            // Get ward wise tax payers details
            prepareWardWiseTaxPayerDetails(wardWiseTaxProducers, wardWiseTaxPayersDetails);
            // Group the revenue ward details by bill collector
            prepareBillCollectorWiseMapData(waterChargedashBoardRequest, wardWiseTaxPayersDetails, billCollectorWiseMap,
                    taxPayerDetailsList);
            // Prepare Bill Collector wise tax payers details
            prepareTaxersInfoForBillCollectors(waterChargedashBoardRequest, billCollectorWiseMap,
                    billCollectorWiseTaxPayerDetails);
            taxProducers = getTaxPayersForBillCollector(true, billCollectorWiseTaxPayerDetails, true);
            taxAchievers = getTaxPayersForBillCollector(true, billCollectorWiseTaxPayerDetails, false);
        } else {

            taxProducers = returnUlbWiseAggregationResults(waterChargedashBoardRequest, WATER_TAX_INDEX_NAME, true,
                    TOTAL_COLLECTION, 10, false);
            taxAchievers = returnUlbWiseAggregationResults(waterChargedashBoardRequest, WATER_TAX_INDEX_NAME, true,
                    TOTAL_COLLECTION, 120, false);
        }
        topTaxPerformers.setProducers(taxProducers);
        topTaxPerformers.setAchievers(taxAchievers);

        return topTaxPerformers;
    }

    /**
     * Returns Top Ten with ULB wise grouping and total amount aggregation
     *
     * @param waterChargedashBoardRequest
     * @param indexName
     * @param order
     * @param orderingAggregationName
     * @return
     */
    public List<TaxPayerDetails> returnUlbWiseAggregationResults(
            final WaterChargeDashBoardRequest waterChargedashBoardRequest, final String indexName, final Boolean order,
            final String orderingAggregationName, final int size, final boolean isBillCollectorWise) {
        final List<TaxPayerDetails> taxPayers = new ArrayList<>();
        final BoolQueryBuilder boolQuery = waterChargeCollDocService.prepareWhereClause(waterChargedashBoardRequest,
                null);

        // orderingAggregationName is the aggregation name by which we have to
        // order the results
        // IN this case can be one of "totaldemand" or TOTAL_COLLECTION or
        // "avg_achievement"
        String groupingField;
        if (StringUtils.isNotBlank(waterChargedashBoardRequest.getUlbCode())
                || StringUtils.isNotBlank(waterChargedashBoardRequest.getType())
                        && waterChargedashBoardRequest.getType().equals(WaterTaxConstants.DASHBOARD_GROUPING_WARDWISE))
            groupingField = WaterTaxConstants.REVENUEWARDAGGREGATIONFIELD;
        else
            groupingField = WaterTaxConstants.CITYNAMEAGGREGATIONFIELD;

        Long startTime = System.currentTimeMillis();
        @SuppressWarnings("rawtypes")
        AggregationBuilder aggregation;
        SearchQuery searchQueryColl;
        // Apply the ordering and max results size only if the type is not
        // billcollector
        if (!isBillCollectorWise) {
            aggregation = AggregationBuilders.terms(AGGREGATION_FIELD).field(groupingField).size(size)
                    .order(Terms.Order.aggregation(orderingAggregationName, order))
                    .subAggregation(AggregationBuilders.sum(TOTALDEMAND).field(TOTAL_DEMAND))
                    .subAggregation(AggregationBuilders.sum(TOTAL_COLLECTION).field("totalCollection"));
            searchQueryColl = new NativeSearchQueryBuilder().withIndices(indexName).withQuery(boolQuery)
                    .addAggregation(aggregation).build();
        } else {
            aggregation = AggregationBuilders.terms(AGGREGATION_FIELD).field(groupingField).size(250)
                    .subAggregation(AggregationBuilders.sum(TOTALDEMAND).field(TOTAL_DEMAND))
                    .subAggregation(AggregationBuilders.sum(TOTAL_COLLECTION).field("totalCollection"));
            searchQueryColl = new NativeSearchQueryBuilder().withIndices(indexName).withQuery(boolQuery)
                    .withPageable(new PageRequest(0, 250)).addAggregation(aggregation).build();
        }

        final Aggregations collAggr = elasticsearchTemplate.query(searchQueryColl,
                response -> response.getAggregations());

        Long timeTaken = System.currentTimeMillis() - startTime;
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Time taken by ulbWiseAggregations is (millisecs) : " + timeTaken);
        TaxPayerDetails taxDetail;
        startTime = System.currentTimeMillis();
        final Date fromDate = new DateTime().withMonthOfYear(4).dayOfMonth().withMinimumValue().toDate();
        final Date toDate = org.apache.commons.lang3.time.DateUtils.addDays(new Date(), 1);
        final Date lastYearFromDate = org.apache.commons.lang3.time.DateUtils.addYears(fromDate, -1);
        final Date lastYearToDate = org.apache.commons.lang3.time.DateUtils.addYears(toDate, -1);
        final StringTerms totalAmountAggr = collAggr.get(AGGREGATION_FIELD);
        for (final Terms.Bucket entry : totalAmountAggr.getBuckets()) {
            taxDetail = new TaxPayerDetails();
            taxDetail.setRegionName(waterChargedashBoardRequest.getRegionName());
            taxDetail.setDistrictName(waterChargedashBoardRequest.getDistrictName());
            taxDetail.setUlbGrade(waterChargedashBoardRequest.getUlbGrade());
            final String fieldName = String.valueOf(entry.getKey());
            if (WaterTaxConstants.REVENUEWARDAGGREGATIONFIELD.equals(groupingField))
                taxDetail.setWardName(fieldName);
            else
                taxDetail.setUlbName(fieldName);
            // Proportional Demand = (totalDemand/12)*noOfmonths
            final int noOfMonths = DateUtils.noOfMonths(fromDate, toDate) + 1;
            final Sum totalDemandAggregation = entry.getAggregations().get(TOTALDEMAND);
            final Sum totalCollectionAggregation = entry.getAggregations().get(TOTAL_COLLECTION);
            final BigDecimal totalDemandValue = BigDecimal.valueOf(totalDemandAggregation.getValue()).setScale(0,
                    BigDecimal.ROUND_HALF_UP);
            final BigDecimal totalCollections = BigDecimal.valueOf(totalCollectionAggregation.getValue()).setScale(0,
                    BigDecimal.ROUND_HALF_UP);
            final BigDecimal proportionalDemand = totalDemandValue
                    .divide(BigDecimal.valueOf(12), BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(noOfMonths));
            taxDetail.setTotalDmd(totalDemandValue);
            taxDetail.setCurrentYearTillDateColl(totalCollections);
            taxDetail.setCurrentYearTillDateDmd(proportionalDemand);
            taxDetail.setAchievement(totalCollections.multiply(WaterTaxConstants.BIGDECIMAL_100)
                    .divide(proportionalDemand, 1, BigDecimal.ROUND_HALF_UP));
            taxDetail.setCurrentYearTillDateBalDmd(proportionalDemand.subtract(totalCollections));
            final BigDecimal lastYearCollection = waterChargeCollDocService.getCollectionBetweenDates(
                    waterChargedashBoardRequest, lastYearFromDate, lastYearToDate, fieldName);
            // variance = ((currentYearCollection -
            // lastYearCollection)*100)/lastYearCollection
            BigDecimal variation;
            if (lastYearCollection.compareTo(BigDecimal.ZERO) == 0)
                variation = WaterTaxConstants.BIGDECIMAL_100;
            else
                variation = totalCollections.subtract(lastYearCollection).multiply(WaterTaxConstants.BIGDECIMAL_100)
                        .divide(lastYearCollection, 1, BigDecimal.ROUND_HALF_UP);
            taxDetail.setLastYearVar(variation);
            taxPayers.add(taxDetail);
        }
        timeTaken = System.currentTimeMillis() - startTime;
        if (LOGGER.isDebugEnabled())
            LOGGER.debug(
                    "Time taken for setting values in returnUlbWiseAggregationResults() is (millisecs) : " + timeTaken);
        return returnTopResults(taxPayers, size, order);
    }

    private List<TaxPayerDetails> returnTopResults(final List<TaxPayerDetails> taxPayers, final int size,
            final Boolean order) {
        if (size > 10) {
            if (order)
                Collections.sort(taxPayers);
            else
                Collections.sort(taxPayers, Collections.reverseOrder());

            return taxPayers.subList(0, taxPayers.size() < 10 ? taxPayers.size() : 10);
        }
        return taxPayers;
    }

    /**
     * Prepare ward wise tax payers details - Map of ward name and tax paers
     * bean
     *
     * @param wardWiseTaxProducers
     * @param wardWiseTaxPayersDetails
     */
    private void prepareWardWiseTaxPayerDetails(final List<TaxPayerDetails> wardWiseTaxProducers,
            final Map<String, TaxPayerDetails> wardWiseTaxPayersDetails) {
        for (final TaxPayerDetails taxPayers : wardWiseTaxProducers)
            wardWiseTaxPayersDetails.put(taxPayers.getWardName(), taxPayers);
    }

    /**
     * Prepare a Map of Bill Collector names and the tax payers list for their
     * respective wards
     *
     * @param waterChargedashBoardRequest
     * @param wardWiseTaxPayersDetails
     * @param billCollectorWiseMap
     * @param taxPayerDetailsList
     */
    private void prepareBillCollectorWiseMapData(final WaterChargeDashBoardRequest waterChargedashBoardRequest,
            final Map<String, TaxPayerDetails> wardWiseTaxPayersDetails,
            final Map<String, List<TaxPayerDetails>> billCollectorWiseMap,
            final List<TaxPayerDetails> taxPayerDetailsList) {
        List<TaxPayerDetails> taxPayerDetailsListTemp;

        final List<BillCollectorIndex> billCollectorsList = waterChargeCollDocService
                .getBillCollectorDetails(waterChargedashBoardRequest);
        for (final BillCollectorIndex billCollIndex : billCollectorsList)
            if (wardWiseTaxPayersDetails.get(billCollIndex.getRevenueWard()) != null)
                if (billCollectorWiseMap.isEmpty()) {
                    taxPayerDetailsList.add(wardWiseTaxPayersDetails.get(billCollIndex.getRevenueWard()));
                    billCollectorWiseMap.put(billCollIndex.getBillCollector(), taxPayerDetailsList);
                } else if (!billCollectorWiseMap.containsKey(billCollIndex.getBillCollector())) {
                    taxPayerDetailsListTemp = new ArrayList<>();
                    taxPayerDetailsListTemp.add(wardWiseTaxPayersDetails.get(billCollIndex.getRevenueWard()));
                    billCollectorWiseMap.put(billCollIndex.getBillCollector(), taxPayerDetailsListTemp);
                } else
                    billCollectorWiseMap.get(billCollIndex.getBillCollector())
                            .add(wardWiseTaxPayersDetails.get(billCollIndex.getRevenueWard()));
    }

    /**
     * Prepares the Producers and Acheivers list - Bill Collector wise
     *
     * @param waterChargedashBoardRequest
     * @param order
     * @param wardWiseTaxProducers
     * @param billCollectorWiseTaxPayerDetails
     * @param isForProducers
     * @return
     */
    private List<TaxPayerDetails> getTaxPayersForBillCollector(final boolean order,
            final List<TaxPayerDetails> billCollectorWiseTaxPayerDetails, final boolean isForProducers) {
        final Map<BigDecimal, TaxPayerDetails> sortedTaxersMap = new HashMap<>();
        // For propducers, prepare sorted list of totalCollection
        // For achievers, prepare sorted list of achievement
        if (isForProducers)
            for (final TaxPayerDetails payerDetails : billCollectorWiseTaxPayerDetails)
                sortedTaxersMap.put(payerDetails.getCurrentYearTillDateColl(), payerDetails);
        else
            for (final TaxPayerDetails payerDetails : billCollectorWiseTaxPayerDetails)
                sortedTaxersMap.put(payerDetails.getAchievement(), payerDetails);

        final List<BigDecimal> sortedList = new ArrayList<>(sortedTaxersMap.keySet());
        // Decides whether API should return in ascending or descending order
        if (order)
            Collections.sort(sortedList);
        else
            Collections.sort(sortedList, Collections.reverseOrder());

        final List<TaxPayerDetails> taxersResult = new ArrayList<>();
        for (final BigDecimal amount : sortedList)
            taxersResult.add(sortedTaxersMap.get(amount));

        if (taxersResult.size() > 10)
            return taxersResult.subList(0, taxersResult.size() < 10 ? taxersResult.size() : 10);
        else
            return taxersResult;
    }

    /**
     * Prepare list of TaxPayerDetails for each bill collector by summing up the
     * values in each ward for the respective bil collector
     *
     * @param waterChargedashBoardRequest
     * @param billCollectorWiseMap
     * @param billCollectorWiseTaxPayerDetails
     */
    private void prepareTaxersInfoForBillCollectors(final WaterChargeDashBoardRequest waterChargedashBoardRequest,
            final Map<String, List<TaxPayerDetails>> billCollectorWiseMap,
            final List<TaxPayerDetails> billCollectorWiseTaxPayerDetails) {
        BigDecimal cytdColl;
        BigDecimal lytdColl ;
        BigDecimal cytdDmd ;
        BigDecimal totalDmd ;
        TaxPayerDetails taxPayerDetails;
        for (final Entry<String, List<TaxPayerDetails>> entry : billCollectorWiseMap.entrySet()) {
            taxPayerDetails = new TaxPayerDetails();
            cytdColl = BigDecimal.ZERO;
            lytdColl = BigDecimal.ZERO;
            cytdDmd = BigDecimal.ZERO;
            totalDmd = BigDecimal.ZERO;
            for (final TaxPayerDetails taxPayer : entry.getValue()) {
                totalDmd = totalDmd.add(taxPayer.getTotalDmd() == null ? BigDecimal.ZERO : taxPayer.getTotalDmd());
                cytdColl = cytdColl.add(taxPayer.getCurrentYearTillDateColl() == null ? BigDecimal.ZERO
                        : taxPayer.getCurrentYearTillDateColl());
                cytdDmd = cytdDmd.add(taxPayer.getCurrentYearTillDateDmd() == null ? BigDecimal.ZERO
                        : taxPayer.getCurrentYearTillDateDmd());
                lytdColl = lytdColl.add(taxPayer.getLastYearTillDateColl() == null ? BigDecimal.ZERO
                        : taxPayer.getLastYearTillDateColl());
            }
            taxPayerDetails.setBillCollector(entry.getKey());
            taxPayerDetails.setRegionName(waterChargedashBoardRequest.getRegionName());
            taxPayerDetails.setDistrictName(waterChargedashBoardRequest.getDistrictName());
            taxPayerDetails.setUlbGrade(waterChargedashBoardRequest.getUlbGrade());
            taxPayerDetails.setCurrentYearTillDateColl(cytdColl);
            taxPayerDetails.setCurrentYearTillDateDmd(cytdDmd);
            taxPayerDetails.setCurrentYearTillDateBalDmd(cytdDmd.subtract(cytdColl));
            taxPayerDetails.setTotalDmd(totalDmd);
            taxPayerDetails.setLastYearTillDateColl(lytdColl);
            taxPayerDetails.setAchievement(
                    cytdColl.multiply(WaterTaxConstants.BIGDECIMAL_100).divide(cytdDmd, 1, BigDecimal.ROUND_HALF_UP));
            if (lytdColl.compareTo(BigDecimal.ZERO) > 0)
                cytdColl.subtract(lytdColl).multiply(WaterTaxConstants.BIGDECIMAL_100).divide(lytdColl, 1,
                        BigDecimal.ROUND_HALF_UP);
            billCollectorWiseTaxPayerDetails.add(taxPayerDetails);
        }
    }
   /* public List<TaxDefaulters> getTopDefaulters(PropertyTaxDefaultersRequest propertyTaxDefaultersRequest) {
        Long startTime = System.currentTimeMillis();
        BoolQueryBuilder boolQuery = filterBasedOnRequest(propertyTaxDefaultersRequest);
        boolQuery = boolQuery.mustNot(QueryBuilders.matchQuery(CITY_NAME, "Guntur"))
                .mustNot(QueryBuilders.matchQuery(CITY_NAME, "Vijayawada"))
                .mustNot(QueryBuilders.matchQuery(CITY_NAME, "Visakhapatnam"))
                .filter(QueryBuilders.matchQuery(IS_ACTIVE, true))
                .filter(QueryBuilders.matchQuery(IS_EXEMPTED, false));

        SearchQuery searchQuery = new NativeSearchQueryBuilder().withIndices(PROPERTY_TAX_INDEX_NAME)
                .withQuery(boolQuery).withSort(new FieldSortBuilder("totalBalance").order(SortOrder.DESC))
                .withPageable(new PageRequest(0, 100)).build();

        final Page<PropertyTaxIndex> propertyTaxRecords = elasticsearchTemplate.queryForPage(searchQuery,
                PropertyTaxIndex.class);
        Long timeTaken = System.currentTimeMillis() - startTime;
        LOGGER.debug("Time taken by defaulters aggregation is : " + timeTaken + MILLISECS);

        List<TaxDefaulters> taxDefaulters = new ArrayList<>();
        TaxDefaulters taxDfaulter;
        startTime = System.currentTimeMillis();
        for (PropertyTaxIndex property : propertyTaxRecords) {
            taxDfaulter = new TaxDefaulters();
            taxDfaulter.setOwnerName(property.getConsumerName());
            taxDfaulter.setPropertyType(property.getPropertyType());
            taxDfaulter.setUlbName(property.getCityName());
            taxDfaulter.setBalance(BigDecimal.valueOf(property.getTotalBalance()));
            taxDfaulter.setPeriod(StringUtils.EMPTY);
            taxDefaulters.add(taxDfaulter);
        }
        timeTaken = System.currentTimeMillis() - startTime;
        LOGGER.debug("Time taken for setting values in getTopDefaulters() is : " + timeTaken + MILLISECS);
        return taxDefaulters;
    }
    private BoolQueryBuilder filterBasedOnRequest(PropertyTaxDefaultersRequest propertyTaxDefaultersRequest) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .filter(QueryBuilders.rangeQuery(TOTAL_DEMAND).from(0).to(null));
        if (StringUtils.isNotBlank(propertyTaxDefaultersRequest.getRegionName()))
            boolQuery = boolQuery
                    .filter(QueryBuilders.matchQuery(REGION_NAME, propertyTaxDefaultersRequest.getRegionName()));
        if (StringUtils.isNotBlank(propertyTaxDefaultersRequest.getDistrictName()))
            boolQuery = boolQuery
                    .filter(QueryBuilders.matchQuery(DISTRICT_NAME, propertyTaxDefaultersRequest.getDistrictName()));
        if (StringUtils.isNotBlank(propertyTaxDefaultersRequest.getUlbCode()))
            boolQuery = boolQuery
                    .filter(QueryBuilders.matchQuery(CITY_CODE, propertyTaxDefaultersRequest.getUlbCode()));
        if (StringUtils.isNotBlank(propertyTaxDefaultersRequest.getUlbGrade()))
            boolQuery = boolQuery
                    .filter(QueryBuilders.matchQuery(CITY_GRADE, propertyTaxDefaultersRequest.getUlbGrade()));
        if (StringUtils.isNotBlank(propertyTaxDefaultersRequest.getWardName()))
            boolQuery = boolQuery
                    .filter(QueryBuilders.matchQuery(REVENUE_WARD, propertyTaxDefaultersRequest.getWardName()));
        if (StringUtils.isNotBlank(propertyTaxDefaultersRequest.getType())) {
            if (propertyTaxDefaultersRequest.getType().equalsIgnoreCase(DASHBOARD_GROUPING_REGIONWISE)
                    && StringUtils.isNotBlank(propertyTaxDefaultersRequest.getRegionName()))
                boolQuery = boolQuery
                        .filter(QueryBuilders.matchQuery(REGION_NAME, propertyTaxDefaultersRequest.getRegionName()));
            else if (propertyTaxDefaultersRequest.getType().equalsIgnoreCase(DASHBOARD_GROUPING_DISTRICTWISE)
                    && StringUtils.isNotBlank(propertyTaxDefaultersRequest.getDistrictName()))
                boolQuery = boolQuery.filter(
                        QueryBuilders.matchQuery(DISTRICT_NAME, propertyTaxDefaultersRequest.getDistrictName()));
            else if (propertyTaxDefaultersRequest.getType().equalsIgnoreCase(DASHBOARD_GROUPING_CITYWISE)
                    && StringUtils.isNotBlank(propertyTaxDefaultersRequest.getUlbCode()))
                boolQuery = boolQuery
                        .filter(QueryBuilders.matchQuery(CITY_CODE, propertyTaxDefaultersRequest.getUlbCode()));
            else if (propertyTaxDefaultersRequest.getType().equalsIgnoreCase(DASHBOARD_GROUPING_GRADEWISE)
                    && StringUtils.isNotBlank(propertyTaxDefaultersRequest.getUlbGrade()))
                boolQuery = boolQuery
                        .filter(QueryBuilders.matchQuery(CITY_GRADE, propertyTaxDefaultersRequest.getUlbGrade()));
        }

        return boolQuery;
    }*/

}