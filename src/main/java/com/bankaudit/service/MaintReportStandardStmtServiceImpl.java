package com.bankaudit.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankaudit.constants.BankAuditConstant;
import com.bankaudit.dao.MaintReportStandardStmtDao;
import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.MaintReportStandardStmt;
import com.bankaudit.model.MaintReportStandardStmtHst;
import com.bankaudit.model.MaintReportStandardStmtWrk;

@Service("maintReportStandardStmtService")
@Transactional("transactionManager")
public class MaintReportStandardStmtServiceImpl implements MaintReportStandardStmtService {

	@Autowired
	private MaintReportStandardStmtDao maintReportStandardStmtDao;


	@Override
	public void createMaintReportStandardStmt(MaintReportStandardStmt maintReportStandardStmt) throws Exception {
		boolean isMappingIdExists = checkMaintReportStandardStmtExsits(maintReportStandardStmt.getLegalEntityCode(),
				maintReportStandardStmt.getMappingId());
		if (!isMappingIdExists) {
			if (maintReportStandardStmt.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_AUTH)) {
				maintReportStandardStmtDao.save(maintReportStandardStmt);
			} else {
				MaintReportStandardStmtWrk maintReportStandardStmtWrk = new MaintReportStandardStmtWrk();
				BeanUtils.copyProperties(maintReportStandardStmt, maintReportStandardStmtWrk);
				maintReportStandardStmtDao.save(maintReportStandardStmtWrk);
			}
		} else {
			throw new Exception("Same Mapping Id is already exist");
		}
	}

	@Override
	public void updateMaintReportStandardStmt(MaintReportStandardStmt maintReportStandardStmt) {
		if (maintReportStandardStmt.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_AUTH)) {
			// get Db object and save into the history
			MaintReportStandardStmt maintReportStandardStmtdb = getMaintReportStandardStmt(maintReportStandardStmt.getLegalEntityCode(),
					maintReportStandardStmt.getMappingId(), BankAuditConstant.STATUS_MOD);
			if (maintReportStandardStmtdb != null) {
				MaintReportStandardStmtHst maintReportStandardStmtHst = new MaintReportStandardStmtHst();
				BeanUtils.copyProperties(maintReportStandardStmtdb, maintReportStandardStmtHst);
				maintReportStandardStmtDao.save(maintReportStandardStmtHst);
			}
			// delete the existing object from both the tables
			maintReportStandardStmtDao.deleteMaintReportStandardStmt(maintReportStandardStmt.getLegalEntityCode(),
					maintReportStandardStmt.getMappingId(), maintReportStandardStmt.getStatus());
			maintReportStandardStmtDao.deleteMaintReportStandardStmt(maintReportStandardStmt.getLegalEntityCode(),
					maintReportStandardStmt.getMappingId(), BankAuditConstant.STATUS_MOD);
			maintReportStandardStmtDao.flushSession();
			maintReportStandardStmt.setCheckerTimestamp(new Date());
			// save again
			maintReportStandardStmtDao.saveOrUpdate(maintReportStandardStmt);
		} else {
			if (maintReportStandardStmt.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_UNAUTH)
					|| maintReportStandardStmt.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_MOD)) {
				maintReportStandardStmt.setMakerTimestamp(new Date());
			}
			MaintReportStandardStmt maintReportStandardStmtDb = getMaintReportStandardStmt(maintReportStandardStmt.getLegalEntityCode(),
					maintReportStandardStmt.getMappingId(), maintReportStandardStmt.getStatus());
			if (maintReportStandardStmtDb != null) {
				maintReportStandardStmtDao.flushSession();
				MaintReportStandardStmtHst maintReportStandardStmtHst = new MaintReportStandardStmtHst();
				BeanUtils.copyProperties(maintReportStandardStmtDb, maintReportStandardStmtHst);
				maintReportStandardStmtDao.save(maintReportStandardStmtHst);
			} else {
				MaintReportStandardStmt maintReportStandardStmtmainDb = getMaintReportStandardStmt(maintReportStandardStmt.getLegalEntityCode(),
						maintReportStandardStmt.getMappingId(), BankAuditConstant.STATUS_AUTH);
				maintReportStandardStmtDao.flushSession();
				MaintReportStandardStmtHst maintReportStandardStmtHst = new MaintReportStandardStmtHst();
				BeanUtils.copyProperties(maintReportStandardStmtmainDb, maintReportStandardStmtHst);
				maintReportStandardStmtDao.save(maintReportStandardStmtHst);
			}
			maintReportStandardStmtDao.deleteMaintReportStandardStmt(maintReportStandardStmt.getLegalEntityCode(),
					maintReportStandardStmt.getMappingId(), maintReportStandardStmt.getStatus());
			MaintReportStandardStmtWrk maintReportStandardStmtWrk = new MaintReportStandardStmtWrk();
			BeanUtils.copyProperties(maintReportStandardStmt, maintReportStandardStmtWrk);
			maintReportStandardStmtDao.flushSession();
			maintReportStandardStmtDao.saveOrUpdate(maintReportStandardStmtWrk);
		}

	}

	@Override
	public MaintReportStandardStmt getMaintReportStandardStmt(Integer legalEntityCode, String mappingId, String status) {
		Map<String, Object> properties = new HashMap<>();
		properties.put("legalEntityCode", legalEntityCode);
		properties.put("mappingId", mappingId);
		if (BankAuditConstant.STATUS_AUTH.equalsIgnoreCase(status)) {
			return (MaintReportStandardStmt) maintReportStandardStmtDao.getUniqueEntityByMatchingProperties(MaintReportStandardStmt.class,
					properties);
		} else {
			MaintReportStandardStmt maintReportStandardStmt = null;
			MaintReportStandardStmtWrk maintReportStandardStmtWrk = (MaintReportStandardStmtWrk) maintReportStandardStmtDao
					.getUniqueEntityByMatchingProperties(MaintReportStandardStmtWrk.class, properties);
			if (maintReportStandardStmtWrk != null) {
				maintReportStandardStmt = new MaintReportStandardStmt();
				BeanUtils.copyProperties(maintReportStandardStmtWrk, maintReportStandardStmt);
			}
			return maintReportStandardStmt;
		}
	}

	@Override
	public DataTableResponse getAllMaintReportStandardStmt(Integer legalEntityCode, String search, Integer orderColumn, String orderDirection,
			Integer page, Integer size) {
		return maintReportStandardStmtDao.getAllMaintReportStandardStmt(legalEntityCode, search, orderColumn, orderDirection, page, size);
	}

	@Override
	public void deleteMaintReportStandardStmt(Integer legalEntityCode, String mappingId, String status) {
		maintReportStandardStmtDao.deleteMaintReportStandardStmt(legalEntityCode, mappingId, status);
	}

	@Override
	public boolean checkMaintReportStandardStmtExsits(Integer legalEntityCode, String mappingId) {
		Map<String, Object> properties = new HashMap<>();
		properties.put("legalEntityCode", legalEntityCode);
		properties.put("mappingId", mappingId);
		MaintReportStandardStmt maintReportStandardStmt = (MaintReportStandardStmt) maintReportStandardStmtDao
				.getUniqueEntityByMatchingProperties(MaintReportStandardStmt.class, properties);
		MaintReportStandardStmtWrk maintReportStandardStmtwrk = (MaintReportStandardStmtWrk) maintReportStandardStmtDao
				.getUniqueEntityByMatchingProperties(MaintReportStandardStmtWrk.class, properties);
		return !(maintReportStandardStmt == null && maintReportStandardStmtwrk == null); 
	}

	@Override
	public MaintReportStandardStmt getMaintReportStandardStmt(Integer legalEntityCode, String mappingId) {
		List<MaintReportStandardStmt> list = maintReportStandardStmtDao.getMaintReportStandardStmtByMappindId(legalEntityCode, mappingId);
		if(list!= null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	@Override
	public MaintReportStandardStmt getMaintReportStandardStmtWrk(Integer legalEntityCode, String mappingId) {
		List<MaintReportStandardStmt> list = maintReportStandardStmtDao.getMaintReportStandardStmtWrkByMappindId(legalEntityCode, mappingId);
		if(list!= null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

}
