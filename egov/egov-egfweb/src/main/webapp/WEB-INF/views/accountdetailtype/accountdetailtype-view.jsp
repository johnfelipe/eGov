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
<%@ include file="/includes/taglibs.jsp"%>
<div class="main-content">
	<div class="row">
		<div class="col-md-12">
			<div class="panel panel-primary" data-collapsed="0">
				<div class="panel-heading">
					<div class="panel-title">Accountdetailtype</div>
				</div>
				<div class="panel-body custom">
					<div class="row add-border">
						<div class="col-xs-3 add-margin">
							<spring:message code="lbl.name" />
						</div>
						<div class="col-sm-3 add-margin view-content">${accountdetailtype.name}</div>
						<div class="col-xs-3 add-margin">
							<spring:message code="lbl.description" />
						</div>
						<div class="col-sm-3 add-margin view-content">${accountdetailtype.description}</div>
					</div>
					<div class="row add-border">
						<div class="col-xs-3 add-margin">
							<spring:message code="lbl.tablename" />
						</div>
						<div class="col-sm-3 add-margin view-content">${accountdetailtype.tablename}</div>
						<div class="col-xs-3 add-margin">
							<spring:message code="lbl.columnname" />
						</div>
						<div class="col-sm-3 add-margin view-content">${accountdetailtype.columnname}</div>
					</div>
					<div class="row add-border">
						<div class="col-xs-3 add-margin">
							<spring:message code="lbl.attributename" />
						</div>
						<div class="col-sm-3 add-margin view-content">${accountdetailtype.attributename}</div>
						<div class="col-xs-3 add-margin">
							<spring:message code="lbl.nbroflevels" />
						</div>
						<div class="col-sm-3 add-margin view-content">${accountdetailtype.nbroflevels}</div>
					</div>
					<div class="row add-border">
						<div class="col-xs-3 add-margin">
							<spring:message code="lbl.isactive" />
						</div>
						<div class="col-sm-3 add-margin view-content">${accountdetailtype.isactive}</div>
						<div class="col-xs-3 add-margin">
							<spring:message code="lbl.fullqualifiedname" />
						</div>
						<div class="col-sm-3 add-margin view-content">${accountdetailtype.fullQualifiedName}</div>
					</div>

				</div>
			</div>
		</div>
	</div>
</div>
<div class="row text-center">
	<div class="add-margin">
		<a href="javascript:void(0)" class="btn btn-default"
			onclick="self.close()">Close</a>
	</div>
</div>