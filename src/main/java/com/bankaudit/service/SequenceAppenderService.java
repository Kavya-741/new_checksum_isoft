/*
 * 
 */
package com.bankaudit.service;

/**
 * The Interface {@link SequenceAppenderService} define the methods that can be
 * exposed to controller layer in order to implements rest end-points and define
 * the business logic.
 *
 * @author amit.patel
 * @version 1.0
 */
public interface SequenceAppenderService {

	String getAutoSequenceId();
}
