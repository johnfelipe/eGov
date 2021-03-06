<%--
  ~ eGov suite of products aim to improve the internal efficiency,transparency,
  ~    accountability and the service delivery of the government  organizations.
  ~
  ~     Copyright (C) <2015>  eGovernments Foundation
  ~
  ~     The updated version of eGov suite of products as by eGovernments Foundation
  ~     is available at http://www.egovernments.org
  ~
  ~     This program is free software: you can redistribute it and/or modify
  ~     it under the terms of the GNU General Public License as published by
  ~     the Free Software Foundation, either version 3 of the License, or
  ~     any later version.
  ~
  ~     This program is distributed in the hope that it will be useful,
  ~     but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~     GNU General Public License for more details.
  ~
  ~     You should have received a copy of the GNU General Public License
  ~     along with this program. If not, see http://www.gnu.org/licenses/ or
  ~     http://www.gnu.org/licenses/gpl.html .
  ~
  ~     In addition to the terms of the GPL license to be adhered to in using this
  ~     program, the following additional terms are to be complied with:
  ~
  ~         1) All versions of this program, verbatim or modified must carry this
  ~            Legal Notice.
  ~
  ~         2) Any misrepresentation of the origin of the material is prohibited. It
  ~            is required that all modified versions of this material be marked in
  ~            reasonable ways as different from the original version.
  ~
  ~         3) This license does not grant any rights to any user of the program
  ~            with regards to rights under trademark law for use of the trade names
  ~            or trademarks of eGovernments Foundation.
  ~
  ~   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
  --%>

<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn"%>


<div class="row" id="page-content">
	<div class="col-md-12">
		<c:if test="${not empty message}">
			<div class="alert alert-success" role="alert">
				<spring:message code="${message}" />
			</div>
		</c:if>

		<form:form id="mutationFeeForm" commandName="mutationFeeDetails"
			class="form-horizontal form-groups-bordered">
			<spring:hasBindErrors name="mutationFeeDetails">
				<div class="alert alert-danger">
					<form:errors path="*" cssClass="error-msg add-margin" />
					<br />
				</div>
			</spring:hasBindErrors>

			<div class="row">
				<div class="col-md-12">
					<div class="panel-group">
						<div class="panel panel-primary">
							<div class="panel-heading slide-history-menu">
								<div class="panel-title">
									<strong><spring:message code="lbl.mutationfee.modify" /></strong>
								</div>

							</div>
							<div class="panel-body history-slide">

								<div class="form-group col-sm-12 col-sm-offset-0">
									<table class="table table-bordered table-hover"
										id="tblViewMutation">
										<thead>
											<tr>
												<th class="text-left"><spring:message
														code="lbl.slabName" /></th>
												<th class="text-left"><spring:message
														code="lbl.fromVal" /></th>
												<th class="text-left"><spring:message code="lbl.toVal" /></th>
												<th class="text-left"><spring:message
														code="lbl.flatRate" /></th>
												<th class="text-left"><spring:message
														code="lbl.percentRate" /></th>
												<th class="text-left"><spring:message code="lbl.recVal" /></th>
												<th class="text-left"><spring:message
														code="lbl.addRate" /></th>
												<th class="text-left"><spring:message
														code="lbl.effectiveFrom" /></th>

												<th class="text-left"><spring:message
														code="lbl.effectiveTo" /></th>
												<th class="text-left"><spring:message code="lbl.action" /></th>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="mutationfee" items="${mutationfee}"
												varStatus="loop">
												<tr>
													<c:set value="${mutationfee.lowLimit}" var="low" />
													<c:set value="${mutationfee.highLimit}" var="high" />
													<fmt:parseNumber var="lowlt" integerOnly="true"
														type="number" value="${low}" />
													<fmt:parseNumber var="highlt" integerOnly="true"
														type="number" value="${high}" />
													<td style="display: none;"><input type="hidden"
														id="key" value="<c:out value="${mutationfee.id}"/>" /></td>
													</td>
													<td><c:out value="${mutationfee.slabName}" /></td>
													<td><c:out value="${lowlt}" /></td>
													<td><c:out value="${highlt}" /></td>
													<c:choose>
														<c:when test="${not empty mutationfee.flatAmount}">
															<td><c:out value="${mutationfee.flatAmount}" /></td>
														</c:when>
														<c:otherwise>
															<td><c:out value="N/A" /></td>
														</c:otherwise>
													</c:choose>
													<c:choose>
														<c:when test="${not empty mutationfee.percentage}">
															<td><c:out value="${mutationfee.percentage}" /></td>
														</c:when>
														<c:otherwise>
															<td><c:out value="N/A" /></td>
														</c:otherwise>
													</c:choose>
													<c:choose>
														<c:when test="${ not empty mutationfee.recursiveFactor}">
															<td><c:out value="${mutationfee.recursiveFactor}" /></td>
														</c:when>
														<c:otherwise>
															<td><c:out value="N/A" /></td>
														</c:otherwise>
													</c:choose>
													<c:choose>
														<c:when test="${not empty mutationfee.recursiveAmount}">
															<td><c:out value="${mutationfee.recursiveAmount}" /></td>
														</c:when>
														<c:otherwise>
															<td><c:out value="N/A" /></td>
														</c:otherwise>
													</c:choose>

													<fmt:formatDate value="${mutationfee.fromDate}"
														pattern="dd/MM/yyyy" var="fromdate" />
													<td><c:out value="${fromdate}" /></td>
													<fmt:formatDate value="${mutationfee.toDate}"
														pattern="dd/MM/yyyy" var="todate" />

													<td><input type="text" name="toDate" id="toDt"
														class="form-control effToDate" value="${todate}"  disabled /></td>

													<td><Button class="btn btn-primary btnedit"
															data-idx='${loop.index}'>
															<spring:message code="lbl.button.edit" />
														</Button></td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="row">
				<div class="text-center">
					<Button class="btn btn-primary btnsave" id="save" disabled>
						<spring:message code="lbl.button.save" />
					</Button>
					<Button class="btn btn-primary btnaddnewslab" id="addnewslab" >
						<spring:message code="lbl.button.add" />
					</Button>
					<a href="javascript:void(0)" class="btn btn-default"
						onclick="self.close()"><spring:message code="lbl.close" /></a>
				</div>
			</div>
		</form:form>
	</div>
</div>
<script type="text/javascript"
	src="<cdn:url value='/resources/js/app/mutationfee.js?rnd=${app_release_no}'/>"></script>