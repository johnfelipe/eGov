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
package org.egov.services.voucher;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.egov.billsaccounting.services.CreateVoucher;
import org.egov.commons.CVoucherHeader;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.utils.StringUtils;
import org.egov.infra.validation.exception.ValidationError;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.infra.workflow.multitenant.model.WorkflowBean;
import org.egov.infra.workflow.multitenant.model.WorkflowConstants;
import org.egov.utils.FinancialConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exilant.exility.common.TaskFailedException;

@Transactional(readOnly = true)
@Service
public class PreApprovedActionHelper {
    @Autowired
    @Qualifier("journalVoucherActionHelper")
    private JournalVoucherActionHelper journalVoucherActionHelper;
    @Autowired
    @Qualifier("voucherService")
    private VoucherService voucherService;
    @Autowired
    @Qualifier("createVoucher")
    private CreateVoucher createVoucher;

    @Transactional
    public CVoucherHeader createVoucherFromBill(CVoucherHeader voucherHeader, WorkflowBean workflowBean, Long billId,
            String voucherNumber, Date voucherDate) throws ApplicationRuntimeException, SQLException, TaskFailedException {
        try {
            Long voucherHeaderId = createVoucher.createVoucherFromBill(billId.intValue(), null,
                    voucherNumber, voucherDate);
            voucherHeader = voucherService.findById(voucherHeaderId, false);
            voucherHeader =(CVoucherHeader) journalVoucherActionHelper.transitionWorkFlow(voucherHeader, workflowBean);
        }catch (final ValidationException e) {
            if (e.getErrors().get(0).getMessage() != null && !e.getErrors().get(0).getMessage().equals(StringUtils.EMPTY))
                throw new ValidationException(e.getErrors().get(0).getMessage(), e.getErrors().get(0).getMessage());
            else
                throw new ValidationException("Voucher creation failed", "Voucher creation failed");

        } catch (final Exception e) {

            final List<ValidationError> errors = new ArrayList<ValidationError>();
            errors.add(new ValidationError("exp", e.getMessage()));
            throw new ValidationException(errors);
        }
        return voucherHeader;
    }

    @Transactional
    public CVoucherHeader sendForApproval(CVoucherHeader voucherHeader, WorkflowBean workflowBean)
    {
        try {
            if (FinancialConstants.CREATEANDAPPROVE.equalsIgnoreCase(workflowBean.getWorkflowAction())
                    && voucherHeader.getWorkflowId() == null)
            {
                voucherHeader.setStatus(FinancialConstants.CREATEDVOUCHERSTATUS);
            }
            else
            {
               journalVoucherActionHelper.transitionWorkFlow(voucherHeader, workflowBean);
               
            }
            voucherService.persist(voucherHeader);
            if(workflowBean.getWorkflowAction().equalsIgnoreCase(WorkflowConstants.ACTION_APPROVE))
            {
                voucherHeader.setStatus(FinancialConstants.CREATEDVOUCHERSTATUS);
                
            }
            
            
            
            

        } catch (final ValidationException e) {

            final List<ValidationError> errors = new ArrayList<ValidationError>();
            errors.add(new ValidationError("exp", e.getErrors().get(0).getMessage()));
            throw new ValidationException(errors);
        } catch (final Exception e) {

            final List<ValidationError> errors = new ArrayList<ValidationError>();
            errors.add(new ValidationError("exp", e.getMessage()));
            throw new ValidationException(errors);
        }
        return voucherHeader;
    }

}