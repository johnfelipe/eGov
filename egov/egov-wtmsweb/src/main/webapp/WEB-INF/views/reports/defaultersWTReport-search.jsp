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
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn"%>
<div class="row" id="page-content">
	<div class="col-md-12">
		<c:if test="${not empty message}">
			<div class="alert alert-success" role="alert">
				<spring:message code="${message}" />
			</div>
		</c:if>
		<form:form class="form-horizontal form-groups-bordered"
			id="defaultersReportform" modelAttribute="defaultersReport"
			method="get">
			<div class="panel panel-primary" data-collapsed="0">
				<div class="panel-heading">
					<div class="panel-title">
						<spring:message code="title.defaultersReport" />
					</div>
				</div>
				<div class="panel-body custom-form">


					<div class="form-group">
						<label for="field-1" class="col-sm-2 control-label"><spring:message
								code="lbl.ward" /></label>
						<div class="col-sm-3 add-margin">
							<form:select id="ward" name="ward" path=""
								cssClass="form-control" cssErrorClass="form-control error">
								<form:option value="">
									<spring:message code="lbl.select" />
								</form:option>
								<c:forEach items="${revenueWards}" var="ward">
									<form:option value="${ward.id}"> ${ward.name} </form:option>
								</c:forEach>
							</form:select>
						</div>
					</div>
				</div>

				<div class="panel-body custom-form">
					<div class="form-group">
						<label for="field-1" class="col-sm-2 control-label text-right"><spring:message
								code="lbl.fromAmount" /><span class="mandatory"></span></label>
						<div class="col-sm-3 add-margin">
							<form:input class="form-control patternvalidation"
								data-pattern="number" maxlength="10" id="fromAmount"
								path="fromAmount" />
							<form:errors path="fromAmount" cssClass="add-margin error-msg" />
						</div>
						<label for="field-1" class="col-sm-2 control-label text-right"><spring:message
								code="lbl.toAmount" /><span class="mandatory"></span></label>
						<div class="col-sm-3 add-margin">
							<form:input class="form-control patternvalidation"
								data-pattern="number" maxlength="10" id="toAmount"
								path="toAmount" />
							<form:errors path="toAmount" cssClass="add-margin error-msg" />
						</div>
					</div>
				</div>
				<div class="panel-body custom-form">
					<div class="form-group">
						<label class="col-sm-2 control-label text-right"><spring:message
								code="lbl.topdefaulters" /></label>
						<div class="col-sm-3 add-margin">
							<form:select id="topDefaulters" name="topDefaulters" path=""
								cssClass="form-control" cssErrorClass="form-control error">
								<form:option value="">
									<spring:message code="lbl.select" />
								</form:option>
								<c:forEach items="${topDefaultersList}" var="topdefaultersvalue">
									<form:option value="${topdefaultersvalue}"> ${topdefaultersvalue} </form:option>
								</c:forEach>
							</form:select>
						</div>

					</div>
				</div>
			</div>
			<div class="row">
				<div class="text-center">
					<button type="button" class="btn btn-primary"
						id="defaultersReportSearch">
						<spring:message code="lbl.submit" />
					</button>
					<a href="javascript:void(0)" class="btn btn-default"
						data-dismiss="modal" onclick="self.close()"><spring:message
							code="lbl.close" /></a>
				</div>
			</div>

		</form:form>

		<div id="reportgeneration-header"
			class="col-md-12 table-header text-left">
			<fmt:formatDate value="${currentDate}" var="currDate"
				pattern="dd-MM-yyyy" />
			<spring:message code="lbl.reportgeneration" />
			:
			<c:out value="${currDate}"></c:out>
		</div>
		<table class="table table-bordered table-hover multiheadertbl"
			id="defaultersReport-table">
			<tbody>
			<tfoot id="report-footer">
				<tr>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<td>Total</td>
					<td></td>
					<td></td>
					<td></td>
				</tr>
			</tfoot>
			</tbody>
		</table>

	</div>
</div>

<link rel="stylesheet"
	href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/jquery.dataTables.min.css' context='/egi'/>" />
<link rel="stylesheet"
	href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/dataTables.bootstrap.min.css' context='/egi'/>">
<script type="text/javascript"
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/jquery.dataTables.min.js' context='/egi'/>"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/dataTables.bootstrap.js' context='/egi'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/extensions/buttons/dataTables.buttons.min.js' context='/egi'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/extensions/buttons/buttons.bootstrap.min.js' context='/egi'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/extensions/buttons/buttons.flash.min.js' context='/egi'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/extensions/buttons/jszip.min.js' context='/egi'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/extensions/buttons/pdfmake.min.js' context='/egi'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/extensions/buttons/vfs_fonts.js' context='/egi'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/extensions/buttons/buttons.html5.min.js' context='/egi'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/extensions/buttons/buttons.print.min.js' context='/egi'/>"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/global/js/jquery/plugins/jquery.validate.min.js' context='/egi'/>"></script>
<script
	src="<cdn:url value='/resources/js/app/defaulters-report.js?rnd=${app_release_no}'/>"></script>
