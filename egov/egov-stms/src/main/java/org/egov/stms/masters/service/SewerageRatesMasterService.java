/**
 * eGov suite of products aim to improve the internal efficiency,transparency,
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
package org.egov.stms.masters.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.egov.stms.masters.entity.SewerageRatesMaster;
import org.egov.stms.masters.entity.enums.PropertyType;
import org.egov.stms.masters.pojo.SewerageRatesSearch;
import org.egov.stms.masters.repository.SewerageRatesMasterRepository;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SewerageRatesMasterService {
    SimpleDateFormat myFormat=new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");

    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private SewerageRatesMasterRepository sewerageRatesMasterRepository;

    public SewerageRatesMaster findBy(final Long id) {
        return sewerageRatesMasterRepository.findOne(id);
    }

    @Transactional
    public SewerageRatesMaster create(final SewerageRatesMaster sewerageRatesMaster) {
        return sewerageRatesMasterRepository.save(sewerageRatesMaster);
    }

    @Transactional
    public SewerageRatesMaster update(final SewerageRatesMaster sewerageRatesMaster) {
        return sewerageRatesMasterRepository.save(sewerageRatesMaster);
    }

    public List<SewerageRatesMaster> findAll() {
        return sewerageRatesMasterRepository.findAll(new Sort(Sort.Direction.DESC,"propertyType","fromDate"));
    }

    public List<SewerageRatesMaster> findAllByConnectionType(final PropertyType propertyType) {
        return sewerageRatesMasterRepository.findAllByPropertyType(propertyType);
    }

    public SewerageRatesMaster load(final Long id) {
        return sewerageRatesMasterRepository.getOne(id);
    }

    public SewerageRatesMaster findByPropertyTypeAndFromDateAndActive(final PropertyType propertyType, final Date fromDate,
            final boolean active) {
        return sewerageRatesMasterRepository.findByPropertyTypeAndFromDateAndActive(propertyType, fromDate, active);
    }

    public SewerageRatesMaster findByPropertyTypeAndActive(final PropertyType propertyType,
            final boolean active) {
        return sewerageRatesMasterRepository.findByPropertyTypeAndActive(propertyType, active);
    }

    public Double getSewerageMonthlyRatesByPropertytype(PropertyType propertyType) {
        return sewerageRatesMasterRepository.getSewerageMonthlyRatesByPropertytype(propertyType);
    }
    
public List<SewerageRatesMaster> getLatestActiveRecord(final PropertyType propertyType, final boolean active){
        
        return sewerageRatesMasterRepository.getLatestActiveRecord(propertyType, true, new Date());
    }
    
    public List<Date> findFromDateByPropertyType(final PropertyType propertyType){
        return sewerageRatesMasterRepository.findFromDateByPropertyType(propertyType);
    }
    
    public List<SewerageRatesSearch> getSewerageMasters(final PropertyType propertyType, final String date, final String status){
        List<SewerageRatesSearch> sewerageMasterSearchRecords = new ArrayList<SewerageRatesSearch>();
        final List<SewerageRatesMaster> sewerageMasterRecords = searchConnectionRecordsBySearchParams(propertyType, date, status);
        for(SewerageRatesMaster sewerageMasterRecord : sewerageMasterRecords){
            SewerageRatesSearch swsearch = new SewerageRatesSearch();
            swsearch.setPropertyType(sewerageMasterRecord.getPropertyType().toString());
            swsearch.setMonthlyRate(sewerageMasterRecord.getMonthlyRate());
            swsearch.setFromDate(sewerageMasterRecord.getFromDate().toString());
            swsearch.setModifiedDate(sewerageMasterRecord.getLastModifiedDate().toString());
            swsearch.setId(sewerageMasterRecord.getId());
            swsearch.setActive(sewerageMasterRecord.isActive());
            sewerageMasterSearchRecords.add(swsearch);
            
            String todaysdate = myFormat.format(new Date());
            String effectiveFromDate=myFormat.format(sewerageMasterRecord.getFromDate());
            
            if(effectiveFromDate.compareTo(todaysdate)<0){
                swsearch.setEditable(false);
            }
            else{
                swsearch.setEditable(true);
            }
            
        }
      
        return sewerageMasterSearchRecords;
    } 
    
    //TODO : Clean code.. 
    public List<SewerageRatesMaster> searchConnectionRecordsBySearchParams (final PropertyType propertyType, final String date, final String status){
        
        final Criteria connectionCriteria = entityManager.unwrap(Session.class)
                .createCriteria(SewerageRatesMaster.class,"donation");
        
        if(null!=propertyType){
            connectionCriteria.add(Restrictions.eq("propertyType", propertyType));
        }
        if(null!=date){
            String formattedDate=null;
            Date fDate = null;
           try {
               formattedDate=formatter.format(myFormat.parse(date));
               fDate =formatter.parse(formattedDate);
               
           } catch (ParseException e) {

           }
            connectionCriteria.add(Restrictions.eq("fromDate",fDate));
        }
        if(null!=status){
            if(status.equals("ACTIVE")){
                boolean var = true;
                connectionCriteria.add(Restrictions.eq("active",var));
            }
            else{
                boolean var = false;
                connectionCriteria.add(Restrictions.eq("active", var));
            }
        }
        else{
            boolean a=true;
            connectionCriteria.add(Restrictions.eq("active",a));
        }
        connectionCriteria.addOrder(Order.desc("fromDate"));
        return connectionCriteria.list();
    }
}
