/* eGov suite of products aim to improve the internal efficiency,transparency,
   accountability and the service delivery of the government  organizations.

    Copyright (C) <2015>  eGovernments Foundation

    The updated version of eGov suite of products as by eGovernments Foundation
    is available at http://www.egovernments.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see http://www.gnu.org/licenses/ or
    http://www.gnu.org/licenses/gpl.html .

    In addition to the terms of the GPL license to be adhered to in using this
    program, the following additional terms are to be complied with:

	1) All versions of this program, verbatim or modified must carry this
	   Legal Notice.

	2) Any misrepresentation of the origin of the material is prohibited. It
	   is required that all modified versions of this material be marked in
	   reasonable ways as different from the original version.

	3) This license does not grant any rights to any user of the program
	   with regards to rights under trademark law for use of the trade names
	   or trademarks of eGovernments Foundation.

  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.mrs.masters.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.persistence.entity.AbstractAuditable;
import org.egov.infra.persistence.validator.annotation.Unique;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "egmrs_registrationunit")
@Unique(id = "id", tableName = "egmrs_registrationunit", columnName = { "name" }, fields = {
"name" }, enableDfltMsg = true, message = "Already Exist.name should be unique.")
@SequenceGenerator(name = MarriageRegistrationUnit.SEQ_REGISTRATIONUNIT, sequenceName = MarriageRegistrationUnit.SEQ_REGISTRATIONUNIT, allocationSize = 1)
public class MarriageRegistrationUnit extends AbstractAuditable {

    private static final long serialVersionUID = 251802908061985741L;

    public static final String SEQ_REGISTRATIONUNIT = "SEQ_EGMRS_REGISTRATIONUNIT";

    @Id
    @GeneratedValue(generator = SEQ_REGISTRATIONUNIT, strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull

    @Length(max = 100)
    private String name;

    @NotNull
    @Length(max = 20)
    @Column(name = "code", updatable = false)
    private String code;

    @NotNull
    private String address;

    @NotNull
    private Boolean isMainRegistrationUnit;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "zone")
    private Boundary zone;

    @NotNull
    private Boolean isActive;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boundary getZone() {
        return zone;
    }

    public void setZone(Boundary zone) {
        this.zone = zone;
    }

    public Boolean getIsMainRegistrationUnit() {
        return isMainRegistrationUnit;
    }

    public void setIsMainRegistrationUnit(Boolean isMainRegistrationUnit) {
        this.isMainRegistrationUnit = isMainRegistrationUnit;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
