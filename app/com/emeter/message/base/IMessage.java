/*
 * Copyright (c) 2004, 2005, 2006, 2007, 2008, 2009, 2010.  
 * This is an unpublished work, is confidential and proprietary to 
 * eMeter Corporation as a trade secret and is not to be used or
 * disclosed except as may be provided in an applicable eMeter Corporation
 * license agreement.
 *
 * U.S. Government Rights.   The software is commercial computer software, and
 * the accompanying documentation is commercial computer software documentation.
 * The terms and conditions of eMeter Corporation's license agreement are fully
 * applicable to the Government's use and disclosure of the software and
 * documentation, and shall supersede any conflicting terms or conditions.
 * No license of any kind is granted in the case of acquisitions which contain
 * or are subject to the clauses FAR 52-227.19 COMMERCIAL COMPUTER
 * SOFTWARE-RESTRICTED RIGHTS (JUNE 1987) or DFARS 252.227-7013 RIGHTS IN
 * TECHNICAL DATA AND COMPUTER SOFTWARE (OCT 1988) or any other clause which
 * purports to grant to the Government rights greater than, or additional to
 * those, set forth in such license agreement, or which purports to impose
 * additional requirements upon eMeter.  If the license agreement fails to meet
 * the Government's stated needs or is inconsistent in any respect with federal
 * law, the Government agrees to return the software and documentation, unused,
 * to eMeter.  The Licensor/Manufacturer is eMeter Corporation, One Twin Dolphin
 * Drive, Redwood City, CA 94065.
 */

package com.emeter.message.base;


import com.emeter.a2f.kafka.message.A2FKafkaMessage;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

/**
 * Base interface for all message beans
 * 
 * @author shivka
 *
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = As.PROPERTY, property = "type")
@JsonSubTypes({ @Type(value = A2FKafkaMessage.class, name = "a2fKafkaMessage") })
public interface IMessage {

}
