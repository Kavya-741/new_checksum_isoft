/*
 * 
 */
package com.bankaudit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankaudit.dao.SequenceAppenderDao;
import com.bankaudit.model.SequenceAppender;

/**
 * The Class {@link SequenceAppenderServiceImpl} Implements the business logic
 * method.
 *
 * @author amit.patel
 * @version 1.0
 */
@Service("sequenceAppenderService")
@Transactional("transactionManager")
public class SequenceAppenderServiceImpl  implements SequenceAppenderService{

	/**
	 * The sequence appender dao is autowired and make methods available from
	 * dao layer .
	 */
	@Autowired
	SequenceAppenderDao sequenceAppenderDao;
	
	@Override
	public String getAutoSequenceId() {
		SequenceAppender sequenceAppender=new SequenceAppender();
		sequenceAppenderDao.save(sequenceAppender);
		return sequenceAppender.getSequnecNo().toString();
	}

}
