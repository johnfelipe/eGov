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


<%@ include file="/includes/taglibs.jsp"%>
<%@ page language="java"%>
<html>
<head>
<title><s:text name="contract.create" /></title>
<script type="text/javascript">

						function onLoadTask() {
							var close = '<s:property value="close"/>';
							var success = '<s:property value="success"/>';

							if (success == 'yes') {
								bootbox.alert("Contract Type Created Successfully");
								} else if((success == 'no')){
									bootbox.alert("Contract Type Could Not be Created");
									}
												
							if (close == 'true') {
								window.close();
							}
						}
						
						function setClose() {
							var close = document.getElementById('close');    
							   close.value = true;
							   return true;
							}
					</script>
</head>
<body onload="onLoadTask();">
	<div class="formmainbox">
		<div class="subheadnew">
			<s:text name="contract.create" />
		</div>

		<s:form name="contractForm" action="contractType" theme="css_xhtml"
			validate="true">
			<s:push value="model">
				<div style="color: red">
					<s:actionmessage theme="css_xhtml" />
					<s:actionerror />
					<s:fielderror />
				</div>
				<%@include file="contractType-form.jsp"%>
				<div class="buttonbottom" align="center">
					<table align="center">
						<tr class="buttonbottom" id="buttondiv" style="align: middle">
							<td><s:submit name="create" value="Save & New"
									method="create" cssClass="buttonsubmit" /></td>
							<td><s:submit name="create" value="Save & Close"
									method="create" cssClass="buttonsubmit" onclick="setClose();" /></td>
							<s:hidden name="close" id="close" />
							<td><input type="button" id="Close" value="Close"
								onclick="javascript:window.close()" class="button" /></td>
						</tr>
					</table>
				</div>
	</div>
	<s:token />
	</s:push>
	</s:form>
	</div>
</body>
</html>
