package com.bankaudit.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.transform.ResultTransformer;
import org.jboss.logging.Logger;
import org.springframework.stereotype.Repository;

import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.MaintAuditTypeDesc;
import com.bankaudit.util.BankAuditUtil;

@Repository("maintAuditTypeDescDao")
public class MaintAuditTypeDescDaoImpl extends AbstractDao implements MaintAuditTypeDescDao{

	static final Logger logger = Logger.getLogger(MaintAuditTypeDescDaoImpl.class);
	
	@SuppressWarnings("deprecation")
	@Override
	public DataTableResponse getMaintAuditTypeDesc(Integer legalEntityCode, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size) {
		
		
	    Session session = getSession();
	    session.setDefaultReadOnly(true);
		List<MaintAuditTypeDesc> maintAuditTypeDescs = null;
		
		DataTableResponse dataTableResponse=new DataTableResponse();
		StringBuilder queryString=new StringBuilder("select "
				+ " at as maintAuditTypeDesc , "
				
				+ "(select concat(gp.value , '-' , gp.description)"
	  			  + " from GeneralParameter gp where "
	  			  + " gp.key1='STATUS' and gp.value=at.status "
	  			  + " and gp.legalEntityCode=at.legalEntityCode ) as status "
	  			  
				+ " from MaintAuditTypeDesc at "
				+ " where "
				+ " at.legalEntityCode =:legalEntityCode ");
		
		if(!BankAuditUtil.isEmptyString(search)){
			queryString=queryString.append(" and ( at.auditTypeCode like :search "
					+ " or  at.auditTypeDesc like :search  "
					+ " or  at.status IN (SELECT value FROM GeneralParameter  WHERE key1='STATUS' and  description LIKE :search ) "

					+ " or  at.maker like :search ) ");
		}
		
		String[] columns={
				"at.auditTypeCode",
				"at.auditTypeDesc",
				"at.maker",
				"at.status"};
		
		if(orderColumn!=null
				&& !BankAuditUtil.isEmptyString(orderDirection)){
			queryString=queryString.append(" order by ").append(columns[orderColumn]).append(" ").append(orderDirection);
		}
		
		@SuppressWarnings("deprecation")
		Query query=session.createQuery(queryString.toString())
				.setResultTransformer(new ResultTransformer() {
					
					@Override
					public Object transformTuple(Object[] tuple, String[] aliases) {
						
						MaintAuditTypeDesc maintAuditTypeDesc=null;
						
						if(tuple[0]!=null){
							maintAuditTypeDesc=(MaintAuditTypeDesc)tuple[0];
							
							if(tuple[1]!=null){
								maintAuditTypeDesc.setStatus(tuple[1].toString());
							}
						}
						
						return maintAuditTypeDesc;
					}
					
					@Override
					public List transformList(List collection) {
						return collection;
					}
				})
				.setParameter("legalEntityCode", legalEntityCode);
		
	
		
		if(!BankAuditUtil.isEmptyString(search)){
			query.setParameter("search", "%"+search+"%");
		}
		
		
		ScrollableResults resultScroll = query.scroll(ScrollMode.SCROLL_INSENSITIVE);
		if(resultScroll.first()&&resultScroll.scroll(page*size)){
			
			maintAuditTypeDescs= new ArrayList<>();
			int i = 0;
			while (size > i++) {
				maintAuditTypeDescs.add((MaintAuditTypeDesc) resultScroll.get(0));
			    if (!resultScroll.next())
			        break;
			}
			dataTableResponse.setData(maintAuditTypeDescs);
			resultScroll.last();
			if(size!=0){
				dataTableResponse.setRecordsFiltered((resultScroll.getRowNumber()+1l/size)+1);
				dataTableResponse.setRecordsTotal((resultScroll.getRowNumber()+1l/size)+1);
			}else{
				dataTableResponse.setError("page size zero");
				dataTableResponse.setData(Collections.EMPTY_LIST);
				dataTableResponse.setRecordsTotal(0l);
				dataTableResponse.setRecordsFiltered(0l);
			}
			
		}else{
			dataTableResponse.setError(null);
			dataTableResponse.setData(Collections.EMPTY_LIST);
			dataTableResponse.setRecordsTotal(0l);
			dataTableResponse.setRecordsFiltered(0l);
		}
		return dataTableResponse;

	
		
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public List<MaintAuditTypeDesc> getAuditTypeByLEAndUser(Integer legalEntityCode,String userId,String role, List<String> deptLst, boolean isDeptAsRMD)  {

		logger.info("Inside getAuditTypeByLEAndUser .. "+legalEntityCode +" userId.."+userId +" role.."+role );
		Session session=getSession();
		Boolean validUsr = false;
		List<MaintAuditTypeDesc> lst = null;
		try {
			
			StringBuilder query= new StringBuilder(" from MaintAuditTypeDesc "
					+ " where legalEntityCode =:legalEntityCode ");
			if(!isDeptAsRMD) {
				query.append( " AND auditTypeCode in (:deptLst) ");
			}
			Query hqlQuery=  session.createQuery(query.toString())
			.setParameter("legalEntityCode", legalEntityCode);
			if(!isDeptAsRMD) {
				hqlQuery.setParameter("deptLst", deptLst);
			}
			lst= hqlQuery.list();
			logger.info("lst .."+ lst);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return lst;
	}

}
