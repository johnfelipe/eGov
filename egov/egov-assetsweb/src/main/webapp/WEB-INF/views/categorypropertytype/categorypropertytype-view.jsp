<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
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

<c:choose>
	<c:when
		test="${ not empty assetCategory.getCategoryProperties()}">
		<div class="panel-heading">
			<div class="panel-title" id="customFieldsTitle">Custom Fields Details</div>
		</div>
		<div class="panel-body custom">
		<%-- <c:set var="properties" value="${asset.categoryPropertyTypeList}" /> --%>
		<c:forEach items="${assetCategory.categoryProperties}"
			var="categoryProperties" varStatus="vs">
			<%-- <c:if test="${empty categoryProperties.localText}">
				<c:set var="localText" value="" />
			</c:if>
			<c:if test="${not empty categoryProperties.localText}">
				<c:set var="localText" value="/${categoryProperties.localText}" />
			</c:if> --%>
			<c:if test="${vs.index%2 == 0}">
				<div class="row add-border">
			</c:if>
			<label class="col-xs-3 add-margin">
				${categoryProperties.name}
			</label>
			<div class="col-sm-3 add-margin view-content">
				${categoryProperties.dataType}</div>
			<c:if test="${vs.index%2==1}">
				</div>
			</c:if>
		</c:forEach> 
		</div>
	</c:when>
</c:choose>