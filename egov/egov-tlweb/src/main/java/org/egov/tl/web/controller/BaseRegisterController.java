/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *     accountability and the service delivery of the government  organizations.
 *
 *      Copyright (C) 2016  eGovernments Foundation
 *
 *      The updated version of eGov suite of products as by eGovernments Foundation
 *      is available at http://www.egovernments.org
 *
 *      This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with this program. If not, see http://www.gnu.org/licenses/ or
 *      http://www.gnu.org/licenses/gpl.html .
 *
 *      In addition to the terms of the GPL license to be adhered to in using this
 *      program, the following additional terms are to be complied with:
 *
 *          1) All versions of this program, verbatim or modified must carry this
 *             Legal Notice.
 *
 *          2) Any misrepresentation of the origin of the material is prohibited. It
 *             is required that all modified versions of this material be marked in
 *             reasonable ways as different from the original version.
 *
 *          3) This license does not grant any rights to any user of the program
 *             with regards to rights under trademark law for use of the trade names
 *             or trademarks of eGovernments Foundation.
 *
 *    In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.tl.web.controller;

import org.egov.infra.admin.master.service.BoundaryService;
import org.egov.tl.entity.dto.BaseRegisterForm;
import org.egov.tl.service.BaseRegisterService;
import org.egov.tl.service.LicenseStatusService;
import org.egov.tl.service.LicenseCategoryService;
import org.egov.tl.utils.Constants;
import org.egov.tl.web.response.adaptor.BaseRegisterResponseAdaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static org.egov.infra.utils.JsonUtils.toJSON;
import static org.egov.tl.utils.Constants.LOCALITY;
import static org.egov.tl.utils.Constants.LOCATION_HIERARCHY_TYPE;


@Controller
@RequestMapping(value = {"baseregister", "/public/baseregister"})
public class BaseRegisterController {
    private final LicenseStatusService licenseStatusService;
    private final BaseRegisterService baseRegisterService;
    private final LicenseCategoryService licenseCategoryService;

    private final BoundaryService boundaryService;

    @Autowired
    public BaseRegisterController(BoundaryService boundaryService, LicenseCategoryService licenseCategoryService, BaseRegisterService baseRegisterService, LicenseStatusService licenseStatusService) {
        this.boundaryService = boundaryService;
        this.licenseCategoryService = licenseCategoryService;
        this.baseRegisterService = baseRegisterService;
        this.licenseStatusService = licenseStatusService;
    }

    @RequestMapping(value = "/search-form", method = RequestMethod.GET)
    public String searchBaseRegister(Model model) {
        model.addAttribute("baseRegisterForm", new BaseRegisterForm());
        model.addAttribute("categories", licenseCategoryService.getCategoriesOrderByName());
        model.addAttribute("subcategories", Collections.emptyList());
        model.addAttribute("statusList", licenseStatusService.findAll());
        model.addAttribute("filters", Arrays.asList("All", "Defaulters"));
        boundaryService.getActiveBoundariesByBndryTypeNameAndHierarchyTypeName(
                LOCALITY, LOCATION_HIERARCHY_TYPE);
        model.addAttribute("wardList", boundaryService.getBoundariesByBndryTypeNameAndHierarchyTypeName(Constants.REVENUE_WARD, Constants.REVENUE_HIERARCHY_TYPE));
        return "baseregister-report";
    }

    @RequestMapping(value = "/search-resultList", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String search(@ModelAttribute BaseRegisterForm baseRegisterForm) throws IOException {
        return new StringBuilder().append("{ \"data\":").append(toJSON(
                baseRegisterService.search(baseRegisterForm),
                BaseRegisterForm.class, BaseRegisterResponseAdaptor.class)).append("}").toString();
    }

}
