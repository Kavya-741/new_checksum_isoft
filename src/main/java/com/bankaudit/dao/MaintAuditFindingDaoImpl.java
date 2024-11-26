package com.bankaudit.dao;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.bankaudit.constants.BankAuditConstant;


@Repository("maintAuditFindingDao")
public class MaintAuditFindingDaoImpl extends AbstractDao implements MaintAuditFindingDao {

	@Override
	public Boolean isMaintAuditFinding(Integer legalEntityCode, String findingCode) {
		Session session=getSession();
		Long count=(Long)session.createQuery(
				" select count(*) "
				+ " from MaintAuditFinding   "
				+ " where "
				+ " legalEntityCode =:legalEntityCode "
				+ " and entityStatus !='E'"
				+ " and findingCode =:findingCode  ")
		.setParameter("legalEntityCode", legalEntityCode)
		.setParameter("findingCode", findingCode).uniqueResult();
		
		Long count1=(Long)session.createQuery(
		
				
				" select count(*) "
				+ " from MaintAuditFindingWrk   "
				+ " where "
				+ " legalEntityCode =:legalEntityCode "
				+ " and findingCode =:findingCode  ")
		.setParameter("legalEntityCode", legalEntityCode)
		.setParameter("findingCode", findingCode).uniqueResult();
		Boolean flag=false;
		if((count1!=null && count1 >0)||(count!=null && count >0)){
			flag=true;
		}else {
			flag=false;
		}
		return flag;
	}

	@Override
	public void deleteMaintAuditFinding(Integer legalEntityCode, String findingId, String statusUnauth) {

		Session session=getSession();
		
		if(!BankAuditConstant.STATUS_AUTH.equals(statusUnauth)){
			
			session.createQuery("delete from MaintAuditFindingWrk  "
					+ " where legalEntityCode =:legalEntityCode "
					+ " and  findingId =:findingId  ").setParameter("findingId", findingId)
			.setParameter("legalEntityCode", legalEntityCode).executeUpdate();
			
		}else {
			session.createQuery("delete from MaintAuditFinding  "
					+ " where legalEntityCode =:legalEntityCode "
					+ " and  findingId =:findingId ").setParameter("findingId", findingId)
			.setParameter("legalEntityCode", legalEntityCode).executeUpdate() ;
		}
	}
}
