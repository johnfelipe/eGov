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

$(document).ready(function(){
	
	
	
	$('#dateofcomp1').show();
	$('#dateofcomp2').show();

	$('input[type=radio][name=judgmentImplIsComplied]').change(function() {
        console.log("Changed called -->", this.value);
    	
        if(this.value === "YES")
        {
        	$('#dateofcomp1').show();
    		$('#dateofcomp2').show();
    		$('#reason').hide();
    		$('#details').hide();
    		$("#apealFields1").hide();
    		$("#apealFields2").hide();
    		$("#contempFields1").hide();
        	$("#contempFields2").hide();
        	
        	$('*[required]').removeAttr('required');
        	
        	$("#dateOfCompliance").attr('required','required');
        	$("#complianceReport").attr('required','required');
        	
        }
        else if(this.value === "NO")
        {
        	$('#reason').show();
    		$('#dateofcomp1').hide();
    		$('#dateofcomp2').hide();
    		$('#details').hide();
    		$("#apealFields1").hide();
    		$("#apealFields2").hide();
    		$("#contempFields1").hide();
        	$("#contempFields2").hide();
        	
        	$('*[required]').removeAttr('required');
        	
        	$("#implementationFailure").prop('selectedIndex', 0);
        	$("#implementationFailure").attr('required','required');
        	
        }
        else if(this.value==="INPROGRESS")
        {
        	$('#details').show();
    		$('#dateofcomp1').hide();
    		$('#dateofcomp2').hide();
    		$('#reason').hide();
    		$("#apealFields1").hide();
    		$("#apealFields2").hide();
    		$("#contempFields1").hide();
        	$("#contempFields2").hide();
        	
        	$('*[required]').removeAttr('required');
        	$("textarea[name='details']").attr('required','required');
        }
    });
	
});

$('#buttonid').click(function() {
	
	if($("#judgmentImplform").valid())
	{
		document.forms[0].submit();
		return true;
		}
	return false;
	
});

loadAppealAndContemptFields();
$('#implementationFailure').change(function(){
	loadAppealAndContemptFields();
});

function loadAppealAndContemptFields(){
	if ($('#implementationFailure :selected').text().localeCompare("Appeal") == 0 ) { 
		$("#apealFields1").show();
		$("#apealFields2").show();
		$("*[name='appeal[0].srNumber']").attr('required', 'required');
		$("*[name='appeal[0].appealFiledOn']").attr('required', 'required');
		$("*[name='appeal[0].appealFiledBy']").attr('required', 'required');
		}
	else{
		$("#apealFields1").hide();
		$("#apealFields2").hide();
		$("*[name='appeal[0].srNumber']").removeAttr('required');
		$("*[name='appeal[0].appealFiledOn']").removeAttr('required');
		$("*[name='appeal[0].appealFiledBy']").removeAttr('required');
		
	}
		
	if($('#implementationFailure :selected').text().localeCompare("Contempt") == 0) {  
		$("#contempFields1").show();
    	$("#contempFields2").show();
    	$("*[name='contempt[0].caNumber']").attr('required', 'required');
    	$("*[name='contempt[0].receivingDate']").attr('required', 'required');
	}else{
		$("#contempFields1").hide();
    	$("#contempFields2").hide();
    	$("*[name='contempt[0].caNumber']").removeAttr('required');
    	$("*[name='contempt[0].receivingDate']").removeAttr('required');
	}
	
}

$('#btnclose').click(function(){
	bootbox.confirm({
	    message: 'Information entered in this screen will be lost if you close this page ? Please confirm if you want to close. ',
	    buttons: {
	        'cancel': {
	            label: 'No',
	            className: 'btn-default pull-right'
	        },
	        'confirm': {
	            label: 'Yes',
	            className: 'btn-danger pull-right'
	        }
	    },
	    callback: function(result) {
	        if (result) {
	             window.close();
	        }
	    }
	});
	
});

